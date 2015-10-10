package io.reactivex.docker.client;

import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

import java.nio.charset.Charset;

public class HttpStatusBufferSubscriber extends Subscriber<Buffer> {

    private final Logger logger = LoggerFactory.getLogger(HttpStatusBufferSubscriber.class);

    private HttpStatus status = null;

    @Override
    public void onCompleted() {
        logger.info("Successfully processed all events");
        status = HttpStatus.NO_CONTENT;
    }

    @Override
    public void onError(Throwable e) {
        logger.error("Error encountered >> ", e);
        if (e instanceof RestServiceCommunicationException) {
            status = HttpStatus.of(((RestServiceCommunicationException) e).getCode(), ((RestServiceCommunicationException) e).getHttpMessage());
        } else {
            status = HttpStatus.SERVER_ERROR;
        }
    }

    @Override
    public void onNext(Buffer res) {
        logger.info("Received message >> {}", res.readString(Charset.defaultCharset()));
    }

    public HttpStatus getStatus() {
        return status;
    }
}
