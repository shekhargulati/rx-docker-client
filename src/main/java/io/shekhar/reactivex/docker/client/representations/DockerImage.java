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

import java.util.List;

public class DockerImage {

    @SerializedName("Created")
    private String created;
    @SerializedName("Id")
    private String id;
    @SerializedName("ParentId")
    private String parentId;
    @SerializedName("RepoTags")
    private List<String> repoTags;
    @SerializedName("Size")
    private Long size;
    @SerializedName("VirtualSize")
    private Long virtualSize;

    public String created() {
        return created;
    }

    public String id() {
        return id;
    }

    public String parentId() {
        return parentId;
    }

    public List<String> repoTags() {
        return repoTags;
    }

    public Long size() {
        return size;
    }

    public Long virtualSize() {
        return virtualSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DockerImage that = (DockerImage) o;

        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (repoTags != null ? !repoTags.equals(that.repoTags) : that.repoTags != null) return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;
        if (virtualSize != null ? !virtualSize.equals(that.virtualSize) : that.virtualSize != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = created != null ? created.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (repoTags != null ? repoTags.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (virtualSize != null ? virtualSize.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DockerImage{" +
                "created='" + created + '\'' +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", repoTags=" + repoTags +
                ", size=" + size +
                ", virtualSize=" + virtualSize +
                '}';
    }
}
