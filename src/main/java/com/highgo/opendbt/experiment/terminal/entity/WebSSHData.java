package com.highgo.opendbt.experiment.terminal.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WebSSHData {

  /**
   * 操作
   */
  private String operate;

  /**
   * ip地址
   */
  private String host;

  /**
   * 端口号
   */
  private Integer port;

  /**
   * 用户名
   */
  private String username;

  /**
   * 密码
   */
  private String password;

  /**
   * 指令
   */
  private String command;
  /**
   * 上传
   */
  private String fileData;

  /**
   * zmodemData
   */
  public String zmodemData;
}
