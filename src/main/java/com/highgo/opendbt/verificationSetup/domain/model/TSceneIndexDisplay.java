package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用于索引显示
 * @Title: TSceneIndexDisplay
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/12 10:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TSceneIndexDisplay extends TSceneIndex {
  private TCheckIndex detail;
}
