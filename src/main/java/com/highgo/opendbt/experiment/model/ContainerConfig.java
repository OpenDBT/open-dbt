package com.highgo.opendbt.experiment.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Title: CommandInput
 * @Package com.highgo.opendbt.experiment.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/25 13:23
 */
@Data
@ToString
public class ContainerConfig  {
  @NotNull(message = "实验ID不能为空")
  private Long experimentId;
  //镜像名称
  @NotNull(message = "镜像名称不能为空")
  private String imageName;
  //学号
  @NotNull(message = "学号不能为空")
  private String studentCode;
  //课程id
  @NotNull(message = "课程ID不能为空")
  private int courseId;
  //实验id
  @NotBlank(message = "镜像id不能为空")
  private String imageId;
  //容器暴露端口
  @NotNull(message = "镜像端口不能为空")
  private String imagePort;
  //cpu内核数
  private String cpu;
  //内存大小
  private String memory;
}
