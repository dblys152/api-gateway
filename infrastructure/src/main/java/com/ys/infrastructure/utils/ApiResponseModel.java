package com.ys.infrastructure.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseModel<T> {
    int status;
    T data;
    Pagination pagination;
    String errorMessage;

    public static <T> ApiResponseModel<T> success(int status, T data) {
        return new ApiResponseModel<>(status, data, null, null);
    }

    public static <T> ApiResponseModel<T> success(int status, T data, Pagination pagination) {
        return new ApiResponseModel<>(status, data, pagination, null);
    }

    public static <T> ApiResponseModel<T> error(int status, String errorMessage) {
        return new ApiResponseModel<>(status, null, null, errorMessage);
    }
}
