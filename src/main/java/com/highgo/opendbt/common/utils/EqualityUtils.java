package com.highgo.opendbt.common.utils;

/**
 * @Description: 相等判断的方法
 * @Title: ObjectEqualUtils
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/26 13:54
 */
public class EqualityUtils {
//该情况是默认null和”“视为相同
  public static boolean areEqual(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if ((a == null || a.equals("")) && (b == null || b.equals(""))) {
      return true;
    }

    if (a!=null&&a.equals(b)) {
      return true;
    }
    if (a instanceof String && b instanceof String) {
      String strA = (String) a;
      String strB = (String) b;
      return strA.isEmpty() && strB.isEmpty();
    }
    return false;
  }
}
