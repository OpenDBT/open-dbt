package com.highgo.opendbt.homework.manage;

/**
 * @Description: 习题类型EVENT
 * @Title: TableInfoEvent
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 11:29
 */
public enum ExerciseTypeEvent {
  SINGLE_CHOICE(1),//单选
  MULTIPLE_CHOICE(2),//多选
  JUDGMENT(3),//判断
  FILL_IN_BLANKS(4),//填空
  SHORT_ANSWER(5),//简答
  DML(6),
  DDL(7),
  VIEW_DDL(8),//视图DDL
  FUNCTION_DDL(9),//函数/存储过程
  TRIGGER(10),//触发器
  ;


  private int event;

  ExerciseTypeEvent(int event) {
    this.event = event;
  }

  public int getEvent() {
    return event;
  }

  public void setEvent(int event) {
    this.event = event;
  }

  public static ExerciseTypeEvent getEvent(int event) {
    for (ExerciseTypeEvent typeEvent : values()) {
      if (typeEvent.event == event) {
        return typeEvent;
      }
    }
    throw new IllegalArgumentException("No matching ExerciseTypeEvent");
  }
}
