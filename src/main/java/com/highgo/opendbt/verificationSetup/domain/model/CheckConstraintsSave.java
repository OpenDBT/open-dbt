package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description: 校验点约束信息保存
 * @Title: CheckConstraintsSave
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/28 16:34
 */
@ApiModel(description = "约束信息保存中间类")
@Data
@ToString
@Accessors(chain = true)
public class CheckConstraintsSave {
  @Valid
  @NotEmpty
  private List<TCheckConstraint> checkConstraints;
}
