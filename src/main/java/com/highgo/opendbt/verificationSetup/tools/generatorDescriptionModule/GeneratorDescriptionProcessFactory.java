package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.ApplicationContextRegister;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerEventAnnotation;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 生成描述
 * @Title: CheckEventProcessFactory
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:49
 */

@Component
public class GeneratorDescriptionProcessFactory {


  private static Map<TableInfoEvent, GeneratorDescriptionProcess> eventProcessMap = new ConcurrentHashMap<>();

  public GeneratorDescriptionProcessFactory() {
    Map<String, Object> beanMap = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(GeneratorDescriptionEventAnnotation.class);

    for (Object evetProcess : beanMap.values()) {
      GeneratorDescriptionEventAnnotation annotation = evetProcess.getClass().getAnnotation(GeneratorDescriptionEventAnnotation.class);
      eventProcessMap.put(annotation.value(), (GeneratorDescriptionProcess) evetProcess);
    }
  }

  public static GeneratorDescriptionProcess createEventProcess(TableInfoEvent event) {
    return eventProcessMap.get(event);
  }
}
