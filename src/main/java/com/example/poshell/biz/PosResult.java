package com.example.poshell.biz;

// PosService 统一返回给 ShellCommand 一个 PosResult 对象，
// 如果操作成功，则包含成功的结果；操作失败则包含错误的信息
public class PosResult <T> {

    private PosStatus status;

    // 错误信息
    private String message;

    // 成功的结果
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

    // 这个类使得我们可以编写 PosResult.ok.body() 之类的代码
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

        // 返回一个 body 为 null 的对象
        public <T> PosResult<T> build() {
            return body(null);
        }

        public <T> PosResult<T> body(T object) {
            return new PosResult<>(status, "", object);
        }
    }
}
