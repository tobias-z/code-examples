package io.github.tobiasz.reactiveserver.request;

public class ServerRequestBuilder {

    public static ServerRequest buildRequestFromString(String request) {
        ServerRequest serverRequest = new ServerRequest();
        String[] lines = request.split("\n");
        int i = buildFromHeaders(serverRequest, lines);
        serverRequest.setData(getRequestData(i, lines));
        return serverRequest;
    }

    private static int buildFromHeaders(ServerRequest serverRequest, String[] lines) {
        // first line is always the requested resource such as 'GET /something HTTP/1.1
        String[] requestedResource = lines[0].split(" ");
        serverRequest.setRequestMethod(RequestMethod.valueOf(requestedResource[0]));
        setPathAndQueryParams(serverRequest, requestedResource[1]);

        int i = 1;
        String currLine = lines[i];
        while (currLine.length() != 1) {
            int endIndex = currLine.indexOf(":");
            serverRequest.getHeaders().put(currLine.substring(0, endIndex), currLine.substring(endIndex + 2));
            currLine = lines[++i];
        }

        // I have no idea why these are unable to be concatenated?????
        serverRequest.setUrl(serverRequest.getHeaders().get("Host") + serverRequest.getRequestPath());
        return i + 1;
    }

    private static void setPathAndQueryParams(ServerRequest serverRequest, String fullPath) {
        if (!fullPath.contains("?")) {
            serverRequest.setRequestPath(fullPath);
            return;
        }
        int queryIndex = fullPath.indexOf("?");
        String path = fullPath.substring(0, queryIndex);
        serverRequest.setRequestPath(path);
        String queries = fullPath.substring(queryIndex + 1);
        if (queries.length() != 0) {
            for (String query : queries.split("&")) {
                int beginIndex = query.indexOf("=");
                String key = query.substring(0, beginIndex);
                String value = queries.substring(beginIndex + 1);
                serverRequest.getQueryParams().put(key, value);
            }
        }
    }

    private static String getRequestData(int i, String[] lines) {
        StringBuilder dataBuilder = new StringBuilder();
        for (int j = i; j < lines.length; j++) {
            dataBuilder.append(lines[j]);
            dataBuilder.append('\n');
        }
        return dataBuilder.toString();
    }
}
