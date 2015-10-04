package io.reactivex.docker.client;

public class RestServiceCommunicationException extends RuntimeException {

    public RestServiceCommunicationException(String message) {
        super(message);
    }

    public RestServiceCommunicationException(Exception e) {
        super(e);
    }
}
