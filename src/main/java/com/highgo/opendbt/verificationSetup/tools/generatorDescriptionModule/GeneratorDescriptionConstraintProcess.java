package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.ConstraintType;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成约束校验描述
 * @Title: GeneratorDescriptionConstraintProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionConstraintProcess")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.CONSTRAINT)
public class GeneratorDescriptionConstraintProcess implements GeneratorDescriptionProcess<TCheckConstraint> {


  @Override
  public StringBuilder generatorDescriptions(List<TCheckConstraint> checkConstraints) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkConstraints.isEmpty() || checkConstraints.size() == 0) {
      return null;
    }
    for (TCheckConstraint constraint : checkConstraints) {
      //新增
      if (CheckStatus.INSERT.toString().equals(constraint.getCheckStatus())) {
        builder.append("新增表");
        builder.append(constraint.getTableName());
        builder.append("中");
        //主键约束
        if (ConstraintType.P.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("主键约束");
        }
        //唯一约束
        if (ConstraintType.U.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("唯一约束");
        }
        //检查约束
        if (ConstraintType.C.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("检查约束");
          builder.append(" 约束名称为");
          builder.append(constraint.getCrName());
          builder.append(" 表达式为");
          builder.append(" (");
          builder.append(constraint.getCrExpression());
          builder.append(");");
        } else
          //排他约束
          if (ConstraintType.X.toString().equalsIgnoreCase(constraint.getCrType())) {
            builder.append("排他约束");
            builder.append(" 约束名称为");
            builder.append(constraint.getCrName());
            if (StringUtils.isNotBlank(constraint.getCrIndexType())) {
              builder.append(" 其中索引类型为");
              builder.append(constraint.getCrIndexType());
              builder.append(" 表达式为");
              builder.append(" (");
              builder.append(constraint.getCrExpression());
              builder.append(")");
            }
          } else {
            builder.append(" 约束名称为");
            builder.append(constraint.getCrName());
            builder.append(" 字段为");
            builder.append(" (");
            builder.append(constraint.getCrFields());
            builder.append(")");
          }

      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(constraint.getCheckStatus())) {
        builder.append("修改表");
        builder.append(constraint.getTableName());
        builder.append("中");
        //主键约束
        if (ConstraintType.P.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("主键约束");
        }
        //唯一约束
        if (ConstraintType.U.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("唯一约束");
        }
        //检查约束
        if (ConstraintType.C.toString().equalsIgnoreCase(constraint.getCrType())) {
          builder.append("检查约束");
          builder.append(" 约束名称为");
          builder.append(constraint.getCrName());
          builder.append(" 表达式为");
          builder.append(" (");
          builder.append(constraint.getCrExpression());
          builder.append(")");
        } else
          //排他约束
          if (ConstraintType.X.toString().equalsIgnoreCase(constraint.getCrType())) {
            builder.append("排他约束");
            builder.append(" 约束名称为");
            builder.append(constraint.getCrName());
            if (StringUtils.isNotBlank(constraint.getCrIndexType())) {
              builder.append(" 其中索引类型为");
              builder.append(constraint.getCrIndexType());
              builder.append(" 表达式为");
              builder.append(" (");
              builder.append(constraint.getCrExpression());
              builder.append(")");
            }
          } else {
            builder.append(" 约束名称为");
            builder.append(constraint.getCrName());
            builder.append(" 字段为");
            builder.append(" (");
            builder.append(constraint.getCrFields());
          }
      }
      //删除
      if (CheckStatus.DEL.toString().equals(constraint.getCheckStatus())) {
        builder.append("删除表");
        builder.append(constraint.getTableName());
        builder.append("中约束");
        builder.append(constraint.getCrName());
      }
    }
    return builder;
  }


}
