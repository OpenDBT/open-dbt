package com.highgo.opendbt.common.exception.assertion;

import com.highgo.opendbt.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public interface Assert {
  /**
   * 创建异常
   */
  BaseException newException(Object... args);

  /**
   * 创建异常
   */
  BaseException newException(Throwable t, Object... args);

  /**
   * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
   *
   * @param obj 待判断对象
   */
  default void assertNotNull(Object obj) {
    if (obj == null) {
      throw newException(obj);
    }
  }

  /**
   * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
   * <p>异常信息<code>message</code>支持传递参数方式，避免在判断之前进行字符串拼接操作
   *
   * @param obj  待判断对象
   * @param args message占位符对应的参数列表
   */
  default void assertNotNull(Object obj, Object... args) {
    if (obj == null) {
      throw newException(args);
    }
  }

  /**
   * 如果为false抛出异常
   **/
  default void assertIsTrue(boolean res, Object... args) {
    if (!res) {
      throw newException(args);
    }
  }

  default void assertIsTrue(boolean res) {
    if (!res) {
      throw newException(null);
    }
  }

  /**
   * 如果为a,b不相等抛出异常
   **/
  default void assertIsEquals(Object obj1, Object obj2, Object... args) {

    // 如果两个对象都为null或空字符串，视为相等，直接返回
    if ((obj1 == null || obj1.toString().trim().equals("")) && (obj2 == null || obj2.toString().trim().equals(""))) {
      return;
    }
    // 如果只有一个对象为null或空字符串，视为不相等，抛出异常
    else if ((obj1 == null || obj1.toString().trim().equals("")) || (obj2 == null || obj2.toString().trim().equals(""))) {
      throw newException(args);
    }
    // 如果两个对象都不为null或空字符串，比较它们是否相等
    else if (!obj1.equals(obj2)) {
      throw newException(args);
    }
  }
  /**
   * 如果为a,b不相等抛出异常(a,b为数组转换成的字符串用逗号隔开)
   **/
  default void assertIsEqualsArrayString(String obj1, String obj2, Object... args) {
    // 将逗号分隔的字符串转换为数组
    String[] arr1 = obj1.split(",");
    String[] arr2 = obj2.split(",");

    // 如果两个字符串长度不同，或者包含不同的元素，抛出异常
    if (arr1.length != arr2.length || !Arrays.asList(arr1).containsAll(Arrays.asList(arr2))) {
      throw newException(args);
    }
  }

  /**
   * 如果为true抛出异常
   **/
  default void assertIsFalse(boolean res, Object... args) {
    if (res) {
      throw newException(args);
    }
  }

  default void assertIsFalse(boolean res) {
    if (res) {
      throw newException(null);
    }
  }

  /**
   * 如果不为空抛出异常
   **/
  default void assertIsEmpty(Object obj, Object... args) {

    if (obj instanceof List) {
      if (obj != null && ((List) obj).size() > 0) {
        throw newException(args);
      }
    } else {
      if (obj != null) {
        throw newException(args);
      }
    }
  }

  /**
   * 如果为空抛出异常
   **/
  default void assertIsNotEmpty(Object obj, Object... args) {
    if (obj instanceof List) {
      if (obj == null || ((List) obj).size() == 0) {
        throw newException(args);
      }
    } else {
      if (obj == null) {
        throw newException(args);
      }
    }
  }
}
