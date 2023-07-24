package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.WrapUtil;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 处理新增表答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorCreateTableProcess")
public class GeneratorCreateTableProcess {


  public StringBuilder generatorAnswer(TCheckDetail detail, List<TCheckField> checkFields) {
    StringBuilder res = new StringBuilder();
    //没有校验点不生产答案
    if (detail == null) {
      return null;
    }
    if(checkFields==null||checkFields.size()==0){
      return null;
    }
    if (CheckStatus.INSERT.toString().equals(detail.getCheckStatus())) {
      //存储建表语句
      StringBuilder builder = new StringBuilder();
      //存储主键，表描述，字段描述
      StringBuilder other = new StringBuilder();

      List<TCheckField> fields = checkFields.stream()
        .filter(item -> item.getTableName()
        .equals(detail.getTableName())).collect(Collectors.toList());
      BusinessResponseEnum.FIELDNOTEMPTY.assertIsNotEmpty(fields);
      builder.append(" CREATE TABLE  ");
      builder.append(detail.getTableName());
      builder.append(" (  ");
      for (TCheckField field : fields) {
        builder.append(field.getFieldName());
        builder.append(" ");
        //拼接varchar类型字段
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
        if (field.getFieldNonNull()!=null&&field.getFieldNonNull() == true) {
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
        builder.append(",");
        if (StringUtils.isNotBlank(field.getFieldComment())) {
          other.append(" COMMENT ON COLUMN ");
          other.append(detail.getTableName());
          other.append(".");
          other.append(field.getFieldName());
          other.append(" IS ");
          if (StringUtils.isBlank(field.getFieldComment())) {
            other.append("'");
            other.append("'");
          } else {
            other.append("'");
            other.append(field.getFieldComment());
            other.append("'");
          }
          WrapUtil.addWrapper(other);
        }
      }
      //筛选主键
      List<String> pkList = fields.stream().filter(item -> item.getPrimaryKey()!=null&&item.getPrimaryKey() == true)
        .map(TCheckField::getFieldName).collect(Collectors.toList());
      //设置主键
      if (pkList.size() > 0) {
        builder.append(" CONSTRAINT ");
        builder.append(detail.getTableName());
        builder.append("_pkey");
        builder.append("  PRIMARY KEY  (");
        builder.append(String.join(",", pkList));
        builder.append(")");
      } else {
        builder.deleteCharAt(builder.lastIndexOf(","));
      }
      builder.append(" )  ");
      WrapUtil.addWrapper(builder);
      if (StringUtils.isNotBlank(detail.getDescribe())) {
        //设置表描述
        builder.append(" COMMENT ON TABLE ");
        builder.append(detail.getTableName());
        builder.append(" IS ");
        builder.append("'");
        builder.append(detail.getDescribe());
        builder.append("'");
        WrapUtil.addWrapper(builder);
      }

      //设置字段描述
      builder.append(other);
      res.append(builder);
    }

    return res;
  }


}
