package com.highgo.opendbt.experiment.terminal.handle;

import com.highgo.opendbt.experiment.manageer.DockerComposeBuilder;
import com.highgo.opendbt.experiment.terminal.common.Constants;
import com.highgo.opendbt.experiment.terminal.service.WebDockerService;
import com.highgo.opendbt.experiment.terminal.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.io.IOException;

@Component
public class SocketHandler extends DefaultHandshakeHandler implements WebSocketHandler {

  private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

  @Autowired
  private WebSocketService webSocketService;
  @Autowired
  private WebDockerService webDockerService;
  @Value("${docker.connect.host}")
  private String dockerHost;
  @Value("${docker.connect.port}")
  private int dockerPort;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    logger.info("初始化连接");
    if ((Boolean) session.getAttributes().get(Constants.CONTAINER_EXEC)) {
      webDockerService.createExec(session);
    } else {
      webSocketService.initConnection(session);
    }
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {

    if (message instanceof TextMessage) {
      logger.info("发送命令:{}", message.getPayload());
      if ((Boolean) session.getAttributes().get(Constants.CONTAINER_EXEC)) {
        webDockerService.recvHandle((String) message.getPayload(), session);
      } else {
        webSocketService.recvHandle((String) message.getPayload(), session);
      }

    } else if (message instanceof BinaryMessage) {
      // 处理二进制消息，即文件数据
      BinaryMessage binaryMessage = (BinaryMessage) message;
      byte[] fileData = binaryMessage.getPayload().array();
      if ((Boolean) session.getAttributes().get(Constants.CONTAINER_EXEC)) {
        // 处理文件数据，保存到磁盘等操作
        webDockerService.recvHandle(fileData, session);
      } else {
        webSocketService.recvHandle(fileData, session);
      }
    } else if (message instanceof PongMessage) {

    } else {
      System.out.println("Unexpected WebSocket message type: " + message);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    logger.error("数据传输错误");
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws IOException {
    logger.info("断开连接");
    String containerId = session.getAttributes().get(Constants.CONTAINER_ID).toString();
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    composeBuilder.stopContainer(containerId);
    //调用service关闭连接
    webSocketService.close(session);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
