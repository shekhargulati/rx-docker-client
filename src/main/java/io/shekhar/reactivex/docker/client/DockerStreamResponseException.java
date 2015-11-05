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

package io.shekhar.reactivex.docker.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DockerStreamResponseException extends RuntimeException {

    private final String error;
    private final String message;

    public DockerStreamResponseException(String json) {
        Type type = new TypeToken<DockerErrorDetails>() {
        }.getType();
        DockerErrorDetails details = new Gson().fromJson(json, type);
        this.error = details.getError();
        this.message = details.getErrorDetail().getMessage();
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}

class DockerErrorDetails {
    private ErrorDetails errorDetail;
    private String error;

    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    public String getError() {
        return error;
    }
}

class ErrorDetails {
    private String message;

    public String getMessage() {
        return message;
    }
}
