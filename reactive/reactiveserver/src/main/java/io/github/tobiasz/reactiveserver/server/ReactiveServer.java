package io.github.tobiasz.reactiveserver.server;

import io.github.tobiasz.reactiveserver.core.publisher.Mono;
import io.github.tobiasz.reactiveserver.request.ServerRequest;
import io.github.tobiasz.reactiveserver.request.ServerRequestBuilder;
import io.github.tobiasz.reactiveserver.response.ResponseDto;
import io.github.tobiasz.reactiveserver.response.ServerResponse;
import io.github.tobiasz.reactiveserver.server.ReactiveEndpointBuilder.CreatedEndpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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
                SelectionKey selectionKey = keys.next();
                keys.remove();

                if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                    this.accept(selectionKey);
                }

                if (selectionKey.isValid() && selectionKey.isReadable()) {
                    this.respondToRequest(selectionKey);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void respondToRequest(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (!channel.isOpen()) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();

        Mono.just(channel.read(buffer))
            .map(read -> {
                try {
                    boolean isRequestWithData = read != -1;
                    if (!isRequestWithData) {
                        // this might be things such as telnet clients or socket connections.
                        // Http requests will always at least send some headers
                        key.cancel();
                        channel.close();
                        return null;
                    }
                    return read;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(read -> {
                buffer.flip();
                byte[] storage = new byte[1000];
                buffer.get(storage, 0, read);
                return new String(storage).replaceAll("\u0000.*", "");
            })
            .map(ServerRequestBuilder::buildRequestFromString)
            .map(serverRequest -> {
                Optional<CreatedEndpoint> endpoint = this.getEndpoint(serverRequest);
                return endpoint
                    .map(createdEndpoint -> {
                        Object result = endpoint.get().getReactiveEndpoint().onPublish(serverRequest);
                        return new ResponseDto(serverRequest, result);
                    })
                    .orElse(new ResponseDto(serverRequest, null));
            })
            .subscribe(responseDto -> {
                try {
                    if (channel.isOpen()) {
                        String response = ServerResponse.buildFromResponseDto(responseDto);
                        channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .onComplete(unused -> {
                try {
                    if (channel.isOpen()) {
                        key.cancel();
                        channel.close();
                        channel.socket().close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("unable to close the channel connection");
                }
            })
            .build();
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

}
