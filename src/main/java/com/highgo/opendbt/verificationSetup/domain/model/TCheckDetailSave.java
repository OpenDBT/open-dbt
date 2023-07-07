package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import lombok.Data;

/**
 * @Description: 表信息保存
 * @Title: TCheckDetailSave
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/21 15:15
 */
@Data
public class TCheckDetailSave {
  private TCheckDetail detail;
  private Integer sceneId;
}
