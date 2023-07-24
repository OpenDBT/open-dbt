package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import java.util.List;

public interface GeneratorDescriptionProcess<T> {
  /**
   * @description: 生成描述
   * @author:
   * @date: 2023/3/24 10:18
   * @param: [checkDetails 校验信息]
   * @return: java.lang.String
   **/
  StringBuilder generatorDescriptions(List<T> checkDetails);
}
