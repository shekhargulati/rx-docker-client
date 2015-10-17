package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

public class DockerImageInfo {

    @SerializedName("description")
    private String description;
    @SerializedName("is_official")
    private boolean official;
    @SerializedName("is_automated")
    private boolean automated;
    @SerializedName("name")
    private String name;
    @SerializedName("star_count")
    private int starCount;

    public String getDescription() {
        return description;
    }

    public boolean isOfficial() {
        return official;
    }

    public boolean isAutomated() {
        return automated;
    }

    public String getName() {
        return name;
    }

    public int getStarCount() {
        return starCount;
    }

    @Override
    public String toString() {
        return "DockerImageInfo{" +
                "description='" + description + '\'' +
                ", official=" + official +
                ", automated=" + automated +
                ", name='" + name + '\'' +
                ", starCount=" + starCount +
                '}';
    }
}
