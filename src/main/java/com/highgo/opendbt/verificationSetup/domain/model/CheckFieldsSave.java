package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 校验字段保存中间类
 * @Title: CheckFieldsSave
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/28 16:25
 */
@ApiModel(description = "校验字段保存中间类")
@Data
@ToString
@Accessors(chain = true)
public class CheckFieldsSave {
  @Valid
  @NotEmpty
  private List<TCheckField> fields;
  @NotNull
  private Integer sceneId;
}
