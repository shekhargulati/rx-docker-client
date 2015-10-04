package io.reactivex.docker.client;

public class HttpStatus {

    private final int code;
    private final String message;

    public HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
