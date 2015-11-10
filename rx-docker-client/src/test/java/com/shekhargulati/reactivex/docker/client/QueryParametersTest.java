package com.shekhargulati.reactivex.docker.client;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryParametersTest {

    @Test
    public void shouldBuildQueryWhenNoQueryParameterIsApplied() throws Exception {
        QueryParametersBuilder builder = new QueryParametersBuilder();
        String expectedQuery = "?all=false&size=false";
        QueryParameters queryParameters = builder.createQueryParameters();
        assertThat(queryParameters.toQuery(), equalTo(expectedQuery));
    }

    @Test
    public void shouldBuildQueryWhenQueryParameterPresent() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().
                withAll(true).
                withLimit(5).
                withSize(true).
                createQueryParameters();

        String expectedQuery = "?all=true&size=true&limit=5";
        assertThat(queryParameters.toQuery(), equalTo(expectedQuery));
    }

    @Test
    public void shouldBuildQueryWithBefore() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().
                withBefore("beforeId").createQueryParameters();

        String expectedQuery = "?all=false&size=false&before=beforeId";
        assertThat(queryParameters.toQuery(), equalTo(expectedQuery));
    }

    @Test
    public void shouldBuildQueryWithFilters() throws Exception {
        QueryParameters queryParameters = new QueryParametersBuilder().
                withFilter("status", "created").
                withFilter("exited", "1").
                createQueryParameters();

        String expectedQuery = "?all=false&size=false&exited=1;status=created";
        assertThat(queryParameters.toQuery(), equalTo(expectedQuery));
    }
}