package io.reactivex.docker.client;

import rx.Observable;

public interface ImageOperations {
    Observable<HttpStatus> pullImageObs(String fromImage);
    HttpStatus pullImage(String fromImage);
}
