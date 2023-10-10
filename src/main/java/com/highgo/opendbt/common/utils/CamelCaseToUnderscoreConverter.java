package com.highgo.opendbt.common.utils;

/**
 * @Description:
 * @Title: CamelCaseToUnderscoreConverter
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/10/8 11:16
 */
public class CamelCaseToUnderscoreConverter {

  public static String convertToUnderscore(String input) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char currentChar = input.charAt(i);
      if (Character.isUpperCase(currentChar)) {
        if (i > 0) {
          result.append("_");
        }
        result.append(Character.toLowerCase(currentChar));
      } else {
        result.append(currentChar);
      }
    }
    return result.toString();
  }

  public static String convert(String originalOrderBy) {
    String[] parts = originalOrderBy.split(" "); // 拆分排序字段和排序方向
    if (parts.length == 2) {
      String fieldName = parts[0]; // 排序字段名
      String direction = parts[1]; // 排序方向（ASC 或 DESC）

      // 将排序字段名转换为下划线命名
      String underscoreFieldName = convertToUnderscore(fieldName);

      // 组合成新的排序字符串
      return underscoreFieldName.concat(" ").concat(direction);
    }
    return null;
  }
  public static boolean isSort(String originalOrderBy) {
    String[] parts = originalOrderBy.split(" "); // 拆分排序字段和排序方向
    if (parts.length == 2) {
     return true;
    }
    return false;
  }
}

