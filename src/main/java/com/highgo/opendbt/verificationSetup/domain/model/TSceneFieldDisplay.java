package com.highgo.opendbt.verificationSetup.domain.model;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用于DDL页面字段显示
 * @Title: TSceneFieldDisplay
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/5/18 9:28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TSceneFieldDisplay extends TSceneField {

  private TCheckField detail;

}
