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
    String container();

    /**
     * Command to run
     *
     * @return command
     */
    String[] command() default "/bin/bash";

    String image() default "ubuntu";

    boolean attachStdin() default true;

    boolean tty() default true;

    boolean start() default false;

    boolean pullImage() default false;

    String[] exposedPorts() default {};

    String[] hostPorts() default {};

    String[] volumes() default {};
}




