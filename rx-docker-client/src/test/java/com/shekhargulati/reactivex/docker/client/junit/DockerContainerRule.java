package com.shekhargulati.reactivex.docker.client.junit;

import com.shekhargulati.reactivex.docker.client.RxDockerClient;
import com.shekhargulati.reactivex.docker.client.representations.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DockerContainerRule implements TestRule {

    private final RxDockerClient client;
    private List<String> containerIds;

    public DockerContainerRule(RxDockerClient client) {
        this.client = client;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        try {
            CreateDockerContainer[] containerAnnotations = description.getTestClass().getDeclaredMethod(description.getMethodName()).getAnnotationsByType(CreateDockerContainer.class);
            if (containerAnnotations != null && containerAnnotations.length > 0) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        containerIds = createContainers(containerAnnotations);
                        try {
                            base.evaluate();
                        } finally {
                            containerIds.forEach(DockerContainerRule.this::removeContainer);
                        }
                    }
                };
            }
            return base;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> createContainers(CreateDockerContainer[] containers) {
        return Stream.of(containers).map(this::createAndStartContainer).map(DockerContainerResponse::getId).collect(toList());
    }

    private DockerContainerResponse createAndStartContainer(CreateDockerContainer c) {
        if (c.pullImage()) {
            client.pullImage("registry");
        }
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String hostPort : c.hostPorts()) {
            List<PortBinding> hostPortBinding = new ArrayList<>();
            hostPortBinding.add(PortBinding.of("0.0.0.0", hostPort));
            portBindings.put(hostPort, hostPortBinding);
        }
        final HostConfig hostConfig = new HostConfigBuilder().setPortBindings(portBindings).createHostConfig();

        DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage(c.image())
                .setCmd(Arrays.asList(c.command()))
                .setAttachStdin(c.attachStdin())
                .setTty(c.tty())
                .addExposedPort(c.exposedPorts())
                .setHostConfig(hostConfig)
                .createDockerContainerRequest();

        DockerContainerResponse response = client.createContainer(request, c.container());
        if (c.start()) {
            client.startContainer(response.getId());
        }
        return response;
    }


    private void removeContainer(String containerId) {
        try {
            client.removeContainer(containerId, false, true);
        } catch (Exception e) {
            // ignore as circle ci does not allow containers and images to be destroyed
        }
    }

    public List<String> containerIds() {
        return containerIds;
    }

    public String first() {
        if (containerIds.isEmpty()) {
            throw new NoSuchElementException("No container found");
        }
        return containerIds.get(0);
    }
}
