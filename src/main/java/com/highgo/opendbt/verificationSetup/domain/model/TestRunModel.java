package com.highgo.opendbt.verificationSetup.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 测试运行中间类
 * @Title: TestRunModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/30 18:30
 */
@ApiModel(description = "测试运行中间类")
@Data
@ToString
@Accessors(chain = true)
public class TestRunModel {
  //场景id
  @NotNull(message = "场景id不能为空")
  private Integer sceneId = -1;
  //习题id
  @NotNull(message = "习题id不能为空")
  private Integer exerciseId;
  //答案
  @NotBlank(message = "习题答案不能为空")
  private String standardAnswer;
  //类型(试题类型 1：单选2：多选3：判断4：填空5：简答6：DML7:DDL8:VIEW_DDL9:FUNCTION)
  @NotNull(message = "习题类型不能为空")
  private Integer exerciseType;
}
