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
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.experiment.model.ContainerModel;
import com.highgo.opendbt.experiment.service.TContainersService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class DockerComposeBuilder {
  private DockerClient dockerClient;
  private static final Log logger = LogFactory.getLog(DockerComposeBuilder.class);

  @Autowired
  private TContainersService containersService;

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
  public ContainerModel createAndStartContainer(String composeYamlPath, Long experimentId, String courseId, String studentCode, String imageName, String userName, String key, String path, String dockerHost, int port) throws Exception {
    if (composeYamlPath == null) {
      throw new APIException("构建容器失败，docker-compose未找到！");
    }
    String containerName = experimentId + "_" + courseId.concat("_").concat(studentCode.concat("_")).concat(imageName.replace(":", "_").replace("/", "_")).concat("_container");
    String existingContainerId = checkContainerByName(containerName);

    ContainerModel model = new ContainerModel();
    if (existingContainerId != null) {
      // 如果容器已存在，直接启动
      //startContainer(existingContainerId);
      startContainer(experimentId, courseId, studentCode, imageName, userName, key, path, dockerHost, port);
      model.setContainerId(existingContainerId);
      model.setContainerName(containerName);
      return model;
    } else {
      // 如果容器不存在，使用 Docker Compose 启动容器服务
      return buildAndRunCompose(containerName, composeYamlPath, studentCode, userName, key, path, dockerHost, port);
    }
  }

  // 启动容器
  private void startContainer(String containerId) {
    try {
      dockerClient.startContainerCmd(containerId).exec();
    } catch (Exception e) {
      logger.error("容器已经启动", e);
    }
  }

  //使用docker-compose启动容器
  private void startContainer(Long experimentId, String courseId, String studentCode, String imageName, String userName, String key, String path, String dockerHost, int dockerPort) throws Exception {
    String containerName = experimentId + "_" + courseId.concat("_").concat(studentCode.concat("_")).concat(imageName.replace(":", "_").replace("/", "_")).concat("-docker-compose.yml");
    String dockerComposeCommand = "docker-compose " + "  --compatibility " + "-f " + path.concat(containerName) + " up -d";
    UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).executeCommand(dockerComposeCommand);
  }

  // 关闭容器
  public void stopContainer(String containerId) {
    try {
      dockerClient.stopContainerCmd(containerId).exec();
    } catch (NotModifiedException e) {
      int httpStatus = e.getHttpStatus();
      if (httpStatus == 304) {
        throw new APIException("容器已在关闭状态！");
      }
      logger.error(e);
    }

  }


  // 删除容器
  public void deleteContainer(String containerId) {
    try {
      // 删除容器
      dockerClient.removeContainerCmd(containerId).withForce(true).exec();
      logger.info("容器删除成功.");
    } catch (NotFoundException e) {
      throw new APIException("容器不存在或已被删除.");
    }
  }

  // 备份容器数据
  public String backupContainerWithData(String containerName, String imageName, String dataBackupPath) {
    try {
      logger.info("容器数据开始备份，备份文件路径: " + dataBackupPath);
      // 创建容器快照
      //CommitCmd commitCmd = dockerClient.commitCmd(containerName).withRepository(imageName);
      //String imageId = commitCmd.exec();
      //logger.info("容器备份成功，镜像 ID: " + imageId);
      // 备份容器内的数据
      backupContainerData(containerName, dataBackupPath);
      logger.info("容器数据备份成功，备份文件路径: " + dataBackupPath);
      return imageName;
    } catch (Exception e) {
      logger.error("容器备份失败: " + e.getMessage());
      throw new APIException("容器备份失败: " + e.getMessage());
    }
  }

  // 备份容器内的数据
  private void backupContainerData(String imageId, String backupPath) {
//    try (InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(imageId, "/").exec();
//         FileOutputStream outputStream = new FileOutputStream(new File(backupPath.concat(File.separator).concat("backup_data.tar")))) {
//      byte[] buffer = new byte[4096];
//      int bytesRead;
//      while ((bytesRead = inputStream.read(buffer)) != -1) {
//        outputStream.write(buffer, 0, bytesRead);
//      }

    try (InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(imageId, "/").exec();
         FileOutputStream outputStream = new FileOutputStream(new File(backupPath, "backup_data.tar"));
         ReadableByteChannel inChannel = Channels.newChannel(inputStream);
         FileChannel outChannel = outputStream.getChannel()) {
         outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
    } catch (IOException e) {
      logger.error("容器备份失败: " + e.getMessage());
      throw new APIException("容器备份失败: " + e.getMessage());
    }
  }

  // 恢复容器及其数据
  public String restoreContainerWithData(String newContainerName, String imageName, String dataBackupPath, String userName, String key, String path, String dockerHost, int port) {
    try {
      String existingContainerId = checkContainerByName(newContainerName);
      if (existingContainerId != null) {
        logger.info("删除原有容器.");
        //删除原有容器
        this.deleteContainer(newContainerName);
      }

//      CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageName)
//        .withName(newContainerName)
//        .exec();
//      // 启动新容器
//      dockerClient.startContainerCmd(containerResponse.getId()).exec();
      // 获取新容器的容器 ID
      //String newContainerId = containerResponse.getId();

      // 创建并启动新容器,获取新容器的容器 ID
       startContainerByCompose(newContainerName, userName, key, path, dockerHost, port);
      String newContainerId = getContainerIdByName(newContainerName);
      logger.info("新容器 ID：" + newContainerId);
      logger.info("容器恢复成功.");
      // 恢复容器内的数据
      restoreContainerData(newContainerName, dataBackupPath.concat(File.separator).concat("backup_data.tar"), newContainerId);
      logger.info("容器数据恢复成功.");
      return newContainerId;
    } catch (Exception e) {
      logger.error("容器恢复失败: " + e.getMessage());
      throw new APIException("容器恢复失败: " + e.getMessage());
    }
  }

  private void startContainerByCompose(String newContainerName, String userName, String key, String path, String dockerHost, int dockerPort) throws Exception {
    String composeName = newContainerName.replace("_container", "-docker-compose.yml");
    String dockerComposeCommand = "docker-compose " + "  --compatibility " + "-f " + path.concat(composeName) + " up -d ";
     UploadUtils.getSftpUtil(dockerHost, dockerPort, userName, key).executeCommand(dockerComposeCommand);
  }


  // 恢复容器内的数据
  private void restoreContainerData(String containerName, String backupPath, String newContainerId) {
    try {
      logger.info("开始恢复容器数据.");
      dockerClient.copyArchiveToContainerCmd(containerName)
        .withHostResource(backupPath)
        .withRemotePath("/")
        .exec();
      logger.info("开始解压容器数据.");
      // 在容器内执行解压 压缩 文件的命令
      String unTarCommand = "tar -xvf /backup_data.tar -C /"; // 替换路径和文件名
      logger.info("容器名称" + containerName);
      ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(newContainerId)
        .withAttachStdout(true)
        .withAttachStdin(true)
        .withTty(true)
        .withUser("root")
        .withCmd("/bin/sh", "-c", unTarCommand)
        .exec();
      logger.info("已创建解压命令.");

      // 启动执行命令并等待其完成
      dockerClient.execStartCmd(execCreateCmdResponse.getId())
        .exec(new ExecStartResultCallback() {
          @Override
          public void onNext(Frame item) {
            logger.info(item.toString());
          }
        }).awaitCompletion();
      logger.info("容器数据恢复成功.");
    } catch (Exception e) {
      logger.error("容器数据恢复失败: " + e.getMessage());
      throw new APIException("容器恢复失败: " + e.getMessage());
    }
  }


  // 获取容器状态
  public String getContainerStatus(String containerId) {
    try {
      InspectContainerResponse response = dockerClient.inspectContainerCmd(containerId).exec();
      // 获取容器状态
      String containerStatus = response.getState().getStatus();
      return containerStatus;
    } catch (NotFoundException e) {
      logger.error(e);
      return null;
    }
  }

  public ContainerModel buildAndRunCompose(String containerName, String composeYamlPath, String studentCode, String userName, String key, String path, String dockerHost, int port) throws Exception {
    // 构建 Docker Compose 配置
    String composeYamlContent;
    try {
      composeYamlContent = IOUtils.toString(new File(composeYamlPath).toURI(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new DockerClientException("无法读取 Docker Compose 文件", e);
    }

    // 使用 docker-compose 构建和运行服务
    String composeFilePath = createTemporaryComposeFile(composeYamlContent, containerName);
    String fileName = UploadUtils.getSftpUtil(dockerHost, port, userName, key).upload(path, composeFilePath);
    if (fileName == null) {
      throw new APIException("composeFile上传失败");
    }
    String dockerComposeCommand = "docker-compose " + "  --compatibility " + "-f " + path.concat(fileName) + " up -d";
    String result = UploadUtils.getSftpUtil(dockerHost, port, userName, key).executeCommand(dockerComposeCommand);
    logger.info(result);
    String dockerIdCommand = " docker ps -aq --filter " + "name=" + containerName;
    String containerId = UploadUtils.getSftpUtil(dockerHost, port, userName, key).executeCommand(dockerIdCommand);
    logger.info("containerId=".concat(containerId));
    String dockerPortCommand = " docker port " + containerName;
    String containerPort = UploadUtils.getSftpUtil(dockerHost, port, userName, key).executeCommand(dockerPortCommand);
    if (containerPort != null) {
      containerPort = containerPort.substring(containerPort.lastIndexOf(":") + 1);
    }
    logger.info("containerPort=".concat(containerPort));
    ContainerModel model = new ContainerModel();
    model.setContainerId(containerId);
    model.setContainerName(containerName);
    model.setContainerPort(containerPort);
    return model;
  }

  private String createTemporaryComposeFile(String composeYamlContent, String containerName) {
    // 创建临时的 Docker Compose 配置文件
    String composeFileName = containerName.replace("_container", "-docker-compose.yml");
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


  public void uploadImage(String localImageName, MultipartFile file) throws IOException {

    System.out.println("Docker 镜像上传完成");
  }
}
