package com.highgo.opendbt.experiment.manageer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @Description:
 * @Title: DockerConfiger
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/23 9:36
 */



@Component
@Data
@ConfigurationProperties(prefix = "docker")
public class DockerConfig {

  private String host;

  private String port;

}
