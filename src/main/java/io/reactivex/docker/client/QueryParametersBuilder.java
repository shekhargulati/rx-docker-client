package io.reactivex.docker.client;

import java.util.HashMap;
import java.util.Map;

public class QueryParametersBuilder {
    private boolean all = false;
    private String since;
    private String before;
    private boolean size = false;
    private int limit;
    private Map<String, String> filters = new HashMap<>();

    public QueryParametersBuilder withAll(boolean all) {
        this.all = all;
        return this;
    }

    public QueryParametersBuilder withSince(String since) {
        this.since = since;
        return this;
    }

    public QueryParametersBuilder withBefore(String before) {
        this.before = before;
        return this;
    }

    public QueryParametersBuilder withSize(boolean size) {
        this.size = size;
        return this;
    }

    public QueryParametersBuilder withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryParametersBuilder withFilter(String key, String value) {
        this.filters.put(key, value);
        return this;
    }

    public QueryParameters createQueryParameters() {
        return new QueryParameters(all, since, before, size, limit, filters);
    }

    public static QueryParameters defaultQueryParameters() {
        return new QueryParameters();
    }
}