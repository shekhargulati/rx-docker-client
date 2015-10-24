package io.reactivex.docker.client;

import org.junit.Test;

public class BuildImageQueryParametersTest {

    @Test
    public void shouldBuildQueryParameterWithDefaultParameters() throws Exception {
        BuildImageQueryParameters queryParameters = new BuildImageQueryParameters("test/dockerfile", "http://abc.com");
        System.out.println(queryParameters.toQueryParameterString());

    }
}