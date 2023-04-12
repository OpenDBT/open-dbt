package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @description: 校验注解
 * @author:
 * @date: 2023/3/30 13:34
 * @param: null
 * @return:
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckEventAnnotation {
  TableInfoEvent value();
}
