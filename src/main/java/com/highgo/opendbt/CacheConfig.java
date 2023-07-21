package com.highgo.opendbt;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;
/**
 * @Description: 缓存配置类
 * @Title: CacheConfig
 * @Package com.highgo.opendbt
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/7/10 13:59
 */




@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public Caffeine caffeineConfig() {
    return Caffeine.newBuilder()
      .initialCapacity(100) // 设置初始缓存容量
      .maximumSize(1000) // 设置最大缓存条数
      .expireAfterWrite(12, TimeUnit.DAYS) // 设置写入后过期时间
      .recordStats(); // 开启统计功能
  }

  @Bean
  public CacheManager cacheManager(Caffeine caffeine) {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(caffeine);
    return cacheManager;
  }
}


