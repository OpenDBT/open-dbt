package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 自动生成描述中间类
 * @Title: TestRunModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/30 18:30
 */
@ApiModel(description = "自动生成描述")
@Data
@ToString
@Accessors(chain = true)
public class GeneratorDescription {
  //场景id
  @NotNull(message = "场景id不能为空")
  private Integer sceneId=-1;
  //习题id
  @NotNull(message = "习题id不能为空")
  @JsonSerialize(using = ToStringSerializer.class)
  private Long exerciseId;
}
