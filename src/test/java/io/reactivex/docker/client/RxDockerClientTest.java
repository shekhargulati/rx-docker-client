package io.reactivex.docker.client;

import com.squareup.okhttp.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.docker.client.representations.*;
import io.reactivex.docker.client.ssl.DockerCertificates;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
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

    private static DockerClient client;
    private static String dockerHost;
    private static Map<String, String> dockerConfiguration;


    @BeforeClass
    public static void setupInfra() throws Exception {
//        createAndWaitForProcessExecution(new String[]{"docker-machine", "create", "--driver", "virtualbox", DOCKER_MACHINE_NAME});
        createAndWaitForProcessExecution(new String[]{"docker-machine", "start", DOCKER_MACHINE_NAME});
        createAndWaitForProcessExecution(new String[]{"docker-machine", "env", DOCKER_MACHINE_NAME});
        readOutputFileAndSetDockerProperties();
        dockerHost = dockerConfiguration.get("DOCKER_HOST");
        client = DockerClient.newDockerClient(dockerHost, dockerConfiguration.get("DOCKER_CERT_PATH"));

    }

    @AfterClass
    public static void tearDownInfra() throws Exception {
//        client.removeAllContainers();
//        assertThat(client.listAllContainers().size(), equalTo(0));
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
        assertThat(dockerVersion.version(), is(equalTo("1.8.1")));
        assertThat(dockerVersion.apiVersion(), is(equalTo("1.20")));
    }

    @Test
    public void shouldFetchDockerInformation() throws Exception {
        DockerInfo info = client.info();
        assertThat(info.dockerRootDir(), equalTo("/mnt/sda1/var/lib/docker"));
        assertThat(info.initPath(), equalTo("/usr/local/bin/docker"));
    }

    @Test
    public void shouldListAllContainers() throws Exception {
//        createContainer("rx-docker-client-test-2");
//        createContainer("rx-docker-client-test-3");
        List<DockerContainer> dockerContainers = client.listAllContainers();
        dockerContainers.forEach(container -> System.out.println("Docker Container >> \n " + container));
        assertThat(dockerContainers, hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    public void shouldListRunningContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listRunningContainers();
        assertThat(dockerContainers.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void shouldCreateContainer() throws Exception {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldCreateContainerWithName() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-1");
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldInspectContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-4");
        ContainerInspectResponse containerInspectResponse = client.inspectContainer(response.getId());
        System.out.println(containerInspectResponse);
        assertThat(containerInspectResponse.path(), is(equalTo("/bin/bash")));
    }

    //    @Test
    public void shouldStartCreatedContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-5");
        HttpResponseStatus httpStatus = client.startContainer(response.getId());
        assertThat(httpStatus.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldStopStartedContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-6");
        client.startContainer(response.getId());
        HttpResponseStatus status = client.stopContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldQueryContainersByFilters() throws Exception {
        createContainer("rx-docker-client-test-7");
        createContainer("rx-docker-client-test-8");
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers.size(), greaterThanOrEqualTo(2));
    }

    //    @Test
    public void shouldRestartAContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-9");
        HttpResponseStatus status = client.restartContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldKillARunningContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-11");
        client.startContainer(response.getId());
        HttpResponseStatus status = client.killRunningContainer(response.getId());
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldRemoveDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-12");
        HttpResponseStatus status = client.removeContainer(response.getId());
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldRemoveDockerContainerWithQueryParameters() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-13");
        HttpResponseStatus status = client.removeContainer(response.getId(), true, true);
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }

    //    @Test
    public void shouldRenameDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-14");
        HttpResponseStatus status = client.renameContainer(response.getId(), "rx-docker-client-test-14-renamed");
        assertThat(status.code(), is(equalTo(NO_CONTENT.code())));
    }


    //    @Test
    public void shouldWaitForARunningDockerContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-15");
        client.startContainer(response.getId());
        Observable.timer(10, TimeUnit.SECONDS).forEach(t -> {
            System.out.println("Stopping container after 10 seconds..");
            client.stopContainer(response.getId(), 5);
        });
        HttpResponseStatus status = client.waitContainer(response.getId());
        assertThat(status.code(), is(equalTo(OK.code())));
    }

    //    @Test
    public void shouldExportContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-16");
        String containerId = response.getId();
        String filepath = "/tmp/" + containerId + ".tar";
        client.exportContainer(containerId, filepath);
        assertTrue(Paths.get(filepath).toFile().exists());
    }

    //    @Test
    public void shouldShowContainerStats() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-17");
        String containerId = response.getId();
        client.startContainer(containerId);
        ContainerStats containerStats = client.containerStats(containerId);
        System.out.println(containerStats);
        assertNotNull(containerStats);
    }

    //    @Test
    public void shouldPullImageFromDockerRegistry() throws Exception {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(2, TimeUnit.MINUTES);
        client.setSslSocketFactory(new DockerCertificates(Paths.get("/Users/shekhargulati/.docker/machine/machines/rx-docker-test")).sslContext().getSocketFactory());
        Request request = new Request.Builder()
                .url("https://192.168.99.100:2376/images/create?fromImage=busybox")
                .header("Content-Type", "application/json").post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Encountered failure >> " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println(response.headers());
                System.out.println(response.body().string());
            }
        });

        Thread.sleep(60000);
    }

    //    @Test
    public void pullImage() throws Exception {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(2, TimeUnit.MINUTES);
        client.setSslSocketFactory(new DockerCertificates(Paths.get("/Users/shekhargulati/.docker/machine/machines/rx-docker-test")).sslContext().getSocketFactory());
        Observable<Buffer> pullImageObservable = Observable.create(sub -> {
            Request request = new Request.Builder()
                    .url("https://192.168.99.100:2376/images/create?fromImage=busybox")
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ""))
                    .build();
            Call call = client.newCall(request);
            try {
                System.out.println("Making a response to fetch an image");
                Response response = call.execute();
                System.out.println(response.headers());
                if (response.isSuccessful()) {
                    System.out.println("Downloading chunk");
                    BufferedSource source = response.body().source();
                    while (!source.exhausted()) {
                        sub.onNext(source.buffer());
                    }
                    sub.onCompleted();
                } else {
                    sub.onError(new RuntimeException(String.format("Unable to complete request %d and message %s", response.code(), response.message())));
                }
            } catch (IOException e) {
                sub.onError(e);
            }

        });

        Subscriber<Buffer> httpSubscriber = new Subscriber<Buffer>() {
            @Override
            public void onCompleted() {
                System.out.println("Successfully recieved all data");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error encountered >> " + e);
                e.printStackTrace();
            }

            @Override
            public void onNext(Buffer res) {
                System.out.println("Received message >> " + res.readString(Charset.defaultCharset()));
            }
        };

        pullImageObservable.subscribe(httpSubscriber);
        httpSubscriber.unsubscribe();


    }

    @Ignore
    public void shouldListProcessesRunningInsideContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-X");
        client.startContainer(response.getId());
        ProcessListResponse processListResponse = client.listProcesses(response.getId());
        assertNotNull(processListResponse);
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