package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Description: 查询场景表信息中间类
 * @Title: TableModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/5/10 17:21
 */
@ApiModel(description = "查询场景表信息中间类")
@Data
@ToString
@Accessors(chain = true)
public class TableModel {
  @NotNull(message = "场景id不能为空")
  private Integer sceneId;
  @JsonSerialize(using = ToStringSerializer.class)
  private Long exerciseId;
}
