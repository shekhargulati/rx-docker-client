package io.reactivex.docker.client.function;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import io.reactivex.docker.client.HttpStatus;

import java.io.IOException;

public interface ResponseTransformer<R> extends IoFunction<Response, R> {

    static ResponseTransformer<Response> identity() {
        return t -> t;
    }

    static ResponseTransformer<HttpStatus> httpStatus() {
        return response -> new HttpStatus(response.code(), response.message());
    }

    static <T> ResponseTransformer<T> fromBody(final ResponseBodyTransformer<T> bodyTransformer) {
        return response -> {
            try (ResponseBody body = response.body()) {
                return bodyTransformer.apply(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
