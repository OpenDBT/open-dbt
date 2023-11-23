package com.highgo.opendbt.experiment.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.highgo.opendbt.common.entity.MyBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @TableName t_containers
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_containers")
public class TContainers extends MyBaseEntity implements Serializable {
  /**
   *
   */
  @TableId
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 容器id
   */
  private String containerId;
  /**
   * 容器名称
   */
  @NotBlank(message = "容器名称不能为空")
  private String containerName;
  /**
   * 容器端口号
   */
  private String containerPort;

  /**
   * 学号
   */
  private String code;

  /**
   * 镜像id
   */
  private String imageId;

  /**
   * 内核数
   */
  private String cpu;

  /**
   * 内存大小
   */
  private String memory;

  /**
   * 课程id
   */
  private Integer courseId;
  /**
   * 实验id
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long experimentId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;


}
