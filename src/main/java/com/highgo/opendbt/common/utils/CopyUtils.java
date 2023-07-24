package com.highgo.opendbt.common.utils;

import com.highgo.opendbt.common.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 拷贝工具类
 * @Title: CopyUtils
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/7 16:31
 */
public class CopyUtils {
  static Logger logger = LoggerFactory.getLogger(CopyUtils.class);
  public static <T, U> List<U> copyListProperties(List<T> sourceList, Class<U> targetClass) {
    List<U> targetList = new ArrayList<>();
    if (sourceList != null && !sourceList.isEmpty()) {
      for (T source : sourceList) {
        U target;
        try {
          target = targetClass.getDeclaredConstructor().newInstance();
          BeanUtils.copyProperties(source,target);
          targetList.add(target);
        } catch (Exception e) {
          logger.error("拷贝失败",e);
         throw new APIException("拷贝失败");
        }
      }
    }
    return targetList;
  }
}
