package io.reactivex.rxdockerclient;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
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
        System.setProperty("javax.net.debug", "ssl:handshake:data");
        String dockerHost = "tcp://192.168.99.100:2376";
        RxDockerClient client = new RxDockerClient(dockerHost, "/Users/shekhargulati/.docker/machine/machines/dev");
        client.version();

    }
}