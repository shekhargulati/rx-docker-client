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

package io.reactivex.docker.client;

import io.reactivex.docker.client.http_client.HttpStatus;
import io.reactivex.docker.client.representations.DockerImage;
import io.reactivex.docker.client.representations.DockerImageHistory;
import io.reactivex.docker.client.representations.DockerImageInfo;
import io.reactivex.docker.client.representations.DockerImageInspectDetails;
import okio.Buffer;
import rx.Observable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ImageOperations {

    String IMAGE_ENDPOINT = "images";
    String IMAGE_BUILD_ENDPOINT = "build";
    String IMAGE_CREATE_ENDPOINT = IMAGE_ENDPOINT + "/create?fromImage=%s%s&tag=%s";
    String IMAGE_LIST_ENDPOINT = IMAGE_ENDPOINT + "/json";
    String IMAGE_REMOVE_ENDPOINT = IMAGE_ENDPOINT + "/%s";
    String IMAGE_SEARCH_ENDPOINT = IMAGE_ENDPOINT + "/search";
    String IMAGE_TAG_ENDPOINT = IMAGE_ENDPOINT + "/%s/tag";
    String IMAGE_HISTORY_ENDPOINT = IMAGE_ENDPOINT + "/%s/history";
    String IMAGE_INSPECT_ENDPOINT = IMAGE_ENDPOINT + "/%s/json";
    String IMAGE_PUSH_ENDPOINT = IMAGE_ENDPOINT + "/%s/push";

    Observable<Buffer> pullImageObs(String image, final Optional<String> user, final Optional<String> tag);

    HttpStatus pullImage(String fromImage, String user, String tag);

    HttpStatus pullImage(String fromImage, String tag);

    HttpStatus pullImage(String fromImage);

    Stream<DockerImage> listImages(ImageListQueryParameters queryParameters);

    Observable<DockerImage> listImagesObs(ImageListQueryParameters queryParameters);

    Stream<DockerImage> listAllImages();

    Stream<DockerImage> listImages(String imageName);

    Stream<DockerImage> listImages();

    Stream<DockerImage> listDanglingImages();

    Observable<HttpStatus> removeImageObs(String imageName);

    Observable<HttpStatus> removeImageObs(String imageName, boolean noPrune, boolean force);

    HttpStatus removeImage(String imageName, boolean noPrune, boolean force);

    HttpStatus removeImage(String imageName);

    default void removeAllImages() {
        removeImages(d -> true);
    }

    default void removeImages(Predicate<DockerImage> predicate) {
        listAllImages().filter(predicate).forEach(image -> {
            System.out.println(String.format("Deleting image with tag %s", image.repoTags()));
            removeImage(image.id(), false, true);
        });
    }

    default void removeDanglingImages() {
        listDanglingImages().forEach(image -> {
            System.out.println(String.format("Deleting dangling image with id %s", image.id()));
            removeImage(image.id(), false, true);
        });
    }

    default Stream<DockerImageInfo> searchImages(String searchTerm) {
        return searchImages(searchTerm, t -> true);
    }

    Stream<DockerImageInfo> searchImages(String searchTerm, Predicate<DockerImageInfo> predicate);

    default Observable<DockerImageInfo> searchImagesObs(String searchTerm) {
        return searchImagesObs(searchTerm, t -> true);
    }

    Observable<DockerImageInfo> searchImagesObs(String searchTerm, Predicate<DockerImageInfo> predicate);

    public Observable<String> buildImageObs(final String repositoryName, final Path pathToTarArchive, BuildImageQueryParameters queryParameters);

    Observable<HttpStatus> tagImageObs(String image, ImageTagQueryParameters queryParameters);

    HttpStatus tagImage(String image, ImageTagQueryParameters queryParameters);

    Stream<DockerImageHistory> imageHistory(String image);

    Observable<DockerImageHistory> imageHistoryObs(String image);

    DockerImageInspectDetails inspectImage(String image);

    Observable<DockerImageInspectDetails> inspectImageObs(String image);

    HttpStatus pushImage(String image, AuthConfig authConfig);

    Observable<String> pushImageObs(String image, AuthConfig authConfig);

    Observable<String> buildImageObs(String repositoryName, Path pathToTarArchive);

    Observable<String> buildImageObs(String repositoryName, BuildImageQueryParameters queryParameters);
}
