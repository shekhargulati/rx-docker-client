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

import java.util.Optional;

public class BuildImageQueryParameters {
    private Optional<String> dockerFile = Optional.empty();
    private Optional<String> remote = Optional.empty();

    public static BuildImageQueryParameters withRemoteDockerfile(final String remote) {
        return new BuildImageQueryParameters(null, remote);
    }

    private BuildImageQueryParameters() {
        this(null, null);
    }

    public BuildImageQueryParameters(String dockerFile) {
        this(dockerFile, null);
    }

    public BuildImageQueryParameters(String dockerFile, String remote) {
        this.dockerFile = Optional.ofNullable(dockerFile);
        this.remote = Optional.ofNullable(remote);
    }

    public String toQueryParameterString() {
        Optional<StringBuilder> queryBuilder = Optional.of(new StringBuilder("&"))
                .flatMap(qb -> Optional.ofNullable(dockerFile.map(df -> qb.append("dockerfile=").append(df).append("&")).orElse(qb)))
                .flatMap(qb -> Optional.ofNullable(remote.map(r -> qb.append("remote=").append(r).append("&")).orElse(qb)));
        return queryBuilder
                .filter(qb -> qb.toString().endsWith("&"))
                .map(StringBuilder::toString)
                .map(qb -> qb.substring(0, qb.lastIndexOf("&")))
                .orElse("");
    }

    public static BuildImageQueryParameters withDefaultValues() {
        return new BuildImageQueryParameters();
    }
}

