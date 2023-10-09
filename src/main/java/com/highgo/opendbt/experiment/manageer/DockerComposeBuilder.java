package com.highgo.opendbt.experiment.manageer;

/**
 * @Description:
 * @Title: DockerComposeBuilder
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/1 11:23
 */

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.experiment.model.ContainerModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerComposeBuilder {
  private DockerClient dockerClient;
  private static final Log logger = LogFactory.getLog(DockerComposeBuilder.class);

  public DockerComposeBuilder() {
  }

  public DockerComposeBuilder(String ip, int port) {
    // 配置 Docker 客户端
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost("tcp://".concat(ip).concat(":").concat(port + "")).withApiVersion("v1.35").build();
    DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
      .withReadTimeout(10000)
      .withConnectTimeout(10000)
      .withMaxTotalConnections(100)
      .withMaxPerRouteConnections(10);
    dockerClient = DockerClientBuilder.getInstance(config)
      .withDockerCmdExecFactory(dockerCmdExecFactory)
      .build();
  }

  // 检查容器是否存在，如果存在返回容器的 ID，如果不存在返回 null
  public String checkContainerByName(String containerName) {
    List<Container> containers = dockerClient.listContainersCmd()
      .withShowAll(true)
      .exec();

    for (Container container : containers) {
      for (String name : container.getNames()) {
        // 去掉名称前面的 '/' 符号
        String formattedName = name.substring(1);
        if (formattedName.equals(containerName)) {
          return container.getId();
        }
      }
    }

    return null; // 如果找不到匹配的容器，则返回 null
  }

  // 创建容器并启动容器服务
  public ContainerModel createAndStartContainer(String composeYamlPath, String studentCode, String imageName, String userName, String key, String path, String dockerHost, int dockerPort) throws Exception {
    if (composeYamlPath == null) {
      throw new APIException("构建容器失败，docker-compose未找到！");
    }
    String containerName = studentCode.concat("_").concat(imageName.replace(":", "_")).concat("_container");
    String existingContainerId = checkContainerByName(containerName);

    ContainerModel model = new ContainerModel();
    if (existingContainerId != null) {
      // 如果容器已存在，直接启动
      startContainer(existingContainerId);
      model.setContainerId(existingContainerId);
      return model;
    } else {
      // 如果容器不存在，使用 Docker Compose 启动容器服务
      return buildAndRunCompose(containerName, composeYamlPath, studentCode, userName, key, path, dockerHost, dockerPort);
    }
  }

  // 启动容器
  private void startContainer(String containerId) {
    try {
      dockerClient.startContainerCmd(containerId).exec();
    } catch (Exception e) {
      logger.error("容器已经启动",e);
    }

  }

  // 关闭容器
  public void stopContainer(String containerId) {
    dockerClient.stopContainerCmd(containerId).exec();
    //dockerClient.removeContainerCmd(containerId).exec();
  }

  public ContainerModel buildAndRunCompose(String containerName, String composeYamlPath, String studentCode, String userName, String key, String path, String dockerHost, int dockerPort) throws Exception {
    // 构建 Docker Compose 配置
    String composeYamlContent;
    try {
      composeYamlContent = IOUtils.toString(new File(composeYamlPath).toURI(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new DockerClientException("无法读取 Docker Compose 文件", e);
    }

    // 使用 docker-compose 构建和运行服务
    String composeFilePath = createTemporaryComposeFile(composeYamlContent, studentCode);
    String fileName = UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).upload(path, composeFilePath);
    if (fileName == null) {
      throw new APIException("composeFile上传失败");
    }
    String dockerComposeCommand = "docker-compose -f " + path.concat(fileName) + " up -d";
    String result = UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).executeCommand(dockerComposeCommand);
    logger.info(result);
    String dockerIdCommand = " docker ps -q --filter " + "name=" + containerName;
    String containerId = UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).executeCommand(dockerIdCommand);
    logger.info("containerId=".concat(containerId));
    String dockerPortCommand = " docker port " + containerName;
    String port = UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).executeCommand(dockerPortCommand);
    if (port != null) {
      port = port.substring(port.lastIndexOf(":") + 1);
    }
    logger.info("port=".concat(port));
    ContainerModel model = new ContainerModel();
    model.setContainerId(containerId);
    model.setContainerPort(port);
    return model;
  }

  private String createTemporaryComposeFile(String composeYamlContent, String studentCode) {
    // 创建临时的 Docker Compose 配置文件
    String composeFileName = studentCode + "-docker-compose.yml";
    File composeFile = new File(composeFileName);

    try {
      FileUtils.writeStringToFile(composeFile, composeYamlContent, StandardCharsets.UTF_8);
      return composeFile.getAbsolutePath();
    } catch (IOException e) {
      e.printStackTrace();
      throw new DockerClientException("无法创建临时 Docker Compose 文件", e);
    }
  }

  /**
   * @description: 获取docker容器名称获取容器id
   * @author:
   * @date: 2023/9/1 13:43
   * @param: [containerName: 容器名称]
   * @return: void
   **/
  public String getContainerIdByName(String containerName) {
    List<Container> containers = dockerClient.listContainersCmd()
      .withShowAll(true)
      .exec();

    for (Container container : containers) {
      for (String name : container.getNames()) {
        // 去掉名称前面的 '/' 符号
        String formattedName = name.substring(1);
        if (formattedName.equals(containerName)) {
          return container.getId();
        }
      }
    }

    return null; // 如果找不到匹配的容器，则返回 null
  }


}
