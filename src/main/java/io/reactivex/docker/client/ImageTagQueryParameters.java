package io.reactivex.docker.client;

public class ImageTagQueryParameters {

    private final String repo;
    private final String tag;
    private boolean force = false;

    private ImageTagQueryParameters(String repo, String tag) {
        this.repo = repo;
        this.tag = tag;
    }

    public static ImageTagQueryParameters with(String repo, String tag) {
        return new ImageTagQueryParameters(repo, tag);
    }

    public ImageTagQueryParameters withForce() {
        this.force = true;
        return this;
    }

    public String toQuery() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("repo=" + repo);
        queryBuilder.append("&");
        queryBuilder.append("force=" + force);
        queryBuilder.append("&");
        queryBuilder.append("tag=" + tag);
        return queryBuilder.toString();
    }
}
