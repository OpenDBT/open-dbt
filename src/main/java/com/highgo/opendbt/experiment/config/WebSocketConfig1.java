package com.highgo.opendbt.experiment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @Description: websocket配置类
 * @Title: WebSocketConfig
 * @Package com.highgo.opendbt.experiment.config
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/18 10:14
 */

/*@Configuration
@EnableWebSocketMessageBroker*/
public class WebSocketConfig1 implements WebSocketMessageBrokerConfigurer {



  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 配置客户端尝试连接地址
    registry.
      addEndpoint("/ws").     // 设置连接节点，前端请求的建立连接的地址就是 http://ip:端口/ws
      //addInterceptors(getWebSocketInterceptor()).     // 设置握手拦截器
        setAllowedOriginPatterns ("*").     // 配置跨域
      withSockJS();       // 开启sockJS支持，这里可以对不支持stomp的浏览器进行兼容。
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 消息代理,这里配置自带的消息代理，也可以配置其它的消息代理
    // 一定要注意这里的参数，可以理解为开启通道,后面如果使用了"/XXX"来作为前缀，这里就要配置,同时这里的"/topic"是默认群发消息的前缀,前端在订阅群发地址的时候需要加上"/topic"
    registry.enableSimpleBroker("/user","/topic");
    // 客户端向服务端发送消息需有的前缀,需要什么样的前缀在这里配置,但是不建议使用，这里跟下面首次订阅返回会有点冲突，如果不需要首次订阅返回消息，也可以加上消息前缀
     registry.setApplicationDestinationPrefixes("/");
    // 配置单发消息的前缀 /user，前端订阅一对一通信的地址需要加上"/user"前缀
    registry.setUserDestinationPrefix("/user");
  }
}

