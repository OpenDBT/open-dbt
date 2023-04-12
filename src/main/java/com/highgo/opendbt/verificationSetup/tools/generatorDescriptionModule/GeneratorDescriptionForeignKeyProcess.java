package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成外键校验描述
 * @Title: GeneratorDescriptionForeignKeyProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionForeignKeyProcess")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.FOREIGN_KEY)
public class GeneratorDescriptionForeignKeyProcess implements GeneratorDescriptionProcess<TCheckFk> {


  @Override
  public StringBuilder generatorDescriptions(List<TCheckFk> checkFks) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkFks.isEmpty() || checkFks.size() == 0) {
      return null;
    }
    for (TCheckFk fk : checkFks) {
      //新增
      if (CheckStatus.INSERT.toString().equals(fk.getCheckStatus())) {
        builder.append(" 新增表");
        builder.append(fk.getTableName());
        builder.append("中外键");
        builder.append(fk.getFkName());
        builder.append("外键字段为");
        builder.append(" (");
        builder.append(fk.getFkFields());
        builder.append(")");
        builder.append("参照表名称为");
        builder.append(fk.getReference());
        builder.append("参照表字段为");
        builder.append(" (");
        builder.append(fk.getReferenceFields());
        builder.append(") ");
        if(StringUtils.isNotBlank(fk.getDeleteRule())){
          builder.append(" 删除时动作为 ");
          builder.append(fk.getDeleteRule());
        }
        if(StringUtils.isNotBlank(fk.getUpdateRule())){
          builder.append("更新时动作为");
          builder.append(fk.getUpdateRule());
        }
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(fk.getCheckStatus())) {
        builder.append(" 修改表");
        builder.append(fk.getTableName());
        builder.append("中外键");
        builder.append(fk.getFkName());
        builder.append("外键字段为");
        builder.append(" (");
        builder.append(fk.getFkFields());
        builder.append(")");
        builder.append("参照表名称为");
        builder.append(fk.getReference());
        builder.append("参照表字段为");
        builder.append(" (");
        builder.append(fk.getReferenceFields());
        builder.append(") ");
        if(StringUtils.isNotBlank(fk.getDeleteRule())){
          builder.append(" 删除时动作为");
          builder.append(fk.getDeleteRule());
        }
        if(StringUtils.isNotBlank(fk.getUpdateRule())){
          builder.append("更新时动作为");
          builder.append(fk.getUpdateRule());
        }

      }
      //删除
      if (CheckStatus.DEL.toString().equals(fk.getCheckStatus())) {
        builder.append(" 删除表");
        builder.append(fk.getTableName());
        builder.append("中外键");
        builder.append(fk.getFkName());
      }
    }
    builder.append(",");
    return builder;
  }
}
