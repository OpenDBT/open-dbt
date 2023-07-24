package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

/**
 * @Description: 表相关信息枚举 字段，索引，约束，外键，序列等
 * @Title: TableInfoEvent
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 11:29
 */
public enum TableInfoEvent {
  TABLE_EXISTS("TABLE_EXISTS"),
  TABLE("TABLE"),
  FIELD("FIELD"),
  INDEX("INDEX"),
  CONSTRAINT("CONSTRAINT"),
  FOREIGN_KEY("FOREIGN_KEY"),
  SEQUENCE("SEQUENCE");



  private String event;

  TableInfoEvent(String event) {
    this.event = event;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
}
