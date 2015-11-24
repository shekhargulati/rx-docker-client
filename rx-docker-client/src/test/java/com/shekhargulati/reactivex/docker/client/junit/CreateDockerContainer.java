package com.shekhargulati.reactivex.docker.client.junit;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CreateDockerContainers.class)
public @interface CreateDockerContainer {

    /**
     * Name for containers
     *
     * @return container names
     */
    public String container();

}




