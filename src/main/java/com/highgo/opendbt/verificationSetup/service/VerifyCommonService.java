package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.model.GeneratorDescription;
import com.highgo.opendbt.verificationSetup.domain.model.RecoveryModel;
import com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
//校验点模块 公共服务类
public interface VerifyCommonService {
  //一键生成答案
  StoreAnswer generatesAnswer(HttpServletRequest request, int sceneId, int exerciseId);

  //测试运行
  boolean testRun(HttpServletRequest request, TestRunModel model);

  //一键恢复
  boolean recovery(HttpServletRequest request, RecoveryModel model);

  //自动生成描述
  String generateDescriptions(HttpServletRequest request, GeneratorDescription model);
}
