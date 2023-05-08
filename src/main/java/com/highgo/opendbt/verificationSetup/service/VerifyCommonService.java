package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
//校验点模块 公共服务类
public interface VerifyCommonService {
  //一键生成答案
  StoreAnswer generatesAnswer(HttpServletRequest request, int sceneId, int exerciseId);

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
}
