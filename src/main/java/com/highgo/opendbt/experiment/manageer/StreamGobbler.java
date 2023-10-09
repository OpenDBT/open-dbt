package com.highgo.opendbt.experiment.manageer;

/**
 * @Description:
 * @Title: StreamGobbler
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/1 11:26
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
  private InputStream inputStream;
  private String logPrefix;

  public StreamGobbler(InputStream inputStream, String logPrefix) {
    this.inputStream = inputStream;
    this.logPrefix = logPrefix;
  }

  @Override
  public void run() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // 处理每一行输出，例如打印到日志中
        System.out.println(logPrefix + ": " + line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void build(InputStream inputStream, String logPrefix) {
    StreamGobbler streamGobbler = new StreamGobbler(inputStream, logPrefix);
    streamGobbler.start();
  }

  public static void down(InputStream inputStream, String logPrefix) {
    StreamGobbler streamGobbler = new StreamGobbler(inputStream, logPrefix);
    streamGobbler.start();
  }
}
