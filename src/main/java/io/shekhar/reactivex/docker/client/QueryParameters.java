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

package io.shekhar.reactivex.docker.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryParameters {

    private boolean all;
    private Optional<String> since = Optional.empty();
    private Optional<String> before = Optional.empty();
    private boolean size = false;
    private int limit;
    private Map<String, String> filters = new HashMap<>();
    private String query;

    public QueryParameters() {
        this.query = createQuery();
    }

    public QueryParameters(boolean all, String since, String before, boolean size, int limit, Map<String, String> filters) {
        this.all = all;
        this.since = Optional.ofNullable(since);
        this.before = Optional.ofNullable(before);
        this.size = size;
        this.limit = limit;
        this.filters = filters;
        this.query = createQuery();
    }

    private String createQuery() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("all=" + all);
        queryBuilder.append("&");
        queryBuilder.append("size=" + size);
        queryBuilder.append("&");
        if (since.isPresent()) {
            queryBuilder.append("since=" + since.get());
            queryBuilder.append("&");
        }
        if (before.isPresent()) {
            queryBuilder.append("before=" + before.get());
            queryBuilder.append("&");
        }
        if (limit > 1) {
            queryBuilder.append("limit=" + limit);
            queryBuilder.append("&");
        }
        queryBuilder.append(this.filters.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining(";")));
        String queryStr = queryBuilder.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.lastIndexOf("&"));
        }
        return queryStr;
    }

    public boolean isAll() {
        return all;
    }

    public Optional<String> getSince() {
        return since;
    }

    public Optional<String> getBefore() {
        return before;
    }

    public boolean isSize() {
        return size;
    }

    public int getLimit() {
        return limit;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public String toQuery() {
        return query;
    }
}
