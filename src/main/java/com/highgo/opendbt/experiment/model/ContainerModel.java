package com.highgo.opendbt.experiment.model;

import lombok.Data;
import lombok.ToString;

/**
 * @Description: 容器id和容器端口号
 * @Title: ContainerModel
 * @Package com.highgo.opendbt.experiment.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/10/8 17:53
 */
@Data
@ToString
public class ContainerModel {
  private String containerId;
  private String containerPort;
}
