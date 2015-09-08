package io.reactivex.rxdockerclient;

public class DockerCertificateException extends RuntimeException {

    public DockerCertificateException(final String message) {
        super(message);
    }

    public DockerCertificateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DockerCertificateException(final Throwable cause) {
        super(cause);
    }
}
