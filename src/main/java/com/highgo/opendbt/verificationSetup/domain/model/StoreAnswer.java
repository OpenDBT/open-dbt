package com.highgo.opendbt.verificationSetup.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @Description: 答案存储中间类
 * @Title: StoreAnswer
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/30 16:27
 */
@ApiModel(description = "答案存储中间类")
@Data
@ToString
@Accessors(chain = true)
public class StoreAnswer {
  //存储有场景表对应的答案,key: t_scene_detail 的id，value: 答案
  private Map<String,String> commonAnswers;
  //存储新增表的答案,key: table_name 的id，value: 答案
  private Map<String,String> addAnswers;
}
