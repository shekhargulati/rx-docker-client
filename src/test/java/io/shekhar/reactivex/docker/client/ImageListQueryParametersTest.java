package io.shekhar.reactivex.docker.client;

import org.junit.Test;

import static io.shekhar.reactivex.docker.client.ImageListQueryParameters.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImageListQueryParametersTest {

    @Test
    public void defaultQueryForListImages() throws Exception {
        ImageListQueryParameters queryParameters = defaultQueryParameters();
        assertThat(queryParameters.toQuery(), equalTo("?all=false"));
    }

    @Test
    public void queryForLisAllImages() throws Exception {
        ImageListQueryParameters queryParameters = allImagesQueryParameters();
        assertThat(queryParameters.toQuery(), equalTo("?all=true"));
    }

    @Test
    public void queryForImageWithSpecificName() throws Exception {
        ImageListQueryParameters queryParameters = queryParameterWithImageName("busybox");
        assertThat(queryParameters.toQuery(), equalTo("?all=false&filter=busybox"));

    }

    @Test
    public void shouldEncodeFilters() throws Exception {
        ImageListQueryParameters queryParameters = defaultQueryParameters().addFilter("dangling", "true");
        assertThat(queryParameters.toQuery(), equalTo("?all=false&filters=%7B%22dangling%22%3A%5B%22true%22%5D%7D"));
    }


}