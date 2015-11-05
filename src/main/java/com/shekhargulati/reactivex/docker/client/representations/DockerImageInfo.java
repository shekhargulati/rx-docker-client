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

package com.shekhargulati.reactivex.docker.client.representations;

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
