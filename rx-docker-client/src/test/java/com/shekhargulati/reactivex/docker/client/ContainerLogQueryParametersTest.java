package com.shekhargulati.reactivex.docker.client;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class ContainerLogQueryParametersTest {

    @Test
    public void shouldSetDefaultValuesForContainerLogQueryParameter() throws Exception {
        ContainerLogQueryParameters queryParameters = ContainerLogQueryParameters.withDefaultValues();
        Assert.assertThat(queryParameters.toQueryParametersString(), equalTo("?stderr=true&stdout=true&timestamps=true&tail=all"));
    }


}