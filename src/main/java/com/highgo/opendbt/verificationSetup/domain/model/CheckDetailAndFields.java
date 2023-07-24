package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
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
 * @Description: 保存表和字段信息
 * @Title: CheckDetailAndFields
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/28 17:00
 */
@ApiModel(description = "保表和字段信息保存中间类")
@Data
@ToString
@Accessors(chain = true)
public class CheckDetailAndFields {
  @Valid
  @NotNull
  private TCheckDetail checkDetail;
  @Valid
  @NotEmpty
  private List<TCheckField> fields;
}
