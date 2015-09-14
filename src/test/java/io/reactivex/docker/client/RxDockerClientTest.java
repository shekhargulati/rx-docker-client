package io.reactivex.docker.client;

import io.reactivex.docker.client.model.DockerVersion;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RxDockerClientTest {

    @Test
    public void shouldConstructHttpDockerAPIUriWhenCertificateNotPresent() throws Exception {
        String dockerHost = "tcp://192.168.99.100:2375";
        RxDockerClient client = new RxDockerClient(dockerHost, null);
        String apiUri = client.getApiUri();
        assertThat(apiUri, equalTo("http://192.168.99.100:2375"));
    }


    @Test
    public void shouldConstructHttspDockerAPIUriWhenCertificatePresent() throws Exception {
        String dockerHost = "tcp://192.168.99.100:2376";
        RxDockerClient client = new RxDockerClient(dockerHost, "~/.docker/machine/machines/dev");
        String apiUri = client.getApiUri();
        assertThat(apiUri, equalTo("https://192.168.99.100:2376"));
    }

    @Test
    public void shouldFetchVersionInformationFromDocker() throws Exception {
//        System.setProperty("javax.net.debug", "ssl:handshake:data");
        String dockerHost = "tcp://192.168.99.100:2376";
        String userHome = System.getenv("HOME");
        RxDockerClient client = new RxDockerClient(dockerHost, String.format("%s/.docker/machine/machines/dev", userHome));

        DockerVersion dockerVersion = client.getServerVersion();
        assertThat(dockerVersion.getVersion(), is(equalTo("1.8.1")));
        assertThat(dockerVersion.getApiVersion(), is(equalTo("1.20")));

    }
}