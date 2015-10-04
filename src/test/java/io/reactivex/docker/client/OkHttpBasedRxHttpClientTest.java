package io.reactivex.docker.client;

import com.google.gson.Gson;
import io.reactivex.docker.client.representations.DockerVersion;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

public class OkHttpBasedRxHttpClientTest {

    @Test
    public void shouldMakeASuccessfulGetRequest() throws Exception {
        RxHttpClient client = RxHttpClient.newRxClient("192.168.99.100", 2376, "/Users/shekhargulati/.docker/machine/machines/rx-docker-test");
        Observable<DockerVersion> responseStream = client.get("/version", json -> new Gson().fromJson(json, DockerVersion.class));

        Subscriber<DockerVersion> httpSubscriber = new Subscriber<DockerVersion>() {
            @Override
            public void onCompleted() {
                System.out.println("Successfully received all data");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error encountered >> " + e);
            }

            @Override
            public void onNext(DockerVersion res) {
                System.out.println("Received message >> " + res);
            }
        };

        responseStream.subscribe(httpSubscriber);
        httpSubscriber.unsubscribe();

    }
}