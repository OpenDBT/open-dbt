package com.highgo.opendbt.experiment.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 实验详情
 * @Title: ExperimentInfo
 * @Package com.highgo.opendbt.experiment.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/27 10:28
 */
@Data
public class ExperimentInfo {
  /**
   *
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 实验名称
   */
  @NotBlank(message = "实验名称不能为空")
  private String experimentName;

  /**
   * 镜像端口号
   */
  private String imagePort;

  /**
   * 镜像id
   */
  @NotNull(message = "镜像不能为空")
  private Integer imageId;

  /**
   * 镜像名称
   */
  private String imageName;

  /**
   * 容器端口号
   */
  private String containerPort;

  /**
   * 容器id
   */
  private String containerId;

  /**
   * 发布状态true:发布，false:未发布
   */
  private Boolean releaseStatus;

  /**
   * 课程id
   */
  @NotNull(message = "课程id不能为空")
  private Integer courseId;

  /**
   * 创建人员
   */
  private Integer createUser;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date updateTime;

  /**
   * 修改人员
   */
  private Integer updateUser;

  /**
   * 删除标志0：未删除1：已删除
   */
  private Integer deleteFlag;

  /**
   * 删除时间
   */
  private Date deleteTime;

  /**
   * 删除人员
   */
  private Integer deleteUser;
  /**
   * 文档内容
   */
  private String experimentContent;
}
