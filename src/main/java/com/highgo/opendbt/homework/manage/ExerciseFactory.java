package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.ApplicationContextRegister;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 题目判定方法工厂
 * @Title: ExerciseFactory
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/10/18 15:50
 */
@Component
public class ExerciseFactory {

  private static Map<ExerciseTypeEvent, Determine> eventProcessMap = new ConcurrentHashMap<>();

  public ExerciseFactory() {
    Map<String, Object> beanMap = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(DetermineEventAnnotation.class);

    for (Object evetProcess : beanMap.values()) {
      DetermineEventAnnotation annotation = evetProcess.getClass().getAnnotation(DetermineEventAnnotation.class);
      eventProcessMap.put(annotation.value(), (Determine) evetProcess);
    }
  }

  public static Determine createEventProcess(int exerciseType) {
    return eventProcessMap.get(ExerciseTypeEvent.getEvent(exerciseType));
  }
}
