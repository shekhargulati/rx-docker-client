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