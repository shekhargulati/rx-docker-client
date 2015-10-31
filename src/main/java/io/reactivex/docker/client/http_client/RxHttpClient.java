package io.reactivex.docker.client.http_client;

import io.reactivex.docker.client.AuthConfig;
import io.reactivex.docker.client.function.*;
import okio.Buffer;
import rx.Observable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public interface RxHttpClient {

    public static final String EMPTY_BODY = "";

    static RxHttpClient newRxClient(final String host, final int port) {
        return new OkHttpBasedRxHttpClient(host, port);
    }

    static RxHttpClient newRxClient(final String host, final int port, String certPath) {
        return newRxClient(host, port, Optional.ofNullable(certPath));
    }

    static RxHttpClient newRxClient(final String host, final int port, Optional<String> certPath) {
        return new OkHttpBasedRxHttpClient(host, port, certPath);
    }

    static RxHttpClient newRxClient(final String apiUrl) {
        return new OkHttpBasedRxHttpClient(apiUrl);
    }

    /**
     * This method makes an HTTP GET request and return response body as String of Observable
     *
     * @param endpoint    Endpoint at which to make the GET call
     * @return Observable sequence of String
     */
    Observable<String> get(String endpoint);

    /**
     * This method makes an HTTP GET request and then convert the resultant JSON into R using the JsonBodyTransformer function
     *
     * @param endpoint    Endpoint at which to make the GET call
     * @param transformer function to convert String response into some other domain object
     * @param <R>         type returned by StringResponseTransformer
     * @return Observable sequence of R
     */
    <R> Observable<R> get(final String endpoint, StringResponseTransformer<R> transformer);

    /**
     * This method makes an HTTP GET request and then convert the resultant JSON into R using the JsonBodyTransformer function
     *
     * @param endpoint    Endpoint at which to make the GET call
     * @param headers     HTTP headers to be sent along with the request
     * @param transformer function to convert String response into some other domain object
     * @param <R>         type returned by StringResponseTransformer
     * @return Observable sequence of R
     */
    <R> Observable<R> get(String endpoint, Map<String, String> headers, StringResponseTransformer<R> transformer);

    <R> Observable<R> get(String endpoint, StringResponseToCollectionTransformer<R> transformer);

    <R> Observable<R> get(String endpoint, Map<String, String> headers, StringResponseToCollectionTransformer<R> transformer);

    <T> Observable<T> get(String endpoint, Map<String, String> headers, BufferTransformer<T> transformer);

    Observable<Buffer> get(String endpoint, Map<String, String> headers);

    Observable<Buffer> getAsBuffer(String endpoint);

    <T> Observable<T> get(String endpoint, BufferTransformer<T> transformer);

    Observable<HttpStatus> getHttpStatus(String endpointPath);

    Observable<HttpStatus> post(String endpoint);

    <R> Observable<R> getWithResponseTransformer(String endpoint, ResponseTransformer<R> transformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseTransformer<R> transformer);

    Observable<HttpStatus> post(String endpoint, String body);

    <R> Observable<R> post(String endpoint, ResponseBodyTransformer<R> bodyTransformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseBodyTransformer<R> bodyTransformer);

    Observable<Buffer> postBuffer(String endpoint);

    Observable<Buffer> postBuffer(String endpoint, AuthConfig authConfig);

    Observable<Buffer> postBuffer(String endpoint, String postBody, Optional<AuthConfig> authConfig);

    Observable<HttpStatus> delete(final String endpoint);

    public <R> Observable<R> postTarStream(final String endpoint, final Path pathToTarArchive, final BufferTransformer<R> transformer);
}
