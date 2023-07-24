package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

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
public class GeneratorAnswerProcessFactory {


  private static Map<TableInfoEvent, GeneratorAnswerProcess> eventProcessMap = new ConcurrentHashMap<>();

  public GeneratorAnswerProcessFactory() {
    Map<String, Object> beanMap = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(GeneratorAnswerEventAnnotation.class);

    for (Object evetProcess : beanMap.values()) {
      GeneratorAnswerEventAnnotation annotation = evetProcess.getClass().getAnnotation(GeneratorAnswerEventAnnotation.class);
      eventProcessMap.put(annotation.value(), (GeneratorAnswerProcess) evetProcess);
    }
  }

  public static GeneratorAnswerProcess createEventProcess(TableInfoEvent event) {
    return eventProcessMap.get(event);
  }
}
