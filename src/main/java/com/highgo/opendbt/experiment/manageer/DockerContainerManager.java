package com.highgo.opendbt.experiment.manageer;

/**
 * @Description:
 * @Title: DockerContainerManager
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/22 16:03
 */

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.highgo.opendbt.ApplicationContextRegister;
import com.highgo.opendbt.common.exception.APIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.util.Collections;


public class DockerContainerManager {
  private static final Log logger = LogFactory.getLog(DockerContainerManager.class);
  private DockerClient dockerClient;
  private static DockerContainerManager instance;
  private static String currentDirectory = "/"; // 初始目录
  private static String dockerHost;
  private static String dockerPort;

  static {
    ApplicationContext applicationContext = ApplicationContextRegister.getApplicationContext();
    dockerHost = applicationContext.getEnvironment().getProperty("docker.connect.host");
    dockerPort = applicationContext.getEnvironment().getProperty("docker.connect.port");
  }

  //构造方法中初始化docker
  private DockerContainerManager() {
    // 创建 Docker 客户端配置
    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost("tcp://" + dockerHost + ":" + dockerPort)
      .build();
    // 创建自定义的 Docker HTTP 客户端
    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
      .dockerHost(config.getDockerHost())
      .sslConfig(config.getSSLConfig())
      .build();
    dockerClient = DockerClientBuilder.getInstance(config)
      .withDockerHttpClient(httpClient)
      .build();

  }

  // 获取 DockerContainerManager 的唯一实例
  public static synchronized DockerContainerManager getInstance() {
    if (instance == null) {
      instance = new DockerContainerManager();
    }
    return instance;
  }


  public static String getPath(String name) {
    // 获取资源文件的输入流
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(name); // 替换为你的资源文件路径

    String path = null;
    if (inputStream != null) {
      try {
        // 创建临时文件
        File tempFile = File.createTempFile(name.substring(0, name.lastIndexOf(".")), name.substring(name.lastIndexOf(".")));

        // 将资源文件内容复制到临时文件中
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
        }

        // 现在，tempFile 就是资源文件的副本，并可以作为 File 使用
        logger.info("临时文件路径：" + tempFile.getAbsolutePath());
        path = tempFile.getAbsolutePath();
        // 在这里可以使用 tempFile 处理资源文件
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      logger.info("资源文件未找到");
    }
    return path;
  }

  public static String getPath(String studentCode, String imageName, String containerPort) {
    // 获取资源文件的输入流
    String dockerComposeConfig = "version: '2'\n" +
      "services:\n" +
      "  " + studentCode + "_service:\n" +
      "    image: " + imageName + "\n" +
      "    container_name: " + studentCode.concat("_").concat(imageName.replace(":", "_")) + "_container\n";
    if (containerPort != null) {
      dockerComposeConfig += "    ports:\n" +
        "      - '" + containerPort + "'\n";

    }

    // 将Docker Compose配置字符串转换为InputStream
    InputStream inputStream = new ByteArrayInputStream(dockerComposeConfig.getBytes());

    String path = null;
    if (inputStream != null) {
      try {
        // 创建临时文件
        File tempFile = File.createTempFile("docker-compose", ".yml");

        // 将资源文件内容复制到临时文件中
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
        }

        // 现在，tempFile 就是资源文件的副本，并可以作为 File 使用
        logger.info("临时文件路径：" + tempFile.getAbsolutePath());
        path = tempFile.getAbsolutePath();
        // 在这里可以使用 tempFile 处理资源文件
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      logger.info("资源文件未找到");
    }
    return path;
  }

  //通过dockerfile构建镜像
  public void buildImage(String dockerfilePath, String imageName) {
    if (dockerfilePath == null) {
      throw new APIException("镜像构建失败，未找到docker-file资源！");
    }
    if (imageName == null) {
      throw new APIException("镜像构建失败，镜像名称不能为空！");
    }
    // 使用 docker-java 构建 Docker 镜像
    BuildImageResultCallback callback = new BuildImageResultCallback() {
      @Override
      public void onNext(BuildResponseItem item) {
        // 处理构建过程中的输出
        if (item.getStream() != null) {
          logger.info(item.getStream());
        }

      }

      @Override
      public void onComplete() {
        // 构建完成时的日志
        logger.info("镜像构建完成：" + imageName);
      }

      @Override
      public void onError(Throwable throwable) {
        // 构建失败时的日志
        System.err.println("镜像构建失败：" + imageName);
        throwable.printStackTrace();
      }
    };

    logger.info("开始构建镜像：" + imageName);
    String imageId = dockerClient.buildImageCmd()
      .withDockerfile(new File(dockerfilePath)) // 使用 Dockerfile 内容
      .withTags(Collections.singleton(imageName))
      .exec(callback)
      .awaitImageId();
    logger.info("imageId=".concat(imageId));
  }


}

