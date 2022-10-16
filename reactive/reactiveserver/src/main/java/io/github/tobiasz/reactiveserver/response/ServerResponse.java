package io.github.tobiasz.reactiveserver.response;

import io.github.tobiasz.reactiveserver.request.ServerRequest;

public class ServerResponse {

    public static String buildFromResponseDto(ResponseDto responseDto) {
        StringBuilder responseBuilder = new StringBuilder();
        ServerRequest serverRequest = responseDto.serverRequest();
        append(responseBuilder, "HTTP/1.0 200 OK");
        buildHeaders(serverRequest, responseBuilder);
        ResponseSerializer.serializeToString(responseDto.result())
            .ifPresent(data -> {
                responseBuilder.append("\r\n");
                responseBuilder.append(data);
            });
        return responseBuilder.toString();
    }

    private static void buildHeaders(ServerRequest serverRequest, StringBuilder responseBuilder) {
        append(responseBuilder, "Content-Type: application/json");
        append(responseBuilder, "Connection: close");
    }

    private static void append(StringBuilder responseBuilder, String line) {
        responseBuilder.append(line);
        responseBuilder.append("\r\n");
    }
}
