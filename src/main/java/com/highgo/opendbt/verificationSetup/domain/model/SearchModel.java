package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.highgo.opendbt.common.Interval.IntervalSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description: 查询中间类
 * @Title: ConstraintModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/12 17:27
 */
@ApiModel(description = "查询中间类")
@Data
@ToString
@Accessors(chain = true)
public class SearchModel {
  @JsonSerialize(using = IntervalSerializer.class)
  private Long sceneDetailId;
  @JsonSerialize(using = IntervalSerializer.class)
  private Long exerciseId;
  //新建表名
  private String tableName;
}
