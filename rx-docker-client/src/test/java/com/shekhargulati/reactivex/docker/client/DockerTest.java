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

package com.shekhargulati.reactivex.docker.client;

import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequest;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequestBuilder;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerResponse;
import com.shekhargulati.reactivex.docker.client.representations.DockerVersion;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DockerTest {

    private DockerClient client = DockerClient.fromDefaultEnv();

    @Test
    public void dockerHostEnvironmentVariableShouldBeFound() throws Exception {
        String dockerHost = System.getenv("DOCKER_HOST");
        System.out.println(String.format("Docker host >> %s", dockerHost));
        System.out.println(client.getApiUri());
        assertThat(client.getApiUri(), equalTo("http://127.0.0.1:2375"));
        DockerVersion dockerVersion = client.serverVersion();
        System.out.println(dockerVersion);
    }

    @Test
    public void shouldCreateContainer() throws Exception {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        assertThat(response.getId(), notNullValue());
    }
}
