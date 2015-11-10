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

package com.shekhargulati.reactivex.docker.client;

import java.time.Instant;
import java.util.Optional;

public class ContainerLogQueryParameters {
    private boolean stderr = true;
    private boolean stdout = true;
    private boolean timestamps = true;
    private int tail = -1;
    private Optional<Instant> since = Optional.empty();

    private static final ContainerLogQueryParameters DEFAULT = new ContainerLogQueryParameters();

    public static ContainerLogQueryParameters withDefaultValues() {
        return DEFAULT;
    }

    private ContainerLogQueryParameters() {
    }

    public ContainerLogQueryParameters(boolean stderr, boolean stdout, boolean timestamps, int tail, Instant since) {
        this.stderr = stderr;
        this.stdout = stdout;
        this.timestamps = timestamps;
        this.tail = tail;
        this.since = Optional.ofNullable(since);
    }

    public String toQueryParametersString() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("stderr=" + stderr);
        queryBuilder.append("&");
        queryBuilder.append("stdout=" + stdout);
        queryBuilder.append("&");
        queryBuilder.append("timestamps=" + timestamps);
        queryBuilder.append("&");
        if (tail < 0) {
            queryBuilder.append("tail=all");
        } else {
            queryBuilder.append("tail=" + tail);
        }
        queryBuilder.append("&");
        if (since.isPresent()) {
            queryBuilder.append("since=" + since.get().getEpochSecond());
        }
        String queryStr = queryBuilder.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.lastIndexOf("&"));
        }
        return queryStr;
    }
}
