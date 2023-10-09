package com.highgo.opendbt.experiment.manageer;

/**
 * @Description:
 * @Title: JSchSessionConfig
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/4 16:48
 */
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: ssh远程连接配置类
 * @author Z
 * @date 2023/11/8 9:57
 * @version 1.0
 */
@Configuration
public class JSchSessionConfig {
  //指定的服务器地址
  @Value("${docker.connect.host}")
  private String ip;
  //用户名
  @Value("${docker.connect.username}")
  private String user;
  //密码
  @Value("${docker.connect.key}")
  private String password;
  //服务器端口 默认22
  @Value("${docker.connect.port}")
  private String port;

  @Bean
  public Session registSession() throws JSchException {
    Session session;

      JSch jSch = new JSch();
      //设置session相关配置信息
      session = jSch.getSession(user, ip, Integer.valueOf(port));
      session.setPassword(password);
      //设置第一次登陆的时候提示，可选值：(ask | yes | no)
      session.setConfig("StrictHostKeyChecking", "no");
    return session;
  }
}

