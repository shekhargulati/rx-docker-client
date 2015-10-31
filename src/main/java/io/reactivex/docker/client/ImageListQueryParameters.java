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

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ImageListQueryParameters {
    private Optional<String> imageName = Optional.empty();
    private Map<String, List<String>> filters = new HashMap<>();
    private boolean all = false;

    public static ImageListQueryParameters queryParameterWithImageName(String imageName) {
        return new ImageListQueryParameters(imageName);
    }

    public static ImageListQueryParameters allImagesQueryParameters() {
        return new ImageListQueryParameters(true);
    }

    public static ImageListQueryParameters defaultQueryParameters() {
        return new ImageListQueryParameters();
    }

    private ImageListQueryParameters() {
    }

    public ImageListQueryParameters(String imageName, boolean all) {
        this.imageName = Optional.ofNullable(imageName);
        this.all = all;
    }

    private ImageListQueryParameters(String imageName) {
        this.imageName = Optional.ofNullable(imageName);
    }

    private ImageListQueryParameters(boolean all) {
        this.all = all;
    }

    public ImageListQueryParameters addFilter(String key, String value) {
        filters.put(key, filters.compute(key, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();

            }
            v.add(value);
            return v;
        }));
        return this;
    }

    public String toQuery() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("all=" + all);
        queryBuilder.append("&");
        if (imageName.isPresent()) {
            queryBuilder.append("filter=" + imageName.get());
            queryBuilder.append("&");
        }
        if (!filters.isEmpty()) {
            String json = new Gson().toJson(filters);
            try {
                final String encoded = URLEncoder.encode(json, UTF_8.name());
                queryBuilder.append("filters=" + encoded);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(String.format("unable to encode filter %s", filters));
            }
        }
        String queryStr = queryBuilder.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.lastIndexOf("&"));
        }
        return queryStr;
    }


}
