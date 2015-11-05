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

package com.shekhargulati.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerContainer {
    @SerializedName("Id")
    private String id;
    @SerializedName("Names")
    private List<String> names;
    @SerializedName("Image")
    private String image;
    @SerializedName("Command")
    private String command;
    @SerializedName("Created")
    private Long created;
    @SerializedName("Status")
    private String status;
    @SerializedName("SizeRw")
    private Long sizeRw;
    @SerializedName("SizeRootFs")
    private Long sizeRootFs;

    public String getId() {
        return id;
    }

    public List<String> getNames() {
        return names;
    }

    public String getImage() {
        return image;
    }

    public String getCommand() {
        return command;
    }

    public Long getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public Long getSizeRw() {
        return sizeRw;
    }

    public Long getSizeRootFs() {
        return sizeRootFs;
    }

    @Override
    public String toString() {
        return "DockerContainer{" +
                "id='" + id + '\'' +
                ", names=" + names +
                ", image='" + image + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
