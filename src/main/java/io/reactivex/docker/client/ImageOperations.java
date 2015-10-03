package io.reactivex.docker.client;

import io.netty.handler.codec.http.HttpResponseStatus;

public interface ImageOperations {
    HttpResponseStatus pullImage(String fromImage);
}
