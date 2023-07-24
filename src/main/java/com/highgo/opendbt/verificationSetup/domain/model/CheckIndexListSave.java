package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 校验点索引信息保存
 * @Title: CheckIndexListSave
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/28 16:34
 */
@ApiModel(description = "索引信息保存中间类")
@Data
@ToString
@Accessors(chain = true)
public class CheckIndexListSave {
  @Valid
  @NotEmpty
  private List<TCheckIndex> indexList;
  @NotNull
  private Integer sceneId;
}
