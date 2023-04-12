package com.highgo.opendbt.verificationSetup.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import lombok.Data;

/**
 * @Description: 返回前端场景和校验集合
 * @Title: TSceneDetailModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/6 16:22
 */
@Data
public class TSceneDetailModel {
  private Long id;

  private Integer sceneId;

  private String tableName;

  private String tableDetail;

  private String tableDesc;

  private TCheckDetail detail;

}
