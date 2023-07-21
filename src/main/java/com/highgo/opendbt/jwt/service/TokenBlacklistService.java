package com.highgo.opendbt.jwt.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 令牌黑名单服务（TokenBlacklistService）来管理失效的令牌
 * @Title: TokenBlacklistService
 * @Package com.highgo.opendbt.jwt.service
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/7/10 11:12
 */
@Service
public class TokenBlacklistService {

  private Set<String> blacklistedTokens = new HashSet<>();

  public boolean isTokenBlacklisted(String token) {
    return blacklistedTokens.contains(token);
  }

  public void blacklistToken(String token) {
    blacklistedTokens.add(token);
  }
}

