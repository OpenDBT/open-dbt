package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import java.util.List;

public interface GeneratorAnswerProcess<T> {
  /**
   * @description: 生成答案
   * @author:
   * @date: 2023/3/24 10:18
   * @param: [checkDetails 校验信息]
   * @return: java.lang.String
   **/

  StringBuilder generatorAnswer(List<T> checkDetails);
}
