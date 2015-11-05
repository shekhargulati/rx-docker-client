/*
 * The MIT License
 *
 * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.shekhar.reactivex.docker.client.http_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

public class HttpStatusSubscriber extends Subscriber<String> {

    private final Logger logger = LoggerFactory.getLogger(HttpStatusSubscriber.class);

    private HttpStatus status = null;

    @Override
    public void onCompleted() {
        logger.info("Successfully processed all events");
        status = HttpStatus.OK;
    }

    @Override
    public void onError(Throwable e) {
        logger.error("Error encountered >> ", e);
        if (e instanceof ServiceException) {
            status = HttpStatus.of(((ServiceException) e).getCode(), ((ServiceException) e).getHttpMessage());
        } else {
            status = HttpStatus.SERVER_ERROR;
        }
    }

    @Override
    public void onNext(String res) {
        logger.info("Received message >> {}", res);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
