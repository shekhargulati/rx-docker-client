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

package com.shekhargulati.reactivex.docker.client.examples;

import com.shekhargulati.reactivex.docker.client.DockerClient;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequest;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequestBuilder;
import rx.Observable;

import java.nio.file.Paths;
import java.util.Arrays;

public class RxDockerClientExamples {

    public static void main(String[] args) {
        DockerClient client = DockerClient.fromDefaultEnv();

        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu:latest")
                .setCmd(Arrays.asList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();

        String container = "my_first_container";
        client.createContainerObs(request, container)
                .flatMap(res -> client.startContainerObs(res.getId()))
                .subscribe(System.out::println,
                        e -> System.err.println("Encountered exception >> " + e.getMessage()),
                        () -> System.out.println("Successfully completed"));


        Observable<String> buildImageObs = client.buildImageObs("shekhargulati/my_hello_world_image",
                Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
        buildImageObs.subscribe(System.out::println);
    }
}
