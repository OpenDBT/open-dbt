package com.highgo.opendbt.experiment.manageer;

/**
 * @Description:
 * @Title: ShellOutputCallback
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/24 17:56
 */
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;

public class ShellOutputCallback extends ExecStartResultCallback {
  private StringBuilder output = new StringBuilder();

  @Override
  public void onNext(Frame frame) {
    output.append(new String(frame.getPayload()));
  }

  public String getOutput() {
    return output.toString();
  }
}
