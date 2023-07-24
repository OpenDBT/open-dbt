package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import com.highgo.opendbt.ApplicationContextRegister;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 事件处理工厂类
 * @Title: EventProcessFactory
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:49
 */

@Component
public class EventProcessFactory {


  private static Map<TableInfoEvent, EventProcess> eventProcessMap = new ConcurrentHashMap<>();

  public EventProcessFactory() {
    Map<String, Object> beanMap = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(TableInfoEventAnnotation.class);

    for (Object evetProcess : beanMap.values()) {
      TableInfoEventAnnotation annotation = evetProcess.getClass().getAnnotation(TableInfoEventAnnotation.class);
      eventProcessMap.put(annotation.value(), (EventProcess) evetProcess);
    }
  }

  public static EventProcess createEventProcess(TableInfoEvent event) {
    return eventProcessMap.get(event);
  }
}
