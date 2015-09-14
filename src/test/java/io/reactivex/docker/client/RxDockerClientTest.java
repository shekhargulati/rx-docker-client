package io.reactivex.docker.client;

import io.reactivex.docker.client.model.DockerContainer;
import io.reactivex.docker.client.model.DockerInfo;
import io.reactivex.docker.client.model.DockerVersion;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
        assertThat(info.getId(), equalTo("G7BK:NRSS:QGPA:ECLM:3CT6:OJGJ:KHWZ:OBWS:SQAT:3VGL:TEAT:LO47"));
    }

    @Test
    public void shouldListAllContainers() throws Exception {
        List<DockerContainer> dockerContainers = client.listContainers();

        assertThat(dockerContainers.size(), equalTo(1));

        System.out.println(dockerContainers.get(0));
    }
}