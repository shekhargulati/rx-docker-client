package io.reactivex.docker.client;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BuildImageQueryParametersTest {

    @Test
    public void shouldReturnEmptyStringForDefaultQueryParameters() throws Exception {
        BuildImageQueryParameters defaultQueryParameters = BuildImageQueryParameters.withDefaultValues();
        assertThat(defaultQueryParameters.toQueryParameterString(), equalTo(""));
    }

    @Test
    public void shouldBuildQueryParameterWithBothQueryParameters() throws Exception {
        BuildImageQueryParameters queryParameters = new BuildImageQueryParameters("test/dockerfile", "http://abc.com");
        assertThat(queryParameters.toQueryParameterString(), equalTo("&dockerfile=test/dockerfile&remote=http://abc.com"));
    }

    @Test
    public void shouldBuildQueryParameterWithDockerfileOption() throws Exception {
        BuildImageQueryParameters queryParameters = new BuildImageQueryParameters("abc/abc");
        assertThat(queryParameters.toQueryParameterString(), equalTo("&dockerfile=abc/abc"));
    }
}