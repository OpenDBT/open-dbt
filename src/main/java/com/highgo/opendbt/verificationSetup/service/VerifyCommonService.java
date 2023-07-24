package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
//校验点模块 公共服务类
public interface VerifyCommonService {
  //一键生成答案
  String generatesAnswer(HttpServletRequest request, int sceneId, Long exerciseId);

  //测试运行
  Object testRun(HttpServletRequest request, TestRunModel model);

  //一键恢复
  boolean recovery(HttpServletRequest request, RecoveryModel model);

  //自动生成描述
  String generateDescriptions(HttpServletRequest request, GeneratorDescription model);

  //教师端测试运行DDL类型题目
  void veryDDLTypeExercise(TestRunModel model, UserInfo userInfo);

  //教师端测试运行视图类型题目
  void testRunVIEWDDLTypeExercise(TestRunModel model, ResponseModel resultMap, UserInfo userInfo);

  //字段信息恢复
  boolean fieldRecovery(HttpServletRequest request, Long id, Long exerciseId);

  //约束信息恢复
  boolean constraintRecovery(HttpServletRequest request, Long id, Long exerciseId);

  //索引信息恢复
  boolean indexRecovery(HttpServletRequest request, Long id, Long exerciseId);

  //序列信息恢复
  boolean seqRecovery(HttpServletRequest request, Long id, Long exerciseId);

  //外键信息恢复
  boolean fkRecovery(HttpServletRequest request, Long id, Long exerciseId);

  //迁移临时表到正式表
  void migrateTOrealityTable(Long exerciseId,Long historyExerciseId);
}
