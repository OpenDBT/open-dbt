package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.common.utils.WrapUtil;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import com.highgo.opendbt.verificationSetup.service.TSceneIndexService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成索引校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorIndexAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.INDEX)
public class GeneratorIndexAnswerProcess implements GeneratorAnswerProcess<TCheckIndex> {
  @Autowired
  TSceneIndexService sceneIndexService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckIndex> checkIndexList) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkIndexList.isEmpty() || checkIndexList.size() == 0) {
      return null;
    }
    for (TCheckIndex index : checkIndexList) {
      //新增
      if (CheckStatus.INSERT.toString().equals(index.getCheckStatus())) {
        builder.append(" CREATE ");
        if (index.getIndexUnique()) {
          builder.append(" UNIQUE ");
        }
        builder.append(" INDEX ");
        builder.append(index.getIndexName());
        builder.append(" ON ");
        builder.append(index.getTableName());
        if (StringUtils.isNotBlank(index.getIndexType())) {
          builder.append(" USING ");
          builder.append(index.getIndexType());
        }
        builder.append(" (");
        builder.append(index.getIndexFields());
        builder.append(")");
        WrapUtil.addWrapper(builder);
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(index.getCheckStatus())) {
        TSceneIndex sceneIndex = sceneIndexService.getById(index.getSceneIndexId());
        //删除就索引
        builder.append(" DROP INDEX ");
        builder.append(sceneIndex.getIndexName());
        builder.append(" CASCADE;");
        //新增修改后索引
        builder.append(" CREATE ");
        if (index.getIndexUnique()) {
          builder.append(" UNIQUE ");
        }
        builder.append(" INDEX ");
        builder.append(index.getIndexName());
        builder.append(" ON ");
        builder.append(index.getTableName());
        if (StringUtils.isNotBlank(index.getIndexType())) {
          builder.append(" USING ");
          builder.append(index.getIndexType());
        }
        builder.append(" (");
        builder.append(index.getIndexFields());
        builder.append(")");
        WrapUtil.addWrapper(builder);
        //}
      }
      //删除
      if (CheckStatus.DEL.toString().equals(index.getCheckStatus())) {
        builder.append(" DROP INDEX ");
        builder.append(index.getIndexName());
        builder.append(" CASCADE");
        WrapUtil.addWrapper(builder);
      }
    }
    return builder;
  }
}
