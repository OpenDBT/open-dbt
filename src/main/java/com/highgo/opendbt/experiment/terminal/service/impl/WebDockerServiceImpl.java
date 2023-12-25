package com.highgo.opendbt.experiment.terminal.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highgo.opendbt.experiment.terminal.common.Constants;
import com.highgo.opendbt.experiment.terminal.entity.WebSSHData;
import com.highgo.opendbt.experiment.terminal.service.WebDockerService;
import com.highgo.opendbt.experiment.terminal.utils.DockerUtil;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ExecCreation;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author highgo
 * @date 2021/12/2 16:28
 */
@Service
public class WebDockerServiceImpl implements WebDockerService {

  private final Logger logger = LoggerFactory.getLogger(WebDockerServiceImpl.class);
  private ExecutorService executorService = Executors.newCachedThreadPool();
  //private Map<String, Object> map = new HashMap<>(2);
  private final static String SOCKET = "socket";
  public static final String EXEC_ID = "exec_id";

  @Value("${docker.connect.host}")
  private String ip;
  @Value("${docker.connect.port}")
  private Integer port;

  @Override
  public void createExec(WebSocketSession session) throws Exception {
    String containerName = session.getAttributes().get(Constants.CONTAINER_NAME).toString();
    logger.info("containerName=" + containerName);
    String execId = DockerUtil.query(ip, docker -> {
      ExecCreation execCreation = docker.execCreate(containerName, new String[]{"/bin/bash"},
        DockerClient.ExecCreateParam.attachStdin(), DockerClient.ExecCreateParam.attachStdout(), DockerClient.ExecCreateParam.attachStderr(),
        DockerClient.ExecCreateParam.tty(true),DockerClient.ExecCreateParam.user("root"));
      return execCreation.id();
    });
    session.getAttributes().put(EXEC_ID, execId);
  }

  @Override
  public void recvHandle(String buffer, WebSocketSession session) throws IOException {
    String execId = session.getAttributes().get(EXEC_ID).toString();
    ObjectMapper objectMapper = new ObjectMapper();
    WebSSHData webSSHData;
    try {
      //转换前端发送的JSON
      webSSHData = objectMapper.readValue(buffer, WebSSHData.class);
    } catch (IOException e) {
      logger.error("Json转换异常");
      logger.error("异常信息:{}", e.getMessage());
      return;
    }

    if (Constants.OPERATE_CONNECT.equals(webSSHData.getOperate())) {
      //如果是连接请求
      //启动线程异步处理
      executorService.execute(new Runnable() {
        @SneakyThrows
        @Override
        public void run() {
          try {
            //连接到终端
            connectExec(session, execId);
          } catch (IOException e) {
            logger.error("container连接异常");
            logger.error("异常信息:{}", e.getMessage());
            session.close();
          }
        }
      });
    } else if (Constants.OPERATE_COMMAND.equals(webSSHData.getOperate())) {
      //如果是发送命令的请求
      Socket socket = (Socket) session.getAttributes().get(SOCKET);
      String command = webSSHData.getCommand();
      try {
        //发送命令到终端
        transToContainer(socket, command);
      } catch (IOException e) {
        logger.error("container连接异常");
        logger.error("异常信息:{}", e.getMessage());
        socket.close();
        session.close();
      }
    } else if (Constants.OPERATE_UPLOAD.equals(webSSHData.getOperate())) {
      // 如果是上传文件的请求
      String fileData = webSSHData.getFileData(); // 假设前端发送的文件数据以base64编码的字符串形式在getFileData()方法中
      Socket socket = (Socket) session.getAttributes().get(SOCKET);
      try {
        //发送命令到终端
        transToContainer(socket, fileData);
      } catch (IOException e) {
        logger.error("container连接异常");
        logger.error("异常信息:{}", e.getMessage());
        socket.close();
        session.close();
      }
    }else  if (Constants.OPERATE_ZMODEM.equals(webSSHData.getOperate())) {
      // 如果是 ZMODEM 请求
      Socket socket = (Socket) session.getAttributes().get(SOCKET);
      try {
        // 处理 ZMODEM 数据
        handleZMODEMData(socket, webSSHData);
      } catch (IOException e) {
        logger.error("ZMODEM 数据处理异常");
        logger.error("异常信息:{}", e.getMessage());
        socket.close();
        session.close();
      }
    }else {
      logger.error("不支持的操作");
      session.close();
    }
  }
  private void handleZMODEMData(Socket socket, WebSSHData webSSHData) throws IOException {
    if (socket != null) {
      OutputStream outputStream = socket.getOutputStream();
      String zmodemData = webSSHData.getZmodemData();
      byte[] data = Base64.getDecoder().decode(zmodemData);
      outputStream.write(data);
      outputStream.flush();
    }
  }
  @Override
  public void recvHandle(byte[] fileData, WebSocketSession session) throws IOException {
    // 如果是上传文件的请求
    Socket socket = (Socket) session.getAttributes().get(SOCKET);
    try {
      //发送命令到终端
      transToContainer(socket, fileData);
    } catch (IOException e) {
      logger.error("container连接异常");
      logger.error("异常信息:{}", e.getMessage());
      socket.close();
      session.close();
    }

  }


