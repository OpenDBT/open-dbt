package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneFk;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用于外键显示
 * @Title: TSceneFKDisplay
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/12 10:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TSceneFKDisplay extends TSceneFk {
  private TCheckFk detail;
}
