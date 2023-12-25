package com.highgo.opendbt;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @Description:
 * @Title: WebAppRootContext
 * @Package com.highgo.opendbt
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/30 15:25
 */



@Configuration
//@ComponentScan
@EnableAutoConfiguration
public class WebAppRootContext implements ServletContextInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    servletContext.addListener(WebAppRootListener.class);
    //这里设置了30兆的缓冲区
    //Tomcat每次请求过来时在创建session时都会把这个webSocketContainer作为参数传进去所以对所有的session都生效了
    servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","30000000");
    servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","30000000");
  }
}
