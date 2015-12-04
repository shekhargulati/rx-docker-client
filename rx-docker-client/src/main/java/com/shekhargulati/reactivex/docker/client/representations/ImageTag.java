/*
 *
 *  * The MIT License
 *  *
 *  * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.shekhargulati.reactivex.docker.client.representations;

import java.util.Optional;

/**
 * Created by shekhargulati on 04/12/15.
 */
public class ImageTag {

    private final String image;
    private final Optional<String> tag;

    private ImageTag(String image, String tag) {
        this.image = image;
        this.tag = Optional.ofNullable(tag);
    }

    private ImageTag(String image) {
        this(image, null);
    }

    public static ImageTag of(String image, String tag) {
        return new ImageTag(image, tag);
    }

    public static ImageTag of(String image) {
        return new ImageTag(image);
    }


    public String getImage() {
        return image;
    }

    public Optional<String> getTag() {
        return tag;
    }
}
