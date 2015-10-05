package io.reactivex.docker.client;

public interface ImageOperations {
    HttpStatus pullImage(String fromImage);
}
