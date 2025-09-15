package org.devbid.domain.common;

import lombok.Getter;

@Getter
public class Result<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;

    public Result(boolean success, T data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(false, null,errorMessage);
    }


}
