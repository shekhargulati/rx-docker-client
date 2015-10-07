package io.reactivex.docker.client;

import okio.Buffer;
import rx.Observable;

public interface ImageOperations {

    String IMAGE_ENDPOINT = "images";
    String IMAGE_CREATE_ENDPOINT = IMAGE_ENDPOINT + "/create";

    Observable<Buffer> pullImageObs(String fromImage);

    HttpStatus pullImage(String fromImage);
}
