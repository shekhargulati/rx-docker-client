package io.reactivex.docker.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageListQueryParameters {
    private Optional<String> imageName = Optional.empty();
    private Map<String, String> filters = new HashMap<>();
    private boolean all = false;

    public static ImageListQueryParameters queryParameterWithImageName(String imageName) {
        return new ImageListQueryParameters(imageName);
    }

    public static ImageListQueryParameters allImages() {
        return new ImageListQueryParameters(true);
    }

    public static ImageListQueryParameters defaultQueryParameters() {
        return new ImageListQueryParameters();
    }

    private ImageListQueryParameters() {
    }

    public ImageListQueryParameters(String imageName, Map<String, String> filters, boolean all) {
        this.imageName = Optional.ofNullable(imageName);
        this.filters = filters;
        this.all = all;
    }

    private ImageListQueryParameters(String imageName) {
        this.imageName = Optional.ofNullable(imageName);
    }

    private ImageListQueryParameters(boolean all) {
        this.all = all;
    }

    public ImageListQueryParameters addFilter(String key, String value) {
        filters.put(key, value);
        return this;
    }

    public Optional<String> getImageName() {
        return imageName;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public boolean isAll() {
        return all;
    }

    public String toQuery() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("all=" + all);
        queryBuilder.append("&");
        if (imageName.isPresent()) {
            queryBuilder.append("filter=" + imageName.get());
            queryBuilder.append("&");
        }
        queryBuilder.append(this.filters.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining(";")));
        String queryStr = queryBuilder.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.lastIndexOf("&"));
        }
        return queryStr;
    }


}
