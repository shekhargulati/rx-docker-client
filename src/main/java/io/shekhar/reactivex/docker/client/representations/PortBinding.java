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

package io.shekhar.reactivex.docker.client.representations;


import com.google.gson.annotations.SerializedName;

public class PortBinding {

    @SerializedName("HostIp")
    private String hostIp;
    @SerializedName("HostPort")
    private String hostPort;

    public String hostIp() {
        return hostIp;
    }

    public void hostIp(final String hostIp) {
        this.hostIp = hostIp;
    }

    public String hostPort() {
        return hostPort;
    }

    public void hostPort(final String hostPort) {
        this.hostPort = hostPort;
    }

    public static PortBinding of(final String ip, final String port) {
        final PortBinding binding = new PortBinding();
        binding.hostIp(ip);
        binding.hostPort(port);
        return binding;
    }

    public static PortBinding of(final String ip, final int port) {
        return of(ip, String.valueOf(port));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PortBinding that = (PortBinding) o;

        if (hostIp != null ? !hostIp.equals(that.hostIp) : that.hostIp != null) {
            return false;
        }
        if (hostPort != null ? !hostPort.equals(that.hostPort) : that.hostPort != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = hostIp != null ? hostIp.hashCode() : 0;
        result = 31 * result + (hostPort != null ? hostPort.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PortBinding{" +
                "hostIp='" + hostIp + '\'' +
                ", hostPort='" + hostPort + '\'' +
                '}';
    }
}
