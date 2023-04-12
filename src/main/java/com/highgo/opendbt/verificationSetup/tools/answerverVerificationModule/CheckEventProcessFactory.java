package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.highgo.opendbt.ApplicationContextRegister;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 校验事件处理工厂类
 * @Title: CheckEventProcessFactory
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:49
 */

@Component
public class CheckEventProcessFactory {


  private static Map<TableInfoEvent, CheckProcess> eventProcessMap = new ConcurrentHashMap<>();

  public CheckEventProcessFactory() {
    Map<String, Object> beanMap = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(CheckEventAnnotation.class);

    for (Object evetProcess : beanMap.values()) {
      CheckEventAnnotation annotation = evetProcess.getClass().getAnnotation(CheckEventAnnotation.class);
      eventProcessMap.put(annotation.value(), (CheckProcess) evetProcess);
    }
  }

  public static CheckProcess createEventProcess(TableInfoEvent event) {
    return eventProcessMap.get(event);
  }
}
