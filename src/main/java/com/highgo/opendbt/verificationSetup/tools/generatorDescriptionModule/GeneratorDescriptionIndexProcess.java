package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import com.highgo.opendbt.verificationSetup.service.TSceneIndexService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成索引校验描述
 * @Title: GeneratorDescriptionIndexProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionIndexProcess")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.INDEX)
public class GeneratorDescriptionIndexProcess implements GeneratorDescriptionProcess<TCheckIndex> {
  @Autowired
  TSceneIndexService sceneIndexService;

  @Override
  public StringBuilder generatorDescriptions(List<TCheckIndex> checkIndexList) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkIndexList.isEmpty() || checkIndexList.size() == 0) {
      return null;
    }
    for (TCheckIndex index : checkIndexList) {
      //新增
      if (CheckStatus.INSERT.toString().equals(index.getCheckStatus())) {
        builder.append(" 为表");
        builder.append(index.getTableName());
        builder.append("中字段(");
        builder.append(index.getIndexFields());
        builder.append(")");
        builder.append(" 新建索引,名称为 ");
        builder.append(index.getIndexName());
        builder.append(" 类型为");
        builder.append(index.getIndexType());
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(index.getCheckStatus())) {
        TSceneIndex sceneIndex = sceneIndexService.getById(index.getSceneIndexId());
        builder.append(" 表");
        builder.append(index.getTableName());
        builder.append("中索引");
        builder.append(sceneIndex.getIndexName());
        builder.append(" 修改索引名称为");
        builder.append(index.getIndexName());
        builder.append(" 字段为(");
        builder.append(index.getIndexFields());
        builder.append(")");
        builder.append(" 类型为");
        builder.append(index.getIndexType());
      }
      //删除
      if (CheckStatus.DEL.toString().equals(index.getCheckStatus())) {
        builder.append(" 删除索引");
        builder.append(index.getIndexName());
      }
    }
    builder.append(",");
    return builder;
  }
}
