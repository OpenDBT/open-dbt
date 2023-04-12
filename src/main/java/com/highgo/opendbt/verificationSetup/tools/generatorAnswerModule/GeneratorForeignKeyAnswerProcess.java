package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneFk;
import com.highgo.opendbt.verificationSetup.service.TSceneFkService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成外键校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorForeignKeyAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.FOREIGN_KEY)
public class GeneratorForeignKeyAnswerProcess implements GeneratorAnswerProcess<TCheckFk> {
  @Autowired
  TSceneFkService sceneFkService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckFk> checkFks) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkFks.isEmpty() || checkFks.size() == 0) {
      return null;
    }
    for (TCheckFk fk : checkFks) {
      //新增
      if (CheckStatus.INSERT.toString().equals(fk.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(fk.getTableName());
        builder.append(" ADD CONSTRAINT ");
        builder.append(fk.getFkName());
        builder.append(" FOREIGN KEY ");
        builder.append(" (");
        builder.append(fk.getFkFields());
        builder.append(")");
        builder.append(" REFERENCES ");
        builder.append(fk.getReference());
        builder.append(" (");
        builder.append(fk.getReferenceFields());
        builder.append(") ");
        if (StringUtils.isNotBlank(fk.getDeleteRule())) {
          builder.append("  ON DELETE  ");
          builder.append(getRule(fk.getDeleteRule()));
        }
        if (StringUtils.isNotBlank(fk.getUpdateRule())) {
          builder.append("  ON UPDATE  ");
          builder.append(getRule(fk.getUpdateRule()));
        }
        builder.append(";");
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(fk.getCheckStatus())) {
        TSceneFk sceneFk = sceneFkService.getById(fk.getSceneFkId());
        //删除
        builder.append(" ALTER TABLE ");
        builder.append(fk.getTableName());
        builder.append(" DROP CONSTRAINT ");
        builder.append(sceneFk.getFkName());
        builder.append(";");
        //增加
        builder.append(" ALTER TABLE ");
        builder.append(fk.getTableName());
        builder.append(" ADD CONSTRAINT ");
        builder.append(fk.getFkName());
        builder.append(" FOREIGN KEY ");
        builder.append(" (");
        builder.append(fk.getFkFields());
        builder.append(")");
        builder.append(" REFERENCES ");
        builder.append(fk.getReference());
        builder.append(" (");
        builder.append(fk.getReferenceFields());
        builder.append(") ");
        if (StringUtils.isNotBlank(fk.getDeleteRule())) {
          builder.append("  ON DELETE  ");
          builder.append(getRule(fk.getDeleteRule()));
        }
        if (StringUtils.isNotBlank(fk.getUpdateRule())) {
          builder.append("  ON UPDATE  ");
          builder.append(getRule(fk.getUpdateRule()));
        }
        builder.append(";");

      }
      //删除
      if (CheckStatus.DEL.toString().equals(fk.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(fk.getTableName());
        builder.append(" DROP CONSTRAINT ");
        builder.append(fk.getFkName());
        builder.append(";");
      }
    }
    return builder;
  }

  private String getRule(String rule) {
    switch (rule) {
      case "c":
        return "CASCADE";
      case "r":
        return "RESTRICT";
      case "a":
        return "NO ACTION";
      case "n":
        return "SET NULL";
      case "d":
        return "SET DEFAULT";
      default:
        return null;
    }
  }


}
