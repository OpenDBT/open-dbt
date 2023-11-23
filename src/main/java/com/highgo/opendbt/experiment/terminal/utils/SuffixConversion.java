package com.highgo.opendbt.experiment.terminal.utils;

import javax.activation.MimetypesFileTypeMap;

/**
 * @Description: 文件类型转换后缀
 * @Title: SuffixConversion
 * @Package com.highgo.opendbt.experiment.terminal.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/10/17 18:37
 */
public class SuffixConversion {

  public static String convertMimeTypeToExtension(String mimeType) {
    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
    String extension = null;//mimeTypesMap.getContentType();

    if (extension == null) {
      // 未找到对应的文件后缀
      extension = "";
    } else if (extension.startsWith(".")) {
      // 去除可能包含的初始点号
      extension = extension.substring(1);
    }

    return extension;
  }
}
