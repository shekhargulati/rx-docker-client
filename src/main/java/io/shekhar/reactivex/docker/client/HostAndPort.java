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

import io.shekhar.reactivex.docker.client.utils.Strings;
import io.shekhar.reactivex.docker.client.utils.Validations;

public final class HostAndPort {

    private final String host;
    private final int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static HostAndPort using(String host, int port) {
        return new HostAndPort(host, port);
    }

    public static HostAndPort from(String hostPortString) {
        Validations.validate(hostPortString, Strings::isEmptyOrNull, "hostPortString can't be null");
        String endpointWithoutScheme = hostPortString.replaceAll(".*://", "");
        String[] split = endpointWithoutScheme.split(":");
        Validations.validate(split, arr -> arr.length != 2, String.format("%s should be of format host:port for example 192.168.99.100:2376", hostPortString));
        return new HostAndPort(split[0], Integer.parseInt(split[1]));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


}
