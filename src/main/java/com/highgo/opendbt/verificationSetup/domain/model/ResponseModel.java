package com.highgo.opendbt.verificationSetup.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.highgo.opendbt.common.bean.DataTypeAndImg;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @Description: 测试运行返回中间类
 * @Title: ResponseModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/19 16:31
 */
@Data
@ToString
public class ResponseModel extends SubmitResult {

  //字段
  private List<Map<Object, Object>> result;
  //类型
  private List<DataTypeAndImg> datatype;
  //字段值
  private List<String> column;

  //是否有查询结果集输出
  @JsonProperty("isSelect")
  private boolean isSelect = false;
  //表结构
  private Map<String, List<TSceneField>> viewInfo;

  //函数返回
  private List<ResultSetInfo> functionResult;

  //更新行数
  private Integer updateRow;

  //返回的结果集
  private ResultSetInfo resultSetInfo;


}
