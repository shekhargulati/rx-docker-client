package io.reactivex.docker.client;

import okio.Buffer;
import rx.Observable;

import java.util.Optional;

public interface ImageOperations {

    String IMAGE_ENDPOINT = "images";
    String IMAGE_CREATE_ENDPOINT = IMAGE_ENDPOINT + "/create?fromImage=%s%s&tag=%s";

    Observable<Buffer> pullImageObs(String image, final Optional<String> user, final Optional<String> tag);

    HttpStatus pullImage(String fromImage, String user, String tag);

    HttpStatus pullImage(String fromImage, String tag);

    HttpStatus pullImage(String fromImage);
}
