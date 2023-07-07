package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneSeq;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用于序列显示
 * @Title: TSceneSeqDisplay
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/12 10:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TSceneSeqDisplay extends TSceneSeq {
  private TCheckSeq detail;
}
