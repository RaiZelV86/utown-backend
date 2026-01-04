package com.utown.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T>{

    private boolean success;
    private T data;
    private String message;
    private ErrorDetails error;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message, String errorCode) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .message(message)
                        .build())
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message, String errorCode, Map<String, String> details) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private Map<String, String> details;
    }
}
