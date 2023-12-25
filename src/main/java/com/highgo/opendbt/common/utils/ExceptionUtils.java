package com.highgo.opendbt.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 异常工具类
 * @Title: ExceptionUtils
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/7/4 10:06
 */
public  class ExceptionUtils {


  public static Throwable getInnermostException(Throwable exception) {
    Throwable innerException = exception;
    while (innerException.getCause() != null) {
      innerException = innerException.getCause();
    }
    return innerException;
  }


}
