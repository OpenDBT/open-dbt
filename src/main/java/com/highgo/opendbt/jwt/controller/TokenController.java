package com.highgo.opendbt.jwt.controller;

import com.auth0.jwt.JWT;
import com.highgo.opendbt.common.bean.ResultTO;
import com.highgo.opendbt.common.utils.JwtUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.jwt.service.TokenBlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description: token失效等操作
 * @Title: TokenController
 * @Package com.highgo.opendbt.jwt.controller
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/7/10 11:25
 */
@RestController
public class TokenController {

  private TokenBlacklistService tokenBlacklistService;

  public TokenController(TokenBlacklistService tokenBlacklistService) {
    this.tokenBlacklistService = tokenBlacklistService;
  }

  @PostMapping("/logout")
  public ResultTO invalidateToken(HttpServletRequest request) {
    String token = JwtUtil.getToken(request);
    // 将令牌添加到黑名单
    tokenBlacklistService.blacklistToken(token);
   return ResultTO.FAILURE(Message.get("InvalidToken"), 401);
  }
}
