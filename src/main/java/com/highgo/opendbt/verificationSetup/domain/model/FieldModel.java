package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.highgo.opendbt.common.Interval.IntervalSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Description: 查询场景表字段信息中间类
 * @Title: TableModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/5/10 17:21
 */
@ApiModel(description = "查询场景表字段信息中间类")
@Data
@ToString
@Accessors(chain = true)
public class FieldModel {
  //@NotNull(message = "场景详情id不能为空")
  @JsonSerialize(using = IntervalSerializer.class)
  private Long sceneDetailId;
  @JsonSerialize(using = IntervalSerializer.class)
  private Long exerciseId;
  //新建表名
  private String tableName;
}
