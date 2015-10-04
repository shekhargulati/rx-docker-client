package io.reactivex.docker.client;

public class HttpStatus {

    private final int code;
    private final String message;

    public HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
