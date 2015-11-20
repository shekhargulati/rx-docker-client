package com.shekhargulati.reactivex.docker.client.junit;

import com.shekhargulati.reactivex.docker.client.DockerClient;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequest;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerRequestBuilder;
import com.shekhargulati.reactivex.docker.client.representations.DockerContainerResponse;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Collections;

public class DockerContainerRule implements TestRule {

    private final DockerClient client;
    private final String containerName;
    private String containerId;

    public DockerContainerRule(DockerClient client, String containerName) {
        this.client = client;
        this.containerName = containerName;
    }

    @Override
    public Statement apply(Statement base, Description description) {

        if (description.getAnnotation(TestDockerContainer.class) != null) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    createContainer();
                    try {
                        base.evaluate();
                    } finally {
                        removeContainer();
                    }
                }
            };
        } else {
            return base;
        }
    }

    private String createContainer() {
        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu")
                .setCmd(Collections.singletonList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();
        DockerContainerResponse container = client.createContainer(request, containerName);
        this.containerId = container.getId();
        return containerId;
    }

    private void removeContainer() {
        try {
            client.removeContainer(containerId, false, false);
        } catch (Exception e) {
            // ignore as circle ci does not allow containers and images to be destroyed
        }
    }

    public String containerId() {
        return containerId;
    }
}
