package com.highgo.opendbt.experiment.terminal.common;

public class Constants {

  /**
   * 随机生成uuid的key名
   */
  public static final String USER_KEY = "user_key";

  /**
   * 容器 名称
   */
  public static final String CONTAINER_NAME = "container_name";

  /**
   * 连接容器 exec 标志
   * true 连接容器 exec
   * false 连接 ssh
   */
  public static final String CONTAINER_EXEC = "container_exec";

  /**
   * 发送指令：连接
   */
  public static final String OPERATE_CONNECT = "connect";

  /**
   * 发送指令：命令
   */
  public static final String OPERATE_COMMAND = "command";
  /**
   * 发送指令：上传
   */
  public static final String OPERATE_UPLOAD = "upload";

  /**
   * 发送指令：ZMODEM
   */
  public static final String OPERATE_ZMODEM = "zmodem";

}
