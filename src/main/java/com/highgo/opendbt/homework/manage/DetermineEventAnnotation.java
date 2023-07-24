package com.highgo.opendbt.homework.manage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 判断习题对错注解
 * @author:
 * @date: 2023/3/30 13:34
 * @param: null
 * @return:
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DetermineEventAnnotation {
  ExerciseTypeEvent value();
}
