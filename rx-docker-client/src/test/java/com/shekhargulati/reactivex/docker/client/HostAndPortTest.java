package com.shekhargulati.reactivex.docker.client;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HostAndPortTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseValidHostPortString() throws Exception {
        String hostPortString = "192.168.99.100:2376";
        HostAndPort hostAndPort = HostAndPort.from(hostPortString);
        assertThat(hostAndPort.getHost(), is(equalTo("192.168.99.100")));
        assertThat(hostAndPort.getPort(), is(equalTo(2376)));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenHostPortStringIsInvalid() throws Exception {
        String hostPortString = "test";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("test should be of format host:port for example 192.168.99.100:2376"));
        HostAndPort.from(hostPortString);

    }
}