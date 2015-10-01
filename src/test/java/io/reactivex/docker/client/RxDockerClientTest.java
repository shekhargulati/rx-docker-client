package io.reactivex.docker.client;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.docker.client.representations.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
        client.killAllRunningContainers();
//        assertThat(client.listAllContainers().size(), equalTo(0));
        createAndWaitForProcessExecution(new String[]{"docker-machine", "stop", DOCKER_MACHINE_NAME});
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

        assertThat(dockerVersion.getVersion(), is(equalTo("1.8.1")));
        assertThat(dockerVersion.getApiVersion(), is(equalTo("1.20")));
    }

    @Test
    public void shouldFetchDockerInformation() throws Exception {
        DockerInfo info = client.info();
        assertThat(info.getDockerRootDir(), equalTo("/mnt/sda1/var/lib/docker"));
        assertThat(info.getInitPath(), equalTo("/usr/local/bin/docker"));
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
    public void shouldListAllContainers() throws Exception {
        createContainer("rx-docker-client-test-2");
        createContainer("rx-docker-client-test-3");
        List<DockerContainer> dockerContainers = client.listAllContainers();
        assertThat(dockerContainers, hasSize(greaterThan(2)));
    }

    @Test
    public void shouldInspectContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-4");
        ContainerInspectResponse containerInspectResponse = client.inspectContainer(response.getId());
        assertThat(containerInspectResponse.path(), is(equalTo("/bin/bash")));
    }

    @Test
    public void shouldStartCreatedContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-5");
        HttpResponseStatus httpStatus = client.startContainer(response.getId());
        assertThat(httpStatus.code(), is(equalTo(HttpResponseStatus.NO_CONTENT.code())));
    }

    @Test
    public void shouldStopStartedContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-6");
        client.startContainer(response.getId());
        HttpResponseStatus status = client.stopContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(HttpResponseStatus.NO_CONTENT.code())));
    }

    @Test
    public void shouldQueryContainersByFilters() throws Exception {
        createContainer("rx-docker-client-test-7");
        createContainer("rx-docker-client-test-8");
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers.size(), greaterThanOrEqualTo(2));
    }

    @Test
    public void shouldRestartAContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-9");
        HttpResponseStatus status = client.restartContainer(response.getId(), 5);
        assertThat(status.code(), is(equalTo(HttpResponseStatus.NO_CONTENT.code())));
    }

    @Test
    public void shouldKillARunningContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-11");
        client.startContainer(response.getId());
        HttpResponseStatus status = client.killRunningContainer(response.getId());
        assertThat(status.code(), is(equalTo(HttpResponseStatus.NO_CONTENT.code())));
    }

    @Ignore
    public void shouldListProcessesRunningInsideContainer() throws Exception {
        DockerContainerResponse response = createContainer("rx-docker-client-test-X");
        client.startContainer(response.getId());
        ProcessListResponse processListResponse = client.listProcesses(response.getId());
        assertNotNull(processListResponse);
    }

    @Ignore
    public void shouldListRunningContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listRunningContainers();
        assertThat(dockerContainers.size(), greaterThanOrEqualTo(2));
    }

    private DockerContainerResponse createContainer(String containerName) {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        return client.createContainer(request, containerName);
    }

    private static void createAndWaitForProcessExecution(String[] cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        builder.redirectError(Paths.get("build/error.txt").toFile());
        builder.redirectOutput(Paths.get("build/output.txt").toFile());
        Process createMchProcess = builder.start();
        int createMchExitValue = createMchProcess.waitFor();
        System.out.println(createMchExitValue);
    }

    private static void readOutputFileAndSetDockerProperties() throws Exception {
        dockerConfiguration = Files.lines(Paths.get("build/output.txt")).filter(line -> line.contains("DOCKER_HOST") || line.contains("DOCKER_CERT_PATH")).map(line -> line.split("\\s")[1]).map(line -> {
            String[] split = line.split("=");
            return new SimpleEntry<>(split[0], split[1].replace("\"", ""));
        }).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }
}