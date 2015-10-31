package io.reactivex.docker.client.function;

import okio.Buffer;

import java.util.function.Function;

public interface BufferTransformer<T> extends Function<Buffer, T> {

    static BufferTransformer<Buffer> identityOp() {
        return t -> t;
    }
}
