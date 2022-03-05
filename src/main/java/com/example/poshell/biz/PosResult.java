package com.example.poshell.biz;

public class PosResult <T> {

    private PosStatus status;

    private String message;

    private T body;

    public PosResult (PosStatus status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public static Builder ok() {
        return new DefaultBuilder(PosStatus.OK);
    }

    public static Builder error() {
        return new DefaultBuilder(PosStatus.NOT_OK);
    }

    public PosStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }

    public interface Builder {
        public <T> PosResult<T> message(String message);

        public <T> PosResult<T> build();

        public <T> PosResult<T> body(T object);
    }

    private static class DefaultBuilder implements Builder {

        private PosStatus status;

        public DefaultBuilder(PosStatus status) {
            this.status = status;
        }

        public <T> PosResult<T> message(String message) {
            return new PosResult<>(status, message, null);
        }

        public <T> PosResult<T> build() {
            return body(null);
        }

        public <T> PosResult<T> body(T object) {
            return new PosResult<>(status, "", object);
        }
    }
}