  /**
   * 以 /bin/bash 方式建立连接
   *
   * @param execId 命令id
   */
  private void connectExec(WebSocketSession session, String execId) throws IOException {
    Socket socket = new Socket(ip, port);
    socket.setKeepAlive(true);
    OutputStream out = socket.getOutputStream();
    StringBuffer pw = new StringBuffer();
    pw.append("POST /exec/" + execId + "/start HTTP/1.1\r\n");
    pw.append("Host: " + ip + ":" + port + " \r\n");
    pw.append("Content-Type: application/json\r\n");
    pw.append("Connection: keep-alive\r\n");
    JSONObject obj = new JSONObject();
    obj.put("Detach", false);
    obj.put("Tty", true);
    String json = obj.toJSONString();
    pw.append("Content-Length: " + json.length() + "\r\n");
    pw.append("\r\n");
    pw.append(json);
    out.write(pw.toString().getBytes(StandardCharsets.UTF_8));
    out.flush();
    session.getAttributes().put(SOCKET, socket);
    // 监听终端返回的数据
    InputStream inputStream = socket.getInputStream();
    //循环读取
    byte[] buffer = new byte[1024];
    int i = 0;
    //如果没有数据来，线程会一直阻塞在这个地方等待数据。
    while ((i = inputStream.read(buffer)) != -1) {
      this.sendMessage(session, Arrays.copyOfRange(buffer, 0, i));
    }
  }

  /**
   * 将消息发送给客户端
   *
   * @param session
   * @param buffer
   * @throws IOException
   */
  private void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
    logger.info("返回客户端的信息：".concat(new String(buffer, "UTF-8")));
    if (!isAscii(buffer)) {
      logger.info("返回客户端二进制消息"+new String(buffer, StandardCharsets.UTF_8));
      session.sendMessage(new BinaryMessage(buffer));
    }else{
      logger.info("返回客户端文本消息"+new String(buffer, StandardCharsets.UTF_8));
      session.sendMessage(new TextMessage(buffer));
    }
  }


  public static boolean isAscii(byte[] byteArray) {
    for (byte b : byteArray) {
      if (b < 0 || b > 127) {
        return false; // 如果字节值不在0-127范围内，则不是ASCII
      }
    }
    return true; // 所有字节值都在0-127范围内，是ASCII
  }
  /**
   * 将消息转发到终端
   */
  private void transToContainer(Socket socket, String command) throws IOException {
    if (socket != null) {
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write(command.getBytes());
      outputStream.flush();
    }
  }

  /**
   * 将消息转发到终端
   */
  private void transToContainer(Socket socket, byte[] command) throws IOException {
    if (socket != null) {
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write(command);
      outputStream.flush();
    }
  }
}
