package com.highgo.opendbt.experiment.terminal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highgo.opendbt.experiment.terminal.common.Constants;
import com.highgo.opendbt.experiment.terminal.entity.ConnectInfo;
import com.highgo.opendbt.experiment.terminal.entity.WebSSHData;
import com.highgo.opendbt.experiment.terminal.service.WebSocketService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class WebSocketServiceImpl implements WebSocketService {

  private static Map<String, Object> sshMap = new ConcurrentHashMap<>();

  private final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);

  private ExecutorService executorService = Executors.newCachedThreadPool();

  @Override
  public void initConnection(WebSocketSession session) {

    JSch jSch = new JSch();
    ConnectInfo connectInfo = new ConnectInfo();
    connectInfo.setJsch(jSch);
    connectInfo.setWebSocketSession(session);
    String uuid = String.valueOf(session.getAttributes().get(Constants.USER_KEY));
    //将这个ssh连接信息放入map中
    sshMap.put(uuid, connectInfo);
  }

  @Override
  public void recvHandle(String buffer, WebSocketSession session) {
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
    //获取刚才设置的随机的uuid
    String userId = String.valueOf(session.getAttributes().get(Constants.USER_KEY));
    if (Constants.OPERATE_CONNECT.equals(webSSHData.getOperate())) {
      //如果是连接请求
      //找到刚才存储的ssh连接对象
      ConnectInfo connectInfo = (ConnectInfo) sshMap.get(userId);
      //启动线程异步处理
      executorService.execute(new Runnable() {
        @Override
        public void run() {
          try {
            //连接到终端
            connectToSSH(connectInfo, webSSHData, session);
          } catch (JSchException | IOException e) {
            logger.error("ssh连接异常");
            logger.error("异常信息:{}", e.getMessage());
            close(session);
          }
        }
      });
    } else if (Constants.OPERATE_COMMAND.equals(webSSHData.getOperate())) {
      //如果是发送命令的请求
      String command = webSSHData.getCommand();
      ConnectInfo connectInfo = (ConnectInfo) sshMap.get(userId);
      if (connectInfo != null) {
        try {
          //发送命令到终端
          transToSSH(connectInfo.getChannel(), command);
        } catch (IOException e) {
          logger.error("ssh连接异常");
          logger.error("异常信息:{}", e.getMessage());
          close(session);
        }
      }
    } else {
      logger.error("不支持的操作");
      close(session);
    }
  }

  @Override
  public void recvHandle(byte[] fileData, WebSocketSession session)  {
    //获取刚才设置的随机的uuid
    String userId = String.valueOf(session.getAttributes().get(Constants.USER_KEY));
      //如果是发送命令的请求
      ConnectInfo connectInfo = (ConnectInfo) sshMap.get(userId);
      if (connectInfo != null) {
        try {
          //发送命令到终端
          transToSSH(connectInfo.getChannel(), fileData);
        } catch (IOException e) {
          logger.error("ssh连接异常");
          logger.error("异常信息:{}", e.getMessage());
          close(session);
        }
      }

  }

  @Override
  public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {

    String receivedData = new String(buffer, "UTF-8"); // 使用UTF-8解码
    System.out.println(receivedData);
    session.sendMessage(new TextMessage(buffer));
  }

  @Override
  public void close(WebSocketSession session) {
    //获取随机生成的uuid
    String userId = String.valueOf(session.getAttributes().get(Constants.USER_KEY));
    ConnectInfo connectInfo = (ConnectInfo) sshMap.get(userId);
    if (connectInfo != null) {
      //断开连接
      if (connectInfo.getChannel() != null) {
        connectInfo.getChannel().disconnect();
      }
      //map中移除该ssh连接信息
      sshMap.remove(userId);
    }
  }

  /**
   * 使用jsch连接终端
   */
  private void connectToSSH(ConnectInfo connectInfo, WebSSHData webSSHData, WebSocketSession webSocketSession) throws JSchException, IOException {
    Session session;
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    //获取jsch的会话
    session = connectInfo.getJsch().getSession(webSSHData.getUsername(), webSSHData.getHost(), webSSHData.getPort());
    session.setConfig(config);
    //设置密码
    session.setPassword(webSSHData.getPassword());
    //连接  超时时间30s
    session.connect(30000);

    //开启shell通道
    Channel channel = session.openChannel("shell");

    //通道连接 超时时间3s
    channel.connect(3000);

    //设置channel
    connectInfo.setChannel(channel);

    //转发消息
    transToSSH(channel, "");

    //读取终端返回的信息流
    InputStream inputStream = channel.getInputStream();
    try {
      //循环读取
      byte[] buffer = new byte[1024];
      int i = 0;
      //如果没有数据来，线程会一直阻塞在这个地方等待数据。
      while ((i = inputStream.read(buffer)) != -1) {
        sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
      }

    } finally {
      //断开连接后关闭会话
      session.disconnect();
      channel.disconnect();
      if (inputStream != null) {
        inputStream.close();
      }
    }

  }

  /**
   * 将消息转发到终端
   */
  private void transToSSH(Channel channel, String command) throws IOException {
    if (channel != null) {
      OutputStream outputStream = channel.getOutputStream();
      outputStream.write(command.getBytes());
      outputStream.flush();
    }
  }
  /**
   * 将消息转发到终端
   */
  private void transToSSH(Channel channel, byte[] fileData) throws IOException {
    if (channel != null) {
      OutputStream outputStream = channel.getOutputStream();
      outputStream.write(fileData);
      outputStream.flush();
    }
  }
}
