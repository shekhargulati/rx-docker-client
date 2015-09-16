package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class RxDockerClientTest {

    private RxDockerClient client;

    @Before
    public void setUp() throws Exception {
        String dockerHost = "tcp://192.168.99.100:2376";
        String userHome = System.getenv("HOME");
        client = new RxDockerClient(dockerHost, String.format("%s/.docker/machine/machines/dev", userHome));
    }

    @Test
    public void shouldConstructHttpDockerAPIUriWhenCertificateNotPresent() throws Exception {
        String dockerHost = "tcp://192.168.99.100:2375";
        RxDockerClient client = new RxDockerClient(dockerHost, null);
        String apiUri = client.getApiUri();
        assertThat(apiUri, equalTo("http://192.168.99.100:2375"));
    }

    @Test
    public void shouldConstructHttspDockerAPIUriWhenCertificatePresent() throws Exception {
        String apiUri = client.getApiUri();
        assertThat(apiUri, equalTo("https://192.168.99.100:2376"));
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
    public void shouldListRunningContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listRunningContainers();
        assertThat(dockerContainers, hasSize(1));
    }

    @Test
    public void shouldListAllContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listAllContainers();
        assertThat(dockerContainers, hasSize(greaterThan(1)));
    }

    @Test
    public void shouldQueryContainersByFilters() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().withAll(true).withLimit(3).withFilter("status", "exited").createQueryParameters();
        List<DockerContainer> containers = client.listContainers(queryParameters);
        assertThat(containers, hasSize(3));
    }


    @Test
    public void shouldCreateContainer() throws Exception {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request);
        assertThat(response.getId(), notNullValue());
    }

    @Test
    public void shouldCreateContainerWithName() throws Exception {
        DockerContainerRequest request = new DockerContainerRequestBuilder().setImage("ubuntu").setCmd(Arrays.asList("/bin/bash")).createDockerContainerRequest();
        DockerContainerResponse response = client.createContainer(request, "shekhar-test");
        assertThat(response.getId(), notNullValue());
    }
}