package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 生成字段校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorFieldAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.FIELD)
public class GeneratorFieldAnswerProcess implements GeneratorAnswerProcess<TCheckField> {

  @Autowired
  private TSceneFieldService sceneFieldService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckField> checkFields) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkFields.isEmpty() || checkFields.size() == 0) {
      return null;
    }
    for (TCheckField field : checkFields) {
      //新增
      if (CheckStatus.INSERT.toString().equals(field.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(field.getTableName());
        builder.append(" ADD ");
        builder.append(field.getFieldName());
        builder.append(" ");
        builder.append(field.getFieldType());
        if ("varchar".equalsIgnoreCase(field.getFieldType())) {
          builder.append("(");
          builder.append(field.getFieldLength());
          builder.append(")");
        }
        //拼接带小数类型字段
        if ("numeric".equalsIgnoreCase(field.getFieldType())) {
          builder.append("(");
          builder.append(field.getFieldLength());
          builder.append(",");
          builder.append(field.getDecimalNum());
          builder.append(")");
        }
        //设置是否为空
        if (field.getFieldNonNull() == true) {
          builder.append(" not null ");
        }
        //设置默认值
        if (StringUtils.isNotBlank(field.getFieldDefault())) {
          builder.append(" DEFAULT ");
          if ("varchar".equalsIgnoreCase(field.getFieldType())
            || "char".equalsIgnoreCase(field.getFieldType())
            || "text".equalsIgnoreCase(field.getFieldType())) {
            builder.append("'");
            builder.append(field.getFieldDefault());
            builder.append("'");
          } else {
            builder.append(field.getFieldDefault());
          }
        }
        builder.append(" ; ");
        //设置字段描述
        if (StringUtils.isNotBlank(field.getFieldComment())) {
          builder.append(" COMMENT ON COLUMN ");
          builder.append(field.getTableName());
          builder.append(".");
          builder.append(field.getFieldName());
          builder.append(" IS ");
          if (StringUtils.isBlank(field.getFieldComment())) {
            builder.append("'");
            builder.append("'");
          } else {
            builder.append("'");
            builder.append(field.getFieldComment());
            builder.append("'");
          }
          builder.append(";");
        }
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(field.getCheckStatus())) {
        TSceneField sceneField = sceneFieldService.getById(field.getSceneFieldId());

        //字段类型修改
        if (!Objects.equals(sceneField.getFieldType(), field.getFieldType())
          || !Objects.equals(sceneField.getFieldLength(), field.getFieldLength())
          || !Objects.equals(sceneField.getDecimalNum(), field.getDecimalNum())) {
          builder.append(" ALTER TABLE ");
          builder.append(field.getTableName());
          builder.append(" ALTER COLUMN ");
          builder.append(sceneField.getFieldName());
          builder.append(" TYPE ");
          builder.append(field.getFieldType());
          if ("varchar".equalsIgnoreCase(field.getFieldType())) {
            builder.append("(");
            builder.append(field.getFieldLength());
            builder.append(")");
          }
          //拼接带小数类型字段
          if ("numeric".equalsIgnoreCase(field.getFieldType())) {
            builder.append("(");
            builder.append(field.getFieldLength());
            builder.append(",");
            builder.append(field.getDecimalNum());
            builder.append(")");
          }
          builder.append(" USING ");
          builder.append(sceneField.getFieldName());
          builder.append("::");
          builder.append(field.getFieldType());
          builder.append(";");
        }
        //非空修改
        if (!Objects.equals(sceneField.getFieldNonNull(), field.getFieldNonNull())) {
          builder.append(" ALTER TABLE ");
          builder.append(field.getTableName());
          builder.append(" ALTER COLUMN ");
          builder.append(sceneField.getFieldName());
          if (sceneField.getFieldNonNull()) {
            builder.append(" DROP ");
          } else {
            builder.append(" SET ");
          }
          builder.append(" NOT NULL ");
          builder.append(";");
        }
        //默认值修改
        if (!Objects.equals(sceneField.getFieldDefault(), field.getFieldDefault())) {
          builder.append(" ALTER TABLE ");
          builder.append(field.getTableName());
          builder.append(" ALTER COLUMN ");
          builder.append(sceneField.getFieldName());
          if (StringUtils.isNotBlank(sceneField.getFieldDefault())) {
            builder.append(" DROP ");
            builder.append(" DEFAULT ");
          } else {
            builder.append(" SET ");
            builder.append(" DEFAULT ");
            builder.append(field.getFieldDefault());
          }
          builder.append(";");
        }
        //字段描述修改
        if (!Objects.equals(sceneField.getFieldComment(), field.getFieldComment())) {
          builder.append(" COMMENT ON COLUMN ");
          builder.append(field.getTableName());
          builder.append(".");
          builder.append(sceneField.getFieldName());
          builder.append(" IS ");
          if (StringUtils.isBlank(field.getFieldComment())) {
            builder.append("'");
            builder.append("'");
          } else {
            builder.append("'");
            builder.append(field.getFieldComment());
            builder.append("'");
          }
          builder.append(";");
        }
        //表名修改
        if (!Objects.equals(sceneField.getFieldName(), field.getFieldName())) {
          builder.append(" ALTER TABLE ");
          builder.append(field.getTableName());
          builder.append(" RENAME COLUMN ");
          builder.append(sceneField.getFieldName());
          builder.append(" TO ");
          builder.append(field.getFieldName());
          builder.append(";");
        }
      }
      //删除
      if (CheckStatus.DEL.toString().equals(field.getCheckStatus())) {
        builder.append(" ALTER TABLE ");
        builder.append(field.getTableName());
        builder.append(" DROP COLUMN ");
        builder.append(field.getFieldName());
        builder.append(";");
      }
    }


    return builder;
  }
}
