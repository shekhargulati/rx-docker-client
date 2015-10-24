package io.reactivex.docker.client.http_client;

public class HttpStatus {

    private final int code;
    private final String message;
    public static final HttpStatus OK = of(200, "OK");
    public static final HttpStatus NO_CONTENT = of(204, "No Content");
    public static final HttpStatus NOT_FOUND = of(404, "Not Found");
    public static final HttpStatus SERVER_ERROR = of(500, "Server Error");
    public static final HttpStatus BAD_REQUEST = of(400, "Bad Request");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpStatus that = (HttpStatus) o;

        if (code != that.code) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
