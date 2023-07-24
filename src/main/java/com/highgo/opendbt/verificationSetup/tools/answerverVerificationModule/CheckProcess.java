package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import java.util.List;

public interface CheckProcess<T1,T2> {
/**
 * @description:校验答案接口
 * @author:
 * @date: 2023/3/22 17:40
 * @param: checkDetails 检验点表信息 ，details 答案提取信息
 * @return:
 **/
  //校验模块
   boolean verify(List<T1> checkDetails, List<T2> details);
}
