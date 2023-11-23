package com.highgo.opendbt.experiment.model;

import com.highgo.opendbt.experiment.domain.TContainers;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 容器列表显示
 * @Title: ContainersInfo
 * @Package com.highgo.opendbt.experiment.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/10/19 11:44
 */
@Data
@Accessors(chain = true)
public class ContainersInfo extends TContainers {
  //用户名称
  private String userName;
  //镜像名称
  @NotBlank(message = "镜像名称不能为空")
  private String imageName;
  //实验名称
  private String experimentName;
/** 容器状态
  running（运行中）：容器正在运行，即正在执行应用程序。

  exited（已停止）：容器已经停止，即应用程序已经退出。

  paused（暂停）：容器被暂停，即应用程序的运行被暂停，但容器仍处于运行状态。

  restarting（重新启动中）：容器正在重新启动，可能由于更新或配置更改而导致。

  created（已创建）：容器已被创建，但尚未启动。

  dead（已死亡）：容器已经停止并退出，但尚未被删除。*/
  private String status;
}
