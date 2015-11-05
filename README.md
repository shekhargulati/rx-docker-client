Reactive Docker Java REST API Client
=========

rx-docker-client is a Java REST API client for Docker REST API(http://docs.docker.com/engine/reference/api/docker_remote_api/). The API uses JDK 8. It makes use of following awesome libraries to get the job done:

1. RxJava
2. OKHttp
3. Gson

## Table of Contents
* [Why](#why)
* [Getting Started](#getting-started)
* [Usage](#usage)
* [License](#license)

Why
----

Most of the existing Java Docker client are synchronous in nature. This API makes use of Reactive programming paradigm so every API call returns an Observable. It allows you to compose REST API calls together. You can use functional methods like `map`, `filter`,`flatmap`, `zip`, etc on the returned Observable. You can decide whether you want to subscribe on the main thread or use a thread from the thread pool and get results on it. It provides a higher fluent abstraction that you can use to write composable programs.

To show the power of rx-docker-client API, let's suppose you want to search **ubuntu** image and then pull the image corresponding to the first search result. You can do this very easily with rx-docker-client as shown below.

```java
DockerClient client = DockerClient.fromDefaultEnv();
client.searchImagesObs("ubuntu")
          .first()
          .flatMap(imageInfo -> client.pullImageObs(imageInfo.getName()))
          .subscribe(System.out::println);
```

In the code shown above, `first`, `flatMap`, and `subscribe` are all RxJava operators. The `searchImagesObs` and `pullImageObs` returns Observable so we can compose different functions together.

Getting Started
--------

To use rx-docker-client in your application, you have to add `rx-docker-client` in your classpath.

For Apache Maven users, please add following to your pom.xml.

```xml

```

Gradle users can add following to their build.gradle file.

```
```

Usage
-----
For most of the operations rx-docker-client provide both the blocking and non-blocking methods. The non-blocking methods returns an Observable so you can compose functions together.

### Blocking methods

Below are few examples of some of the blocking methods. When you make call to these methods they will return when they have result.

```java
//Create a new Docker client using DOCKER_HOST and DOCKER_CERT_PATH environment variables
DockerClient client = DockerClient.fromDefaultEnv();

// Getting Docker version
DockerVersion dockerVersion = client.serverVersion();
System.out.println(dockerVersion.version()); // 1.8.3

DockerInfo info = client.info();
System.out.println(info.images()); // 40

HttpStatus ping = client.ping();
System.out.println(ping); // HttpStatus{code=200, message='OK'}

// Pull image
HttpStatus successPullImage = client.pullImage("ubuntu");
System.out.println("On Success HTTP Code will be 200");
```

### non-blocking methods

It is suggested that you use methods which return an Observable as they allow you to write non-blocking and composable code. All the non-blocking methods name ends with **Obs**.

#### To pull image

```java
//Create a new Docker client using DOCKER_HOST and DOCKER_CERT_PATH environment variables
DockerClient client = DockerClient.fromDefaultEnv();

// pull the latest image from Docker Hub
Observable<String> pullImageObs = client.pullImageObs("busybox");
        pullImageObs.subscribe(System.out::println,
                e -> System.err.println("Encountered exception >> " + e.getMessage()),
                () -> System.out.println("Successfully completed"));
```

### Create and start container

```java
DockerContainerRequest request = new DockerContainerRequestBuilder()
                .setImage("ubuntu:latest")
                .setCmd(Arrays.asList("/bin/bash"))
                .setAttachStdin(true)
                .setTty(true)
                .createDockerContainerRequest();

String container = "my_first_container";
client.createContainerObs(request, container)
        .flatMap(res -> client.startContainerObs(res.getId()))
        .subscribe(System.out::println);
```

### Container Stats

```java
Observable<ContainerStats> containerStatsObservable = client.containerStatsObs(containerId);
Subscriber<ContainerStats> containerStatsSubscriber = new Subscriber<ContainerStats>() {

    @Override
    public void onCompleted() {
        logger.info("Successfully received all the container stats for container with id {}", containerId);
    }

    @Override
    public void onError(Throwable e) {
        logger.error("Error encountered while processing container stats for container with id {}", containerId);
    }

    @Override
    public void onNext(ContainerStats msg) {
        logger.info("Received a new message for container '{}'", containerId);
    }
};
```
### View Container logs

```java
Observable<String> logsObs = client.containerLogsObs(containerId);
logsObs.subscribe(System.out::println,
        e -> System.err.println("Encountered exception >> " + e.getMessage()),
        () -> System.out.println("Successfully completed"));
```

### Build image

```java
Observable<String> buildImageObs = client.buildImageObs("shekhargulati/my_hello_world_image",
                Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));
buildImageObs.subscribe(System.out::println);
```

### Building and pushing image

```java
String image = "shekhar007/my_hello_world_image";

Observable<String> buildImageObs = client.buildImageObs(image, Paths.get("src", "test", "resources", "images", "my_hello_world_image.tar"));

buildImageObs.subscribe(System.out::println,
        e -> System.err.println("Encountered exception >> " + e.getMessage()),
        () -> System.out.println("Successfully completed"));

client.pushImageObs(image, AuthConfig.authConfig("xxxx", "xxx", "xxx")).subscribe(System.out::println,
        e -> System.err.println("Encountered exception >> " + e.getMessage()),
        () -> System.out.println("Successfully completed"));
```


### Misc functions

```java
client.removeImage("hello-world");
client.removeDanglingImages();
client.listDanglingImages();
client.removeImages(dockerImage -> dockerImage.repoTags().stream().anyMatch(repo -> repo.contains("test_rx_docker")));
client.removeAllContainers();
```

License
-------
rx-docker-client is licensed under the MIT License - see the `LICENSE` file for details.
