package io.reactivex.docker.client.representations;

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
