package io.reactivex.docker.client;

public class RestServiceCommunicationException extends RuntimeException {

    private int code;
    private String httpMessage;

    public RestServiceCommunicationException(final String errorMessage, final int code, final String httpMessage) {
        super(errorMessage);
        this.code = code;
        this.httpMessage = httpMessage;
    }

    public RestServiceCommunicationException(Exception e) {
        super(e);
    }

    public int getCode() {
        return code;
    }

    public String getHttpMessage() {
        return httpMessage;
    }
}
