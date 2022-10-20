package io.github.tobiasz.integration.dto;

public record CreatedDto<ID>(ID id) {

    public static <T> CreatedDto<T> toCreated(T id) {
        return new CreatedDto<>(id);
    }

}
