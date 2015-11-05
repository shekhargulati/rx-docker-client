package com.shekhargulati.reactivex.docker.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shekhargulati.reactivex.docker.client.function.StringResponseToCollectionTransformer;
import com.shekhargulati.reactivex.docker.client.function.StringResponseTransformer;
import com.shekhargulati.reactivex.docker.client.http_client.RxHttpClient;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class OkHttpBasedRxHttpClientTest {

    final String githubApiUrl = "https://api.github.com";
    private String githubUser = "shekhargulati";

    @Test
    public void shouldMakeGetCallAndReturnObservableOfString() throws Exception {
        String listUserRepo = String.format("/users/%s/repos", githubUser);
        RxHttpClient client = RxHttpClient.newRxClient(githubApiUrl);
        Observable<String> repos = client.get(listUserRepo);
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        repos.subscribe(subscriber);
        assertThat(subscriber.getOnNextEvents(), hasSize(1));
        assertThat(subscriber.getOnCompletedEvents(), hasSize(1));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void shouldMakeGetCallAndReturnObservableWithTransformedType() throws Exception {
        String listUserRepo = String.format("/users/%s/repos", githubUser);
        RxHttpClient client = RxHttpClient.newRxClient(githubApiUrl);
        Type type = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        StringResponseTransformer<List<Map<String, Object>>> transformer = json -> new Gson().fromJson(json, type);
        Observable<List<Map<String, Object>>> repos = client.get(listUserRepo, transformer);
        TestSubscriber<List<Map<String, Object>>> subscriber = new TestSubscriber<>();
        repos.subscribe(subscriber);
        assertThat(subscriber.getOnNextEvents(), hasSize(1));
        assertThat(subscriber.getOnCompletedEvents(), hasSize(1));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void shouldMakeGetCallAndReturnObservableWithCollectionTransformedType() throws Exception {
        String listUserRepo = String.format("/users/%s/repos", githubUser);
        RxHttpClient client = RxHttpClient.newRxClient(githubApiUrl);
        Type type = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        StringResponseToCollectionTransformer<Map<String, Object>> transformer = json -> new Gson().fromJson(json, type);
        Observable<Map<String, Object>> repos = client.get(listUserRepo, transformer);
        TestSubscriber<Map<String, Object>> subscriber = new TestSubscriber<>();
        repos.subscribe(subscriber);
        assertThat(subscriber.getOnNextEvents(), hasSize(greaterThanOrEqualTo(30)));
        assertThat(subscriber.getOnCompletedEvents(), hasSize(1));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }
}