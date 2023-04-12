package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import com.highgo.opendbt.verificationSetup.service.TSceneConstraintService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.ConstraintType;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成约束校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorConstraintAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.CONSTRAINT)
public class GeneratorConstraintAnswerProcess implements GeneratorAnswerProcess<TCheckConstraint> {
  @Autowired
  TSceneConstraintService sceneConstraintService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckConstraint> checkConstraints) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkConstraints.isEmpty() || checkConstraints.size() == 0) {
      return null;
    }
    for (TCheckConstraint constraint : checkConstraints) {
      //新增
      if (CheckStatus.INSERT.toString().equals(constraint.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(constraint.getTableName());
        builder.append(" ADD CONSTRAINT ");
        builder.append(constraint.getCrName());
        //主键约束
        if (ConstraintType.P.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" PRIMARY KEY ");
        }
        //唯一约束
        if (ConstraintType.U.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" UNIQUE ");
        }
        //检查约束
        if (ConstraintType.C.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" CHECK ");
          builder.append(" (");
          builder.append(constraint.getCrExpression());
          builder.append(");");
        } else
          //排他约束
          if (ConstraintType.X.toString().equalsIgnoreCase(constraint.getCrType())) {
            builder.append(" EXCLUDE USING ");
            if (StringUtils.isNotBlank(constraint.getCrIndexType())) {
              builder.append(constraint.getCrIndexType());
              builder.append(" (");
              builder.append(constraint.getCrExpression());
              builder.append(");");
            }
          } else {
            builder.append(" (");
            builder.append(constraint.getCrFields());
            builder.append(");");
          }
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(constraint.getCheckStatus())) {
        TSceneConstraint sceneConstraint = sceneConstraintService.getById(constraint.getSceneConstraintId());
        //先删除
        builder.append(" ALTER TABLE ");
        builder.append(constraint.getTableName());
        builder.append(" DROP CONSTRAINT ");
        builder.append(sceneConstraint.getCrName());
        builder.append(";");
        //后新增
        builder.append(" ALTER TABLE ");
        builder.append(constraint.getTableName());
        builder.append(" ADD CONSTRAINT ");
        builder.append(constraint.getCrName());
        //主键约束
        if (ConstraintType.P.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" PRIMARY KEY ");
        }
        //唯一约束
        if (ConstraintType.U.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" UNIQUE ");
        }
        //检查约束
        if (ConstraintType.C.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append(" CHECK ");
          builder.append(" (");
          builder.append(constraint.getCrExpression());
          builder.append(");");
        } else
          //排他约束
          if (ConstraintType.X.toString().equalsIgnoreCase(constraint.getCrType())) {
            builder.append(" EXCLUDE USING ");
            if (StringUtils.isNotBlank(constraint.getCrIndexType())) {
              builder.append(constraint.getCrIndexType());
              builder.append(" (");
              builder.append(constraint.getCrExpression());
              builder.append(");");
            }
          } else {
            builder.append(" (");
            builder.append(constraint.getCrFields());
            builder.append(");");
          }
      }
      //删除
      if (CheckStatus.DEL.toString().equals(constraint.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(constraint.getTableName());
        builder.append(" DROP CONSTRAINT ");
        builder.append(constraint.getCrName());
        builder.append(";");
      }
    }
    return builder;
  }


}
