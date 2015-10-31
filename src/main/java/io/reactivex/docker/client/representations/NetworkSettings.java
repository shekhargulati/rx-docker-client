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

package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class NetworkSettings {

    @SerializedName("IPAddress")
    private String ipAddress;
    @SerializedName("IPPrefixLen")
    private Integer ipPrefixLen;
    @SerializedName("Gateway")
    private String gateway;
    @SerializedName("Bridge")
    private String bridge;
    @SerializedName("PortMapping")
    private Map<String, Map<String, String>> portMapping;
    @SerializedName("Ports")
    private Map<String, List<PortBinding>> ports;
    @SerializedName("MacAddress")
    private String macAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getIpPrefixLen() {
        return ipPrefixLen;
    }

    public String getGateway() {
        return gateway;
    }

    public String getBridge() {
        return bridge;
    }

    public Map<String, Map<String, String>> getPortMapping() {
        return portMapping;
    }

    public Map<String, List<PortBinding>> getPorts() {
        return ports;
    }

    public String getMacAddress() {
        return macAddress;
    }
}