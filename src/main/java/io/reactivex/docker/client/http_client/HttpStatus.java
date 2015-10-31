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

package io.reactivex.docker.client.http_client;

public class HttpStatus {

    private final int code;
    private final String message;
    public static final HttpStatus OK = of(200, "OK");
    public static final HttpStatus NO_CONTENT = of(204, "No Content");
    public static final HttpStatus NOT_FOUND = of(404, "Not Found");
    public static final HttpStatus SERVER_ERROR = of(500, "Server Error");
    public static final HttpStatus BAD_REQUEST = of(400, "Bad Request");

    private HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus of(final int code, final String message) {
        return new HttpStatus(code, message);
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpStatus that = (HttpStatus) o;

        if (code != that.code) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
