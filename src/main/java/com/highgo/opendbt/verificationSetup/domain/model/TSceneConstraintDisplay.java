package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用于约束显示
 * @Title: TSceneConstraintDisplay
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/12 10:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TSceneConstraintDisplay extends TSceneConstraint {
  private TCheckConstraint detail;
}
