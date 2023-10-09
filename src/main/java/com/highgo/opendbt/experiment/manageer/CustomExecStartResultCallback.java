package com.highgo.opendbt.experiment.manageer;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * @Description: 类以处理输出流和错误流：
 * @Title: CustomExecStartResultCallback
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/25 10:07
 */


public class CustomExecStartResultCallback extends ResultCallback.Adapter<Frame> {
  private final ByteArrayOutputStream output;
  private final ByteArrayOutputStream error;

  private boolean isStdout = false;

  public CustomExecStartResultCallback() {
    this.output = new ByteArrayOutputStream();
    this.error = new ByteArrayOutputStream();
  }

  @Override
  public void onNext(Frame item) {
    try {
      super.onNext(item);

      // 检查帧的 Payload 来确定是 stdout 还是 stderr
      if (item.getPayload() != null) {
        if (isStdout) {
          output.write(item.getPayload());
          System.out.println(item.getPayload());
        } else {
          error.write(item.getPayload());
          System.out.println(item.getPayload());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    super.close();
    try {
      output.close();
      error.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getOutput() {
    return output.toString();
  }

  public String getError() {
    return error.toString();
  }
}
