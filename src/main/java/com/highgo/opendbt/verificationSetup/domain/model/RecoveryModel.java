package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Description: 一键恢复中间类
 * @Title: RecoveryModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/6 10:04
 */
@ApiModel(description = "一键恢复中间类")
@Data
@ToString
@Accessors(chain = true)
public class RecoveryModel {
  //场景id
  @NotNull(message = "场景详情id不能为空")
  private Integer sceneDetailId;
  //习题id
  @NotNull(message = "习题id不能为空")
  private Integer exerciseId;
  //  TABLE
  //  FIELD
  //  INDEX
  //  CONSTRAINT
  //  FOREIGN_KEY
  //  SEQUENCE
  //  ALL
  @NotNull(message = "类型不能为空")
  private String recoverType;

}
