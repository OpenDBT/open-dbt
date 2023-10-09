package com.highgo.opendbt.experiment.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @TableName t_containers
 */
@Data
@Accessors(chain = true)
public class TContainers implements Serializable {
  /**
   *id
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 容器id
   */
  private String containerId;

  /**
   * 容器端口号
   */
  private String containerPort;

  /**
   * 学号
   */
  private String code;

  /**
   * 实验id
   */
  private int imageId;

  private static final long serialVersionUID = 1L;

}
