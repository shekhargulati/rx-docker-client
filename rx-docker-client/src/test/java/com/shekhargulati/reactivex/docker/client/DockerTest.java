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
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

public class DockerTest {

    private final Logger logger = LoggerFactory.getLogger(RxDockerClientTest.class);

    public static final String CONTAINER_NAME = "my_first_container";
    public static final String SECOND_CONTAINER_NAME = "my_second_container";

    private static DockerClient client = DockerClient.fromDefaultEnv();

    @BeforeClass
    public static void init() throws Exception {
        client.pullImage("ubuntu");
    }

    @Rule
    public DockerContainerRule containerRule = new DockerContainerRule(client);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

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

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldRemoveDockerContainerWithQueryParameters() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        HttpStatus status = null;
        try {
            status = client.removeContainer(containerId, false, true);
            assertThat(status.code(), is(equalTo(204)));
        } catch (Exception e) {
            // ignoring for Circle CI
        }
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldRenameDockerContainer() throws Exception {
        HttpStatus status = client.renameContainer(containerRule.containerIds().get(0), "my_first_container-renamed");
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldWaitForARunningDockerContainer() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        rx.Observable.timer(1, TimeUnit.SECONDS).forEach(t -> {
            System.out.println("Stopping container after 1 second..");
            client.stopContainer(containerId, 5);
        });
        HttpStatus status = client.waitContainer(containerId);
        assertThat(status.code(), is(equalTo(200)));
    }


    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldExportContainer() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        Path pathToExportTo = tmp.newFolder().toPath();
        client.exportContainer(containerId, pathToExportTo);
        assertTrue(Files.newDirectoryStream(pathToExportTo, p -> p.toFile().isFile()).iterator().hasNext());
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldShowContainerStats() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        rx.Observable<ContainerStats> containerStatsObservable = client.containerStatsObs(containerId);
        Subscriber<ContainerStats> containerStatsSubscriber = new Subscriber<ContainerStats>() {

            @Override
            public void onCompleted() {
                logger.info("Successfully received all the container stats for container with id {}", containerId);
            }

            @Override
            public void onError(Throwable e) {
                logger.error("Error encountered while processing container stats for container with id {}", containerId);
            }

            @Override
            public void onNext(ContainerStats msg) {
                logger.info("Received a new message for container '{}'", containerId);
                assertNotNull(msg);
            }
        };

        rx.Observable.timer(5, TimeUnit.SECONDS).forEach(t -> {
            logger.info("Unsubscribing subscriber...");
            containerStatsSubscriber.unsubscribe();
            logger.info("Unsubscribed subscriber...");
        });

        containerStatsObservable.subscribe(containerStatsSubscriber);
    }

    @Test
    @CreateDockerContainer(containers = CONTAINER_NAME)
    public void shouldShowContainerLogs() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        Observable<String> logsObs = client.containerLogsObs(containerId);
        StringBuilder result = new StringBuilder();
        Subscriber<String> statsSub = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                logger.info("Successfully received all the container logs for container with id {}", containerId);
                result.append("Completed!!");
            }

            @Override
            public void onError(Throwable e) {
                logger.error(String.format("Error encountered while processing container logs for container with id %s", containerId), e);
                fail("Should not throw error");
            }

            @Override
            public void onNext(String msg) {
                logger.info("Received a new message for container '{}'", containerId);
                assertNotNull(msg);
            }
        };

        rx.Observable.timer(5, TimeUnit.SECONDS).forEach(t -> {
            logger.info("Unsubscribing subscriber...");
            statsSub.unsubscribe();
            logger.info("Unsubscribed subscriber...");
        });

        logsObs.subscribe(statsSub);
        assertThat(result.toString(), equalTo("Completed!!"));
    }

    @Test
    public void shouldPullImageFromDockerHub() throws Exception {
        HttpStatus status = client.pullImage("busybox");
        assertThat(status.code(), equalTo(HttpStatus.OK.code()));
    }

    @Test
    public void shouldPullLatestTagOfHelloWorldImageFromDockerHub() throws Exception {
        HttpStatus status = client.pullImage("hello-world", "latest");
        assertThat(status.code(), equalTo(HttpStatus.OK.code()));
    }

    @Test
    public void shouldPullImageObsFromDockerHub() throws Exception {
        client.pullImageObs("busybox").subscribe(System.out::println);
    }

    @Test
    public void shouldPullLatestTagOfOpenShiftHelloImageFromDockerHub() throws Exception {
        HttpStatus status = client.pullImage("hello-openshift", "openshift", "latest");
        assertThat(status.code(), equalTo(HttpStatus.OK.code()));
    }

    @Test
    public void shouldListImagesInLocalRepository() throws Exception {
        Stream<DockerImage> images = client.listImages();
        assertThat(images.count(), is(greaterThan(0L)));
    }

    @Test
    public void shouldListAllImages() throws Exception {
        Stream<DockerImage> images = client.listAllImages();
        assertThat(images.count(), is(greaterThan(0L)));
    }

    @Test
    public void shouldListImageByName() throws Exception {
        client.pullImage("busybox");
        Stream<DockerImage> images = client.listImages("busybox");
        assertThat(images.count(), is(greaterThan(0L)));
    }


    @Test
    public void shouldListDanglingImages() throws Exception {
        Stream<DockerImage> images = client.listDanglingImages();
        images.forEach(System.out::println);
    }

    @Test
    public void shouldSearchImage() throws Exception {
        Stream<DockerImageInfo> ubuntuImages = client.searchImages("ubuntu");
        long count = ubuntuImages.count();
        assertThat(count, greaterThan(0L));
    }

    @Test
    public void shouldSearchImageWithPredicate() throws Exception {
        Stream<DockerImageInfo> ubuntuImages = client.searchImages("ubuntu", image -> image.getStarCount() > 2400 && image.isOfficial());
        DockerImageInfo officialDockerImage = ubuntuImages.findFirst().get();
        assertThat(officialDockerImage.getName(), equalTo("ubuntu"));
    }

    @Test
    public void shouldBuildImageFromTarWithOnlyDockerFile() throws Exception {
        Observable<String> buildImageObs = client.buildImageObs("test_rx_docker/my_hello_world_image", Paths.get("rx-docker-client", "src", "test", "resources", "images", "my_hello_world_image.tar"));
        final StringBuilder resultCapturer = new StringBuilder();
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> resultCapturer.append("Completed!!!"));
        assertThat(resultCapturer.toString(), equalTo("Completed!!!"));
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
