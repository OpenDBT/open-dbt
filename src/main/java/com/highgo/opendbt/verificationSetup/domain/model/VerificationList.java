package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.*;
import lombok.Data;

import java.util.List;

/**
 * @Description: 校验点初始化场景表信息和校验设置信息
 * @Title: VerificationList
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/27 17:17
 */
@Data
public class VerificationList {
  //场景表
  private List<TSceneDetail> sceneDetails;
  //校验表
  private List<TCheckDetail> checkDetails;
  //场景字段相关信息表
  private List<TSceneField> sceneFields;
  //校验字段相关信息表
  private List<TCheckField> checkFields;
  //场景索引信息表
  private List<TSceneIndex> sceneIndexList;
  //校验索引相关信息表
  private List<TCheckIndex> checkIndexList;
  //场景约束信息表
  private List<TSceneConstraint> sceneConstraints;
  //校验约束相关信息表
  private List<TCheckConstraint> checkConstraints;
  //场景外键信息表
  private List<TSceneFk> sceneFks;
  //校验外键相关信息表
  private List<TCheckFk> checkFks;
  //场景序列信息表
  private List<TSceneSeq> sceneSeqs;
  //校验序列相关信息表
  private List<TCheckSeq> checkSeqs;

  //合并场景表和校验表信息
  private List<TSceneDetailModel> sceneDetailModels;
}
