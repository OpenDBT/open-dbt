package com.highgo.opendbt.experiment.terminal.handle;

import com.highgo.opendbt.experiment.manageer.DockerComposeBuilder;
import com.highgo.opendbt.experiment.terminal.common.Constants;
import com.highgo.opendbt.experiment.terminal.service.WebDockerService;
import com.highgo.opendbt.experiment.terminal.service.WebSocketService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SocketHandler extends TextWebSocketHandler {

  private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

  @Autowired
  private WebSocketService webSocketService;
  @Autowired
  private WebDockerService webDockerService;
  @Value("${docker.connect.host}")
  private String dockerHost;
  @Value("${docker.connect.port}")
  private int dockerPort;

  public SocketHandler() {
    logger.info("SocketHandler初始化了");
  }

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
      logger.info("Received TextMessage: {}"+ ((TextMessage) message).getPayload());
      logger.info("发送命令:{}"+ message.getPayload());
      handleTextMessage(session, (TextMessage) message);

    } else if (message instanceof BinaryMessage) {
      handleBinaryMessage(session, (BinaryMessage) message);
    } else if (message instanceof PongMessage) {
      // 处理 Pong 消息
      logger.info("Received Pong message");
    } else {
      logger.info("Unexpected WebSocket message type: " + message);
    }
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

    System.out.println("TextMessage=" + message.getPayload());
    logger.info("Received TextMessage: {}"+ message.getPayload());
    logger.info("发送命令:{}"+message.getPayload());
    if ((Boolean) session.getAttributes().get(Constants.CONTAINER_EXEC)) {
      webDockerService.recvHandle(message.getPayload(), session);
    } else {
      webSocketService.recvHandle(message.getPayload(), session);
    }
  }

  @SneakyThrows
  @Override
  public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
    logger.info("客户端发送消息到后端size: {}"+ new String(message.getPayload().array(), StandardCharsets.UTF_8));
    // 处理二进制消息，即文件数据
    byte[] fileData = message.getPayload().array();
    if ((Boolean) session.getAttributes().get(Constants.CONTAINER_EXEC)) {
      // 处理文件数据，保存到磁盘等操作
      webDockerService.recvHandle(fileData, session);
    } else {
      webSocketService.recvHandle(fileData, session);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    logger.error("数据传输错误");
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
    logger.info("断开连接");
    String containerId = session.getAttributes().get(Constants.CONTAINER_NAME).toString();
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
