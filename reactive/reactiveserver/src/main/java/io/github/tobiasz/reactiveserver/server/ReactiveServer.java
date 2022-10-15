package io.github.tobiasz.reactiveserver.server;

import io.github.tobiasz.reactiveserver.api.ReactiveEndpointBuilder;
import io.github.tobiasz.reactiveserver.api.ReactiveEndpointBuilder.CreatedEndpoint;
import io.github.tobiasz.reactiveserver.request.ServerRequest;
import io.github.tobiasz.reactiveserver.request.ServerRequestBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveServer implements AutoCloseable {

    private final String host;
    private final int port;
    private final ReactiveEndpointBuilder reactiveEndpointBuilder;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public void start() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.socket().bind(new InetSocketAddress(this.host, this.port));
        this.selector = Selector.open();
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        this.handleConnections();
    }

    @Override
    public void close() throws Exception {
        this.selector.close();
        this.serverSocketChannel.close();
        this.serverSocketChannel.socket().close();
    }

    private void handleConnections() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            this.selector.select();
            Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    this.accept(key);
                }

                if (key.isReadable()) {
                    this.read(key);
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        int read = channel.read(buffer);

        boolean isRequestWithData = read != -1;
        if (!isRequestWithData) {
            // this might be things such as telnet clients or socket connections.
            // Http requests will always at least send some headers
            return;
        }

        buffer.flip();
        byte[] storage = new byte[1000];
        buffer.get(storage, 0, read);
        String request = new String(storage).replaceAll("\u0000.*", "");
        ServerRequest serverRequest = ServerRequestBuilder.buildRequestFromString(request);

        // TODO: Wrap in subscribe
        Optional<CreatedEndpoint> endpoint = this.getEndpoint(serverRequest);
        if (endpoint.isPresent()) {
            Object result = endpoint.get().getReactiveEndpoint().onPublish(serverRequest);
            // wrapper around the data to tell which status code we want to send back among others
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private Optional<CreatedEndpoint> getEndpoint(ServerRequest serverRequest) {
        return this.reactiveEndpointBuilder.getCreatedEndpointList()
            .stream()
            .filter(createdEndpoint -> {
                boolean isSamePath = createdEndpoint.getPath().equals(serverRequest.getRequestPath());
                boolean isSameRequestMethod = createdEndpoint.getRequestMethod().equals(serverRequest.getRequestMethod());
                return isSamePath && isSameRequestMethod;
            })
            .findFirst();
    }

    private void accept(SelectionKey key) throws IOException {
        System.out.println("accepting");
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

}
