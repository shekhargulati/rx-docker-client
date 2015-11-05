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

import io.shekhar.reactivex.docker.client.http_client.HttpStatus;
import io.shekhar.reactivex.docker.client.representations.DockerInfo;
import io.shekhar.reactivex.docker.client.representations.DockerVersion;
import rx.Observable;

public interface MiscOperations {

    String VERSION_ENDPOINT = "version";
    String INFO_ENDPOINT = "info";
    String CHECK_AUTH_ENDPOINT = "auth";
    String PING_ENDPOINT = "_ping";

    Observable<DockerVersion> serverVersionObs();

    DockerVersion serverVersion();

    Observable<DockerInfo> infoObs();

    DockerInfo info();

    HttpStatus checkAuth(AuthConfig authConfig);

    Observable<HttpStatus> checkAuthObs(AuthConfig authConfig);

    HttpStatus ping();

}
