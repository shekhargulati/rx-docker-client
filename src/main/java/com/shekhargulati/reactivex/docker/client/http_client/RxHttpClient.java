/*
 * The MIT License
 *
 * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.shekhargulati.reactivex.docker.client.http_client;

import com.shekhargulati.reactivex.docker.client.AuthConfig;
import com.shekhargulati.reactivex.docker.client.function.*;
import okio.Buffer;
import rx.Observable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

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
     * @param endpoint Endpoint at which to make the GET call
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

    <T> Observable<T> getResponseStream(String endpoint, Map<String, String> headers, StringResponseTransformer<T> transformer);

    Observable<String> getResponseStream(String endpoint, Map<String, String> headers);

    Observable<Buffer> getResponseBufferStream(String endpoint);

    Observable<String> getResponseStream(String endpoint);

    <T> Observable<T> getResponseStream(String endpoint, StringResponseTransformer<T> transformer);

    Observable<HttpStatus> getResponseHttpStatus(String endpointPath);

    Observable<HttpStatus> post(String endpoint);

    <R> Observable<R> get(String endpoint, ResponseTransformer<R> transformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseTransformer<R> transformer);

    Observable<HttpStatus> post(String endpoint, String body);

    <R> Observable<R> post(String endpoint, ResponseBodyTransformer<R> bodyTransformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseBodyTransformer<R> bodyTransformer);

    Observable<String> postAndReceiveResponse(String endpoint);

    Observable<String> postAndReceiveResponse(String endpoint, AuthConfig authConfig, Predicate<String> errorChecker);

    Observable<String> postAndReceiveResponse(String endpoint, String postBody, Optional<AuthConfig> authConfig, Predicate<String> errorChecker);

    Observable<HttpStatus> delete(final String endpoint);

    public <R> Observable<R> postTarStream(final String endpoint, final Path pathToTarArchive, final BufferTransformer<R> transformer);
}
