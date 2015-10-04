package io.reactivex.docker.client.function;

import com.squareup.okhttp.ResponseBody;

public interface ResponseBodyTransformer<R> extends IoFunction<ResponseBody, R> {

}
