package io.reactivex.docker.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.reactivex.docker.client.model.DockerVersion;
import io.reactivex.netty.protocol.http.client.FlatResponseOperator;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.junit.Test;
import rx.Observable;

import java.nio.charset.Charset;

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
//        System.setProperty("javax.net.debug", "ssl:handshake:data");
        String dockerHost = "tcp://192.168.99.100:2376";
        String userHome = System.getenv("HOME");
        RxDockerClient client = new RxDockerClient(dockerHost, String.format("%s/.docker/machine/machines/dev", userHome));
        Observable<HttpClientResponse<ByteBuf>> obs = client.versionObs();
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        obs.lift(FlatResponseOperator.<ByteBuf>flatResponse()).map(resp -> gson.fromJson(resp.getContent().toString(Charset.defaultCharset()), DockerVersion.class)).toBlocking().forEach(System.out::println);

    }
}