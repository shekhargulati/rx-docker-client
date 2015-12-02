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

import com.shekhargulati.reactivex.docker.client.junit.CreateDockerContainer;
import com.shekhargulati.reactivex.docker.client.junit.DockerContainerRule;
import com.shekhargulati.reactivex.docker.client.representations.*;
import com.shekhargulati.reactivex.rxokhttp.HttpStatus;
import com.shekhargulati.reactivex.rxokhttp.QueryParameter;
import com.shekhargulati.reactivex.rxokhttp.StreamResponseException;
import org.junit.*;
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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class RxDockerClientTest {

    private final Logger logger = LoggerFactory.getLogger(RxDockerClientTest.class);

    public static final String CONTAINER_NAME = "my_first_container";
    public static final String SECOND_CONTAINER_NAME = "my_second_container";

    private static DockerClient client = DockerClient.fromDefaultEnv();

    @BeforeClass
    public static void init() throws Exception {
        client.pullImage("ubuntu");
    }

    @After
    public void tearDown() throws Exception {
        client.listAllImages().filter(dockerImage -> dockerImage.repoTags().stream().anyMatch(repo -> repo.contains("test_rx_docker"))).forEach(dockerImage -> {
            try {
                logger.info("Removing image {}", dockerImage.id());
                client.removeImage(dockerImage.id(), true, true);
            } catch (Exception e) {
                // ignoring for CircleCI
            }
        });
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
    @CreateDockerContainer(container = CONTAINER_NAME)
    @CreateDockerContainer(container = SECOND_CONTAINER_NAME)
    public void shouldListAllContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listAllContainers();
        dockerContainers.forEach(container -> System.out.println("Docker Container >> \n " + container));
        assertThat(dockerContainers, hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldInspectContainer() throws Exception {
        ContainerInspectResponse containerInspectResponse = client.inspectContainer(containerRule.containerIds().get(0));
        assertThat(containerInspectResponse.path(), is(equalTo("/bin/bash")));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
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
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldStopStartedContainer() throws Exception {
        String containerId = containerRule.first();
        HttpStatus status = client.stopContainer(containerId, 5);
        assertThat(status.code(), is(equalTo(204)));
    }


    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    @CreateDockerContainer(container = SECOND_CONTAINER_NAME)
    public void shouldQueryContainersByFilters() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers.size(), greaterThanOrEqualTo(2));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldRestartAContainer() throws Exception {
        HttpStatus status = client.restartContainer(containerRule.containerIds().get(0), 5);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldKillARunningContainer() throws Exception {
        String containerId = containerRule.first();
        HttpStatus status = client.killRunningContainer(containerId);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldRemoveDockerContainerWithQueryParameters() throws Exception {
        String containerId = containerRule.first();
        try {
            HttpStatus status = client.removeContainer(containerId, false, true);
            assertThat(status.code(), is(equalTo(204)));
        } catch (Exception e) {
            // ignoring for Circle CI
        }
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldRenameDockerContainer() throws Exception {
        HttpStatus status = client.renameContainer(containerRule.containerIds().get(0), "my_first_container-renamed");
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldWaitForARunningDockerContainer() throws Exception {
        String containerId = containerRule.first();
        rx.Observable.timer(1, TimeUnit.SECONDS).forEach(t -> {
            System.out.println("Stopping container after 1 second..");
            client.stopContainer(containerId, 5);
        });
        HttpStatus status = client.waitContainer(containerId);
        assertThat(status.code(), is(equalTo(200)));
    }


    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldExportContainer() throws Exception {
        String containerId = containerRule.first();
        Path pathToExportTo = tmp.newFolder().toPath();
        client.exportContainer(containerId, pathToExportTo);
        assertTrue(Files.newDirectoryStream(pathToExportTo, p -> p.toFile().isFile()).iterator().hasNext());
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldShowContainerStats() throws Exception {
        String containerId = containerRule.first();
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
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldShowContainerLogs() throws Exception {
        String containerId = containerRule.containerIds().get(0);
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
        Observable<String> buildImageObs = client.buildImageObs("test_rx_docker/my_hello_world_image", Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
        final StringBuilder resultCapturer = new StringBuilder();
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> resultCapturer.append("Completed!!!"));
        assertThat(resultCapturer.toString(), equalTo("Completed!!!"));
    }

    @Test
    public void shouldBuildImageWhenDockerFileIsPresentAtDifferentPathInsideTar() throws Exception {
        String repositoryName = "test_rx_docker/dockerfile_option_image";
        Path path = Paths.get("src", "test", "resources", "images", "dockerfile_option_image.tar");
        Observable<String> buildImageObs = client.buildImageObs(repositoryName, path, new BuildImageQueryParameters("innerDir/innerDockerfile"));
        final StringBuilder resultCapturer = new StringBuilder();
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> resultCapturer.append("Completed!!!"));
        assertThat(resultCapturer.toString(), equalTo("Completed!!!"));
    }

    @Test
    public void shouldBuildImageUsingRemoteDockerFile() throws Exception {
        String repository = "test_rx_docker/hello_world_remote";
        Observable<String> buildImageObs = client.buildImageObs(repository, BuildImageQueryParameters.withRemoteDockerfile("https://raw.githubusercontent.com/shekhargulati/hello-world-docker/master/Dockerfile"));
        final StringBuilder resultCapturer = new StringBuilder();
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> resultCapturer.append("Completed!!!"));
        assertThat(resultCapturer.toString(), equalTo("Completed!!!"));
    }

    @Test
    public void shouldTagAnImage() throws Exception {
        Observable<String> buildImageObs = client.buildImageObs("my_hello_world_image", Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> System.out.println("Completed!!!"));
        HttpStatus httpStatus = client.tagImage("my_hello_world_image", ImageTagQueryParameters.with("test_rx_docker/my_hello_world_image", "v42"));
        assertThat(httpStatus.code(), equalTo(201));
    }

    @Test
    public void shouldShowHistoryOfImage() throws Exception {
        String image = "test_rx_docker/my_hello_world_image";
        Observable<String> buildImageObs = client.buildImageObs(image, Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> System.out.println("Completed!!!"));

        Stream<DockerImageHistory> dockerImageHistoryStream = client.imageHistory(image);
        long historyCount = dockerImageHistoryStream.count();
        assertThat(historyCount, greaterThan(1L));
    }

    @Test
    public void shouldInspectDockerImage() throws Exception {
        DockerImageInspectDetails inspectDetails = client.inspectImage("ubuntu");
        assertNotNull(inspectDetails);
    }

    @Test
    public void pushImageToRepository() throws Exception {
        String image = "shekhar007/my_hello_world_image";
        Observable<String> buildImageObs = client.buildImageObs(image, Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
        buildImageObs.subscribe(System.out::println, error -> fail("Should not fail but failed with message " + error.getMessage()), () -> System.out.println("Completed!!!"));

        StringBuilder resultCapturer = new StringBuilder();
        client.pushImageObs(image, AuthConfig.authConfig("xxxx", "xxx", "xxx")).subscribe(System.out::println, error -> {
            assertThat(error, is(instanceOf(StreamResponseException.class)));
            String message = DockerErrorDetails.errorDetails(((StreamResponseException) error).getJson()).getError();
            resultCapturer.append(message);
        }, () -> fail("should not complete as authentication header was incorrect!!"));

        assertThat(resultCapturer.toString(), anyOf(equalTo("Authentication is required."), equalTo("unauthorized: access to the requested resource is not authorized")));
    }

    @Test
    public void shouldReturnHttpStatus500WhenAuthConfigurationIsInvalid() throws Exception {
        HttpStatus httpStatus = client.checkAuth(AuthConfig.authConfig("xxx", "xxx", "xxxx"));
        assertThat(httpStatus, is(equalTo(HttpStatus.SERVER_ERROR)));
    }

    @Test
    public void shouldPingDockerServer() throws Exception {
        HttpStatus httpStatus = client.ping();
        assertThat(httpStatus, is(equalTo(HttpStatus.OK)));
    }

    @Ignore // enable it when we have exec operation available
    @Test
    @CreateDockerContainer(container = CONTAINER_NAME)
    public void shouldInspectAContainerForFileSystemChanges() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        client.startContainer(containerId);
        List<ContainerChange> containerChanges = client.inspectChangesOnContainerFilesystem(containerId);
        containerChanges.forEach(System.out::println);
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldResizeContainerTtyToUserGivenValues() throws Exception {
        String containerId = containerRule.containerIds().get(0);
        HttpStatus httpStatus = client.resizeContainerTty(containerId, QueryParameter.of("h", 10), QueryParameter.of("w", 80));
        assertThat(httpStatus, equalTo(HttpStatus.OK));
    }


    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldPauseAndUnpauseAContainer() throws Exception {
        assumeTrue(System.getenv("CIRCLE_USERNAME") == null);
        String containerId = containerRule.containerIds().get(0);
        HttpStatus httpStatus = client.pauseContainer(containerId);
        assertThat(httpStatus, equalTo(HttpStatus.NO_CONTENT));
        DockerContainer pausedContainer = client.listAllContainers().stream().filter(c -> c.getId().equals(containerId)).findFirst().get();
        assertThat(pausedContainer.getStatus(), containsString("Paused"));
        HttpStatus httpStatus1 = client.unpauseContainer(containerId);
        assertThat(httpStatus1, equalTo(HttpStatus.NO_CONTENT));

        DockerContainer unpausedContainer = client.listAllContainers().stream().filter(c -> c.getId().equals(containerId)).findFirst().get();
        assertThat(unpausedContainer.getStatus(), not(containsString("Paused")));
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, command = {"ls", "-la"}, start = true)
    public void shouldAttachToAContainer() throws Exception {
        String containerId = containerRule.first();
        QueryParameter[] queryParameters = {QueryParameter.of("stream", true), QueryParameter.of("stdout", true)};
        Observable<String> containerStream = client.attachContainerObs(containerId, queryParameters);
        containerStream.subscribe(System.out::println, System.out::println, System.out::println);
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldRetrieveArchiveInformationOfContainer() throws Exception {
        String containerId = containerRule.first();

        ContainerArchiveInformation containerArchiveInformation = client.containerArchiveInformation(containerId, "/root");

        System.out.println(containerArchiveInformation);

        assertThat(containerArchiveInformation.getName(), is(equalTo("root")));
        assertThat(containerArchiveInformation.getSize(), is(greaterThan(0))); // as CircleCI returns different value than 4096
    }

    @Test
    @CreateDockerContainer(container = CONTAINER_NAME, start = true)
    public void shouldRetrieveContainerArchive() throws Exception {
        String containerId = containerRule.first();

        Path pathToExportTo = tmp.newFolder().toPath();
        client.containerArchive(containerId, "/root", pathToExportTo);
        assertTrue(Files.newDirectoryStream(pathToExportTo, p -> p.toFile().isFile()).iterator().hasNext());
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
