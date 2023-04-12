package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

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
 * @Description: 生成字段校验描述
 * @Title: GeneratorDescriptionFieldProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionFieldProcess")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.FIELD)
public class GeneratorDescriptionFieldProcess implements GeneratorDescriptionProcess<TCheckField> {

  @Autowired
  private TSceneFieldService sceneFieldService;

  @Override
  public StringBuilder generatorDescriptions(List<TCheckField> checkFields) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkFields.isEmpty() || checkFields.size() == 0) {
      return null;
    }
    for (TCheckField field : checkFields) {
      //新增
      if (CheckStatus.INSERT.toString().equals(field.getCheckStatus())) {
        builder.append(" 在表 ");
        builder.append(field.getTableName());
        builder.append(" 中新增字段 ");
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
          builder.append(" 非空 ");
        }
        //设置默认值
        if (StringUtils.isNotBlank(field.getFieldDefault())) {
          builder.append(" 默认值为 ");
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
        //设置字段描述
        if (StringUtils.isNotBlank(field.getFieldComment())) {
          builder.append(" 字段描述为 ");
          builder.append(field.getFieldComment());
        }
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(field.getCheckStatus())) {
        TSceneField sceneField = sceneFieldService.getById(field.getSceneFieldId());
        //表名修改
        if (!Objects.equals(sceneField.getFieldName(), field.getFieldName())) {
          builder.append(" 修改表 ");
          builder.append(field.getTableName());
          builder.append(" 的字段名称由 ");
          builder.append(sceneField.getFieldName());
          builder.append("修改为");
          builder.append(field.getFieldName());
        }
        //字段类型修改
        if (!Objects.equals(sceneField.getFieldType(), field.getFieldType())
          || !Objects.equals(sceneField.getFieldLength(), field.getFieldLength())
          || !Objects.equals(sceneField.getDecimalNum(), field.getDecimalNum())) {
          builder.append(" 修改表 ");
          builder.append(field.getTableName());
          builder.append("字段");
          builder.append(sceneField.getFieldName());
          builder.append(" 的类型由");
          builder.append(sceneField.getFieldType());
          if ("varchar".equalsIgnoreCase(sceneField.getFieldType())) {
            builder.append("(");
            builder.append(sceneField.getFieldLength());
            builder.append(")");
          }
          //拼接带小数类型字段
          if ("numeric".equalsIgnoreCase(sceneField.getFieldType())) {
            builder.append("(");
            builder.append(sceneField.getFieldLength());
            builder.append(",");
            builder.append(sceneField.getDecimalNum());
            builder.append(")");
          }
          builder.append("修改为");
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
        }
        //非空修改
        if (!Objects.equals(sceneField.getFieldNonNull(), field.getFieldNonNull())) {
          builder.append(" 设置表");
          builder.append(field.getTableName());
          builder.append("字段");
          builder.append(sceneField.getFieldName());
          builder.append("的非空设置为");
          if (sceneField.getFieldNonNull()) {
            builder.append("可为空");
          } else {
            builder.append("非空");
          }
        }
        //默认值修改
        if (!Objects.equals(sceneField.getFieldDefault(), field.getFieldDefault())) {
          builder.append(" 修改表");
          builder.append(field.getTableName());
          builder.append("中");
          builder.append(sceneField.getFieldName());
          builder.append("字段默认值为");
          builder.append(field.getFieldDefault());
        }
        //字段描述修改
        if (!Objects.equals(sceneField.getFieldComment(), field.getFieldComment())) {
          builder.append(" 设置表");
          builder.append(field.getTableName());
          builder.append("中");
          builder.append(field.getFieldName());
          builder.append("字段描述为");
          builder.append(field.getFieldComment());
        }

      }
      //删除
      if (CheckStatus.DEL.toString().equals(field.getCheckStatus())) {
        builder.append(" 删除表");
        builder.append(field.getTableName());
        builder.append("中字段");
        builder.append(field.getFieldName());
      }
    }
    builder.append(",");
    return builder;
  }
}
