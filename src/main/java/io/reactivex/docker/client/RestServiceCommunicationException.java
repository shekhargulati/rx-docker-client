package io.reactivex.docker.client;

public class RestServiceCommunicationException extends RuntimeException {
    public RestServiceCommunicationException(Exception e) {
        super(e);
    }
}
