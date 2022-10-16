package io.github.tobiasz.reactiveserver.response;

import io.github.tobiasz.reactiveserver.request.ServerRequest;

public record ResponseDto(ServerRequest serverRequest, Object result) {

}
