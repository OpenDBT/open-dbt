package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 生成表校验描述
 * @Title: GeneratorDescriptionTable
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionTable")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.TABLE)
public class GeneratorDescriptionTable implements GeneratorDescriptionProcess<TCheckDetail> {
  @Autowired
  private TSceneDetailService sceneDetailService;

  @Override
  public StringBuilder generatorDescriptions(List<TCheckDetail> checkDetails) {

    if (checkDetails.isEmpty() || checkDetails.size() == 0) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (TCheckDetail detail : checkDetails) {
      if (CheckStatus.UPDATE.toString().equals(detail.getCheckStatus())) {
        //根据校验表信息查询原始场景表信息
        TSceneDetail sceneDetail = sceneDetailService.getById(detail.getSceneDetailId());
        //表名不同 添加修改表名语句
        if (!sceneDetail.getTableName().equalsIgnoreCase(detail.getTableName())) {
          builder.append(" 表名由 ");
          builder.append(sceneDetail.getTableName());
          builder.append(" 修改为 ");
          builder.append(detail.getTableName());
        }
        //表描述不同添加修改表描述语句
        if (!Objects.equals(sceneDetail.getTableDesc(), detail.getDescribe())) {
          builder.append(" 表描述由 ");
          builder.append(sceneDetail.getTableDesc());
          builder.append(" 修改为 ");
          builder.append(detail.getDescribe());
        }
      }
      if (CheckStatus.DEL.toString().equals(detail.getCheckStatus())) {
        //根据校验表信息查询原始场景表信息
        TSceneDetail sceneDetail = sceneDetailService.getById(detail.getSceneDetailId());
        builder.append(" 删除表 ");
        builder.append(sceneDetail.getTableName());
      }
    }
    builder.append(",");
    return builder;
  }
}
