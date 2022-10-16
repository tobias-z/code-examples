package io.github.tobiasz.reactiveserver.response;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.util.Optional;

public class ResponseSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ByteBuffer serializeToBytes(Object obj) {
        try {
            if (isNull(obj)) {
                return null;
            }
            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(obj);
            return ByteBuffer.wrap(bytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> serializeToString(Object obj) {
        try {
            if (isNull(obj)) {
                return Optional.empty();
            }
            return Optional.ofNullable(OBJECT_MAPPER.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
