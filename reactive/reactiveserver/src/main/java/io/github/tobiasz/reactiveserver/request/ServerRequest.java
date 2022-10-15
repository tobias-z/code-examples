package io.github.tobiasz.reactiveserver.request;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServerRequest {

    private String url;
    private String requestPath;
    private String data;
    private RequestMethod requestMethod;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();

}
