package com.highgo.opendbt.experiment.manageer;

/**
 * @Description: 动态构建镜像
 * @Title: DockerImageBuilder
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/22 16:33
 */

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.File;

public class DockerImageBuilder {

  private final DockerClient dockerClient;

  public DockerImageBuilder() {
    // 创建 Docker 客户端配置
    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost("tcp://localhost:2375") // 根据你的 Docker 主机地址配置
      .build();

    // 创建自定义的 Docker HTTP 客户端
    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
      .dockerHost(config.getDockerHost())
      .sslConfig(config.getSSLConfig())
      .build();

    // 使用 Docker 客户端构建器创建 Docker 客户端
    dockerClient = DockerClientBuilder.getInstance(config)
      .withDockerHttpClient(httpClient)
      .build();
  }

  public String buildImage(String imageName, File dockerfileDirectory) {
    // 使用 Dockerfile 构建镜像
    BuildImageResultCallback callback = new BuildImageResultCallback() {
      @Override
      public void onNext(BuildResponseItem item) {
        super.onNext(item);
        System.out.println(item.getStream());
      }
    };

    BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(dockerfileDirectory)
      .withPull(true) // 如果需要，可以从 Docker Hub 拉取基础镜像
      .withTag(imageName);

    String imageId = buildImageCmd.exec(callback).awaitImageId();

    return imageId;
  }

  public void pushImage(String imageName) {
    // 推送镜像到 Docker 镜像仓库（如果需要）
    dockerClient.pushImageCmd(imageName)
      .withTag("latest")
      .exec(new PushImageResultCallback())
      .awaitSuccess();
  }

  public static void main(String[] args) {
    DockerImageBuilder imageBuilder = new DockerImageBuilder();

    // 构建镜像
    String imageName = "my-docker-image";
    File dockerfileDirectory = new File("/path/to/your/dockerfile/directory");
    String imageId = imageBuilder.buildImage(imageName, dockerfileDirectory);

    // 推送镜像到 Docker Hub（如果需要）
    imageBuilder.pushImage(imageName);

    System.out.println("Docker镜像名称: " + imageName);
    System.out.println("Docker镜像ID: " + imageId);
  }
}

