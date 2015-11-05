package io.shekhar.reactivex.docker.client.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void shouldReturnTrueForNull() throws Exception {
        boolean emptyOrNull = Strings.isEmptyOrNull(null);
        assertThat(emptyOrNull, is(equalTo(true)));
    }

    @Test
    public void shouldReturnTrueForEmptyString() throws Exception {
        boolean emptyOrNull = Strings.isEmptyOrNull("");
        assertThat(emptyOrNull, is(equalTo(true)));
    }

    @Test
    public void shouldReturnTrueForEmptyWithSpacesString() throws Exception {
        boolean emptyOrNull = Strings.isEmptyOrNull("    ");
        assertThat(emptyOrNull, is(equalTo(true)));
    }

    @Test
    public void shouldReturnFalseWhenStringHasContent() throws Exception {
        boolean emptyOrNull = Strings.isEmptyOrNull("shekhar");
        assertThat(emptyOrNull, is(equalTo(false)));
    }
}