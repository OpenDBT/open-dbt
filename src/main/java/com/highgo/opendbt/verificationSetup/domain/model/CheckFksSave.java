package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 校验点外键信息保存
 * @Title: CheckFksSave
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/28 16:34
 */
@ApiModel(description = "外键信息保存中间类")
@Data
@ToString
@Accessors(chain = true)
public class CheckFksSave {
  @Valid
  @NotEmpty
  private List<TCheckFk> checkFks;
  //场景id
  @NotNull
  private Integer sceneId;
}
