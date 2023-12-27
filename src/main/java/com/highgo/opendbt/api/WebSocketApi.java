package com.highgo.opendbt.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 用于websocket的一些服务
 * @Title: WebSocketApi
 * @Package com.highgo.opendbt.api
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/12/19 9:57
 */
@Api(tags = "websocket相关服务")
@RestController
@RequestMapping("/socket")
@CrossOrigin
public class WebSocketApi {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${wshost}")
  private String wshost;
  /**
   * @description: 因为容器化部署，避免前端重复打包，用于返回ws请求地址
   * @author:
   * @date: 2023/12/19 9:59
   * @param: []
   * @return: java.lang.String
   **/
  @ApiOperation(value = "查询后台访问地址")
  @GetMapping("/getWebSocketUrl")
  public String sendWebSocketUrl(HttpServletRequest request){
    String backendUrl =wshost;
    if(StringUtils.isBlank(wshost)){
      // 获取当前请求的协议、域名、端口等信息
      String serverName = request.getServerName(); // 获取域名
      int serverPort = request.getServerPort(); // 获取端口号
      // 构建后端访问地址
       backendUrl = "ws" + "://" + serverName + ":" + serverPort;
    }
    logger.info("ws访问地址"+backendUrl);
    return backendUrl;
  }
}
