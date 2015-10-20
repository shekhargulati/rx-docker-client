package io.reactivex.docker.client;

import io.reactivex.docker.client.http_client.HttpStatus;
import io.reactivex.docker.client.representations.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

public class RxDockerClientTest {

    private static final String DOCKER_MACHINE_NAME = "rx-docker-test";
    public static final String CONTAINER_NAME = "my_first_container";
    public static final String SECOND_CONTAINER_NAME = "my_second_container";

    private final Logger logger = LoggerFactory.getLogger(RxDockerClientTest.class);

    private static DockerClient client;
    private static Map<String, String> dockerConfiguration;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();


    @BeforeClass
    public static void setupInfra() throws Exception {
//        createAndWaitForProcessExecution(new String[]{"docker-machine", "create", "--driver", "virtualbox", DOCKER_MACHINE_NAME});
        createAndWaitForProcessExecution(new String[]{"docker-machine", "start", DOCKER_MACHINE_NAME});
        createAndWaitForProcessExecution(new String[]{"docker-machine", "env", DOCKER_MACHINE_NAME});
        readOutputFileAndSetDockerProperties();
        String dockerHost = dockerConfiguration.get("DOCKER_HOST");
        client = DockerClient.newDockerClient(dockerHost, dockerConfiguration.get("DOCKER_CERT_PATH"));

    }

    @After
    public void tearDownInfra() throws Exception {
        client.removeAllContainers();
        assertThat(client.listAllContainers().size(), equalTo(0));
        client.removeDanglingImages();
        client.removeImages(dockerImage -> dockerImage.repoTags().stream().anyMatch(repo -> repo.contains("test_rx_docker")));
//        createAndWaitForProcessExecution(new String[]{"docker-machine", "stop", DOCKER_MACHINE_NAME});
//        createAndWaitForProcessExecution(new String[]{"docker-machine", "rm", DOCKER_MACHINE_NAME});
    }

    @Test
    public void shouldConstructHttpDockerAPIUriWhenCertificateNotPresent() throws Exception {
        String dockerHost = "tcp://192.168.99.100:2375";
        RxDockerClient client = DockerClient.newDockerClient(dockerHost, null);
        String apiUri = client.getApiUri();
        assertThat(apiUri, equalTo("http://192.168.99.100:2375"));
    }

    @Test
    public void shouldConstructHttspDockerAPIUriWhenCertificatePresent() throws Exception {
        String apiUri = client.getApiUri();
        assertThat(apiUri, startsWith("https://"));
        assertThat(apiUri, containsString(":2376"));
    }

    @Test
    public void shouldFetchVersionInformationFromDocker() throws Exception {
        DockerVersion dockerVersion = client.serverVersion();
        assertThat(dockerVersion.version(), is(equalTo("1.8.3")));
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
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldCreateContainerWithName() throws Exception {
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

    @Test
    public void shouldInspectContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        ContainerInspectResponse containerInspectResponse = client.inspectContainer(response.getId());
        System.out.println(containerInspectResponse);
        assertThat(containerInspectResponse.path(), is(equalTo("/bin/bash")));
    }

    @Test
    public void shouldStartCreatedContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        HttpStatus httpStatus = client.startContainer(response.getId());
        assertThat(httpStatus.code(), is(equalTo(204)));
    }

    @Test
    public void shouldStopStartedContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        client.startContainer(response.getId());
        HttpStatus status = client.stopContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldQueryContainersByFilters() throws Exception {
        createContainer(CONTAINER_NAME);
        createContainer(SECOND_CONTAINER_NAME);
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers.size(), greaterThanOrEqualTo(2));
    }

    @Test
    public void shouldRestartAContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        HttpStatus status = client.restartContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldKillARunningContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        client.startContainer(response.getId());
        HttpStatus status = client.killRunningContainer(response.getId());
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldRemoveDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        HttpStatus status = client.removeContainer(response.getId());
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldRemoveDockerContainerWithQueryParameters() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        HttpStatus status = client.removeContainer(response.getId(), true, true);
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldRenameDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        HttpStatus status = client.renameContainer(response.getId(), "my_first_container-renamed");
        assertThat(status.code(), is(equalTo(204)));
    }

    @Test
    public void shouldWaitForARunningDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        client.startContainer(response.getId());
        Observable.timer(1, TimeUnit.SECONDS).forEach(t -> {
            System.out.println("Stopping container after 1 second..");
            client.stopContainer(response.getId(), 5);
        });
        HttpStatus status = client.waitContainer(response.getId());
        assertThat(status.code(), is(equalTo(200)));
    }

    @Test
    public void shouldExportContainer() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        String containerId = response.getId();
        Path pathToExportTo = tmp.newFolder().toPath();
        client.exportContainer(containerId, pathToExportTo);
        assertTrue(Files.newDirectoryStream(pathToExportTo, p -> p.toFile().isFile()).iterator().hasNext());
    }

    @Test
    public void shouldShowContainerStats() throws Exception {
        DockerContainerResponse response = createContainer(CONTAINER_NAME);
        String containerId = response.getId();
        client.startContainer(containerId);
        Observable<ContainerStats> containerStatsObservable = client.containerStatsObs(containerId);
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

        Observable.timer(5, TimeUnit.SECONDS).forEach(t -> {
            logger.info("Unsubscribing subscriber...");
            containerStatsSubscriber.unsubscribe();
            logger.info("Unsubscribed subscriber...");
        });

        containerStatsObservable.subscribe(containerStatsSubscriber);
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
        Stream<DockerImage> images = client.listImages("busybox");
        assertThat(images.count(), is(equalTo(7L)));
    }

    @Test
    public void shouldListDanglingImages() throws Exception {
        Stream<DockerImage> images = client.listDanglingImages();
        images.forEach(System.out::println);
    }

    @Test
    public void shouldRemoveImage() throws Exception {
        client.pullImage("hello-world", "latest");
        HttpStatus httpStatus = client.removeImage("hello-world");
        assertThat(httpStatus.code(), equalTo(200));
    }

    @Test
    public void shouldRemoveDanglingImages() throws Exception {
        client.removeDanglingImages();
        long count = client.listDanglingImages().count();
        assertThat(count, equalTo(0L));
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
        Observable<String> buildImageObs = client.buildImageObs("test_rx_docker/my_first_docker_image", Paths.get("src", "test", "resources", "images", "my_docker_image.tar"));
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

    private DockerContainerResponse createContainer(String containerName) {
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Arrays.asList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }

    private static void createAndWaitForProcessExecution(String[] cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        builder.redirectError(Paths.get("build/error.txt").toFile());
        builder.redirectOutput(Paths.get("build/output.txt").toFile());
        Process createMchProcess = builder.start();
        int createMchExitValue = createMchProcess.waitFor();
        System.out.println(String.format("%s >> %d", Arrays.toString(cmd), createMchExitValue));
    }

    private static void readOutputFileAndSetDockerProperties() throws Exception {
        dockerConfiguration = Files.lines(Paths.get("build/output.txt")).filter(line -> line.contains("DOCKER_HOST") || line.contains("DOCKER_CERT_PATH")).map(line -> line.split("\\s")[1]).map(line -> {
            String[] split = line.split("=");
            return new SimpleEntry<>(split[0], split[1].replace("\"", ""));
        }).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }
}