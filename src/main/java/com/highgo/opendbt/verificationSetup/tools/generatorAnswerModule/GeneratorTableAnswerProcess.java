package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.common.utils.EqualityUtils;
import com.highgo.opendbt.common.utils.WrapUtil;
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
 * @Description: 生成表校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorTableAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.TABLE)
public class GeneratorTableAnswerProcess implements GeneratorAnswerProcess<TCheckDetail> {
  @Autowired
  private TSceneDetailService sceneDetailService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckDetail> checkDetails) {

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
          builder.append(" ALTER TABLE ");
          builder.append(sceneDetail.getTableName());
          builder.append(" RENAME TO ");
          builder.append(detail.getTableName());
          WrapUtil.addWrapper(builder);
        }
        //表描述不同添加修改表描述语句
        if (!EqualityUtils.areEqual(sceneDetail.getTableDesc(), detail.getDescribe())) {
          builder.append(" COMMENT ON TABLE ");
          builder.append(detail.getTableName());
          builder.append(" IS ");
          builder.append("'");
          builder.append(detail.getDescribe());
          builder.append("'");
          WrapUtil.addWrapper(builder);
        }
      }
      if (CheckStatus.DEL.toString().equals(detail.getCheckStatus())) {
        //根据校验表信息查询原始场景表信息
        TSceneDetail sceneDetail = sceneDetailService.getById(detail.getSceneDetailId());
        builder.append(" DROP TABLE ");
        builder.append(sceneDetail.getTableName());
        WrapUtil.addWrapper(builder);
      }
      if (CheckStatus.INSERT.toString().equals(detail.getCheckStatus())) {
        builder.append(" CREATE TABLE ");
        builder.append(detail.getTableName());
        builder.append("()");
        WrapUtil.addWrapper(builder);
      }
    }
    return builder;
  }
}
