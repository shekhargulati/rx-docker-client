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

import com.shekhargulati.reactivex.docker.client.http_client.HttpStatus;
import com.shekhargulati.reactivex.docker.client.junit.CreateDockerContainer;
import com.shekhargulati.reactivex.docker.client.junit.DockerContainerRule;
import com.shekhargulati.reactivex.docker.client.representations.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class DockerTest {

    public static final String CONTAINER_NAME = "my_first_container";
    public static final String SECOND_CONTAINER_NAME = "my_second_container";

    private static DockerClient client = DockerClient.fromDefaultEnv();

    @BeforeClass
    public static void init() throws Exception {
        client.pullImage("ubuntu");
    }

    @Rule
    public DockerContainerRule containerRule = new DockerContainerRule(client);

    @Test
    public void shouldFetchVersionInformationFromDocker() throws Exception {
        DockerVersion dockerVersion = client.serverVersion();
        assertThat(dockerVersion.version(), containsString("1.8"));
        assertThat(dockerVersion.apiVersion(), is(equalTo("1.20")));
    }

    @Test
    public void shouldCreateContainer() throws Exception {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Collections.singletonList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        String containerId = response.getId();
        assertThat(containerId, notNullValue());
        removeContainer(containerId);
    }

    @Test
    public void shouldCreateContainerWithName() throws Exception {
        DockerContainerResponse response = createContainer("shouldCreateContainerWithName");
        String containerId = response.getId();
        assertThat(containerId, notNullValue());
        removeContainer(containerId);
    }

    @Test
    @CreateDockerContainer(containers = {CONTAINER_NAME, SECOND_CONTAINER_NAME})
    public void shouldListAllContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listAllContainers();
        dockerContainers.forEach(container -> System.out.println("Docker Container >> \n " + container));
        assertThat(dockerContainers, hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldInspectContainer() throws Exception {
        ContainerInspectResponse containerInspectResponse = client.inspectContainer(containerRule.containerIds().get(0));
        assertThat(containerInspectResponse.path(), is(equalTo("/bin/bash")));
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldStartCreatedContainer() throws Exception {
        HttpStatus httpStatus = client.startContainer(containerRule.containerIds().get(0));
        assertThat(httpStatus.code(), is(equalTo(204)));
    }

    @Test
    public void shouldStartContainerWithAllExposedPortsPublished() throws Exception {
        DockerContainerResponse response = createContainerWithPublishAllPorts("shouldStartContainerWithAllExposedPortsPublished", "9999/tcp");
        HttpStatus httpStatus = client.startContainer(response.getId());
        assertThat(httpStatus.code(), is(equalTo(204)));
        removeContainer(response.getId());
    }

    @Test
    public void shouldStartContainerWithExposedPortsAndHostPortsPublished() throws Exception {
        DockerContainerResponse response = createContainerWithExposedAndHostPorts("shouldStartContainerWithExposedPortsAndHostPortsPublished", new String[]{"9999/tcp"}, new String[]{"9999/tcp"});
        HttpStatus httpStatus = client.startContainer(response.getId());
        assertThat(httpStatus.code(), is(equalTo(204)));
        removeContainer(response.getId());
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldStopStartedContainer() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        HttpStatus status = client.stopContainer(containerId, 5);
        assertThat(status.code(), is(equalTo(204)));
    }


    @Test
    @CreateDockerContainer(containers = {CONTAINER_NAME, SECOND_CONTAINER_NAME})
    public void shouldQueryContainersByFilters() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers.size(), greaterThanOrEqualTo(2));
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldRestartAContainer() throws Exception {
        HttpStatus status = client.restartContainer(containerRule.containerIds().get(0), 5);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldKillARunningContainer() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        HttpStatus status = client.killRunningContainer(containerId);
        assertThat(status.code(), is(equalTo(204)));
    }

    private DockerContainerResponse createContainer(String containerName) {
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Collections.singletonList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }

    private void removeContainer(String containerId) {
        try {
            client.removeContainer(containerId, true, true);
        } catch (Exception e) {
            // ignore as circle ci does not allow containers and images to be destroyed
        }
    }

    private DockerContainerResponse createContainerWithPublishAllPorts(String containerName, String... ports) {
        final HostConfig hostConfig = new HostConfigBuilder().setPublishAllPorts(true).createHostConfig();
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Collections.singletonList("/bin/bash"))
                .setAttachStdin(true)
                .addExposedPort(ports)
                .setHostConfig(hostConfig)
                .setTty(true)
                .createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }

    private DockerContainerResponse createContainerWithExposedAndHostPorts(String containerName, String[] exposedPorts, String[] hostPorts) {
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String hostPort : hostPorts) {
            List<PortBinding> hostPortBinding = new ArrayList<>();
            hostPortBinding.add(PortBinding.of("0.0.0.0", hostPort));
            portBindings.put(hostPort, hostPortBinding);
        }
        final HostConfig hostConfig = new HostConfigBuilder().setPortBindings(portBindings).createHostConfig();
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Collections.singletonList("/bin/bash"))
                .setAttachStdin(true)
                .addExposedPort(exposedPorts)
                .setHostConfig(hostConfig)
                .setTty(true)
                .createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }


}
