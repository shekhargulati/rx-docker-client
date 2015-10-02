package io.reactivex.docker.client;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ContainerLogQueryParametersTest {

    @Test
    public void shouldSetDefaultValuesForContainerLogQueryParameter() throws Exception {
        ContainerLogQueryParameters queryParameters = ContainerLogQueryParameters.withDefaultValues();
        assertTrue(queryParameters.isStderr());
        assertTrue(queryParameters.isStdout());
        assertTrue(queryParameters.isTimestamps());
        assertThat(queryParameters.getTail(), equalTo(10));
        assertTrue(Instant.ofEpochSecond(queryParameters.getSince()).isBefore(Instant.now().minus(1, ChronoUnit.MINUTES)));
    }

    @Test
    public void shouldProduceQueryStringFromDefaultContainerLogQueryParameter() throws Exception {
        ContainerLogQueryParameters queryParameters = ContainerLogQueryParameters.withDefaultValues();
        System.out.println(queryParameters.toQueryString().get());

    }
}