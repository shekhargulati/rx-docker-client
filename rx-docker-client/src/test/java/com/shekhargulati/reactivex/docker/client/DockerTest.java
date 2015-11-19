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

import com.shekhargulati.reactivex.docker.client.representations.*;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class DockerTest {

    public static final String CONTAINER_NAME = "my_first_container";
    public static final String SECOND_CONTAINER_NAME = "my_second_container";

    private DockerClient client = DockerClient.fromDefaultEnv();


    @After
    public void tearDownInfra() throws Exception {
        client.removeAllContainers();
        assertThat(client.listAllContainers().size(), equalTo(0));
        client.removeDanglingImages();
        client.removeImages(dockerImage -> dockerImage.repoTags().stream().anyMatch(repo -> repo.contains("test_rx_docker")));
    }

    @Test
    public void shouldFetchVersionInformationFromDocker() throws Exception {
        DockerVersion dockerVersion = client.serverVersion();
        assertThat(dockerVersion.version(), containsString("1.8"));
        assertThat(dockerVersion.apiVersion(), is(equalTo("1.20")));
    }

    @Test
    public void shouldFetchDockerInformation() throws Exception {
        DockerInfo info = client.info();
        assertThat(info.dockerRootDir(), equalTo("/mnt/sda1/var/lib/docker"));
        assertThat(info.initPath(), equalTo("/usr/local/bin/docker"));
    }

    @Test
    public void shouldCreateContainer() throws Exception {
        client.pullImage("ubuntu");
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldCreateContainerWithName() throws Exception {
        client.pullImage("ubuntu");
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldListAllContainers() throws Exception {
        createContainer(CONTAINER_NAME);
        createContainer(SECOND_CONTAINER_NAME);
        List<DockerContainer> dockerContainers = client.listAllContainers();
        dockerContainers.forEach(container -> System.out.println("Docker Container >> \n " + container));
        assertThat(dockerContainers, hasSize(greaterThanOrEqualTo(2)));
    }

    private DockerContainerResponse createContainer(String containerName) {
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Arrays.asList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }


}
