package io.reactivex.docker.client.http_client;

public class HttpStatus {

    private final int code;
    private final String message;
    public static final HttpStatus OK = of(200, "Ok");
    public static final HttpStatus NO_CONTENT = of(204, "Ok");
    public static final HttpStatus NOT_FOUND = of(404, "Ok");
    public static final HttpStatus SERVER_ERROR = of(500, "Ok");
    public static final HttpStatus BAD_REQUEST = of(400, "Ok");

    private HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus of(final int code, final String message) {
        return new HttpStatus(code, message);
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
