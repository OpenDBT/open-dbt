package com.highgo.opendbt.common.utils;

/**
 * @Description: 换行添加;
 * @Title: WrapUtil
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/26 14:16
 */
public class WrapUtil {
  public static void addWrap(StringBuilder res) {
    if(res.length()>0&&!res.toString().endsWith(";<br>")){
      res.append(";<br>");
    }
  }
  public static void addWrapper(StringBuilder res) {
    if(res.length()>0&&!res.toString().endsWith(";\r\n")){
      res.append(";\r\n");
    }
  }
}
