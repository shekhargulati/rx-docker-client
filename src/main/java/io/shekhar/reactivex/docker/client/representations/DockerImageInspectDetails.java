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

import java.util.Date;

public class DockerImageInspectDetails {
    @SerializedName("Id")
    private String id;
    @SerializedName("Parent")
    private String parent;
    @SerializedName("Comment")
    private String comment;
    @SerializedName("Created")
    private Date created;
    @SerializedName("Container")
    private String container;
    @SerializedName("ContainerConfig")
    private DockerContainerRequest containerConfig;
    @SerializedName("DockerVersion")
    private String dockerVersion;
    @SerializedName("Author")
    private String author;
    @SerializedName("Config")
    private DockerContainerRequest config;
    @SerializedName("Architecture")
    private String architecture;
    @SerializedName("Os")
    private String os;
    @SerializedName("Size")
    private Long size;
    @SerializedName("VirtualSize")
    private Long virtualSize;

    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public String getComment() {
        return comment;
    }

    public Date getCreated() {
        return created;
    }

    public String getContainer() {
        return container;
    }

    public DockerContainerRequest getContainerConfig() {
        return containerConfig;
    }

    public String getDockerVersion() {
        return dockerVersion;
    }

    public String getAuthor() {
        return author;
    }

    public DockerContainerRequest getConfig() {
        return config;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOs() {
        return os;
    }

    public Long getSize() {
        return size;
    }

    public Long getVirtualSize() {
        return virtualSize;
    }

    @Override
    public String toString() {
        return "DockerImageInspectDetails{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", comment='" + comment + '\'' +
                ", created=" + created +
                ", container='" + container + '\'' +
                ", dockerVersion='" + dockerVersion + '\'' +
                ", author='" + author + '\'' +
                ", architecture='" + architecture + '\'' +
                ", os='" + os + '\'' +
                ", size=" + size +
                ", virtualSize=" + virtualSize +
                '}';
    }
}
