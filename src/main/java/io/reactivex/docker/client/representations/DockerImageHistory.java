package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerImageHistory {

    @SerializedName("Id")
    private String id;
    @SerializedName("Created")
    private String created;
    @SerializedName("CreatedBy")
    private String createdBy;
    @SerializedName("Tags")
    private List<String> tags;
    @SerializedName("Size")
    private Long size;
    @SerializedName("Comment")
    private String comment;

    public String getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public List<String> getTags() {
        return tags;
    }

    public Long getSize() {
        return size;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "DockerImageHistory{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", tags=" + tags +
                ", size=" + size +
                ", comment='" + comment + '\'' +
                '}';
    }
}
