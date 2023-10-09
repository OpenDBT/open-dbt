package com.highgo.opendbt.experiment.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 实验
 *
 * @TableName t_experiment
 */
@TableName(value = "t_experiment")
@Data
public class TExperiment implements Serializable {
  /**
   *
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 实验名称
   */
  private String experimentName;

  /**
   * 镜像端口号
   */
  @TableField(exist = false)
  private String imagePort;

  /**
   * 镜像id
   */
  private Integer imageId;

  /**
   * 镜像名称
   */
  @TableField(exist = false)
  private String imageName;

  /**
   * 容器端口号
   */
  @TableField(exist = false)
  private String containerPort;

  /**
   * 发布状态true:发布，false:未发布
   */
  private Boolean releaseStatus;

  /**
   * 课程id
   */
  private Integer courseId;

  /**
   * 创建人员
   */
  private Integer createUser;

  /**
   * 创建时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  /**
   * 修改时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  /**
   * 删除人员
   */
  private Integer deleteUser;

  private static final long serialVersionUID = 1L;


}
