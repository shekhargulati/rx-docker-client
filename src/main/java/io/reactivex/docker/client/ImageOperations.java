package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.DockerImage;
import okio.Buffer;
import rx.Observable;

import java.util.Optional;
import java.util.stream.Stream;

public interface ImageOperations {

    String IMAGE_ENDPOINT = "images";
    String IMAGE_CREATE_ENDPOINT = IMAGE_ENDPOINT + "/create?fromImage=%s%s&tag=%s";
    String IMAGE_LIST_ENDPOINT = IMAGE_ENDPOINT + "/json";

    Observable<Buffer> pullImageObs(String image, final Optional<String> user, final Optional<String> tag);

    HttpStatus pullImage(String fromImage, String user, String tag);

    HttpStatus pullImage(String fromImage, String tag);

    HttpStatus pullImage(String fromImage);

    Stream<DockerImage> listImages(ImageListQueryParameters queryParameters);

    Observable<DockerImage> listImagesObs(ImageListQueryParameters queryParameters);

    Stream<DockerImage> listAllImages();

    Stream<DockerImage> listImages(String imageName);

    Stream<DockerImage> listImages();
}
