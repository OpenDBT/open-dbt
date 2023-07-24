package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 新增表信息删除参数
 * @Title: NewTableInfoDel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/29 13:26
 */
@ApiModel(description = "新增表信息删除参数中间类")
@Data
@ToString
@Accessors(chain = true)
public class NewTableInfoDel {
  //习题id
  @NotNull(message = "习题id不能为空")
  @JsonSerialize(using = ToStringSerializer.class)
  private Long exerciseId;
  //类型
  @NotBlank(message = "类型不能为空")
  private String types;
  //id
  @NotEmpty(message = "需要删除的相关校验id不能为空")
  private List<Long> ids;
}
