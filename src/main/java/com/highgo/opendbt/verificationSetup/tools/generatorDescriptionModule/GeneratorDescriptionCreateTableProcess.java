package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 处理新增表描述
 * @Title: GeneratorDescriptionCreateTableProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionCreateTableProcess")
public class GeneratorDescriptionCreateTableProcess {


  public StringBuilder generatorAnswer(TCheckDetail detail, List<TCheckField> checkFields) {
    StringBuilder res = new StringBuilder();

    //没有校验点不生产答案描述
    if (detail == null) {
      return null;
    }
      if (CheckStatus.INSERT.toString().equals(detail.getCheckStatus())) {
        //存储建表语句
        StringBuilder builder = new StringBuilder();
        List<TCheckField> fields = checkFields.stream().filter(item -> item.getTableName().equals(detail.getTableName())).collect(Collectors.toList());
        BusinessResponseEnum.FIELDNOTEMPTY.assertIsNotEmpty(fields);
        builder.append(" 新建表");
        builder.append(detail.getTableName());
        for (TCheckField field : fields) {
          builder.append(" 字段名称为");
          builder.append(field.getFieldName());
          builder.append(" 字段类型为");
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
          if (field.getFieldNonNull() == true) {
            builder.append(" 非空");
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
          if (StringUtils.isNotBlank(field.getFieldComment())) {
            builder.append(" 字段描述为");
            builder.append(field.getFieldComment());
          }
          builder.append(",");
        }
        //筛选主键
        List<String> pkList = fields.stream().filter(item -> item.getPrimaryKey() == true)
          .map(TCheckField::getFieldName).collect(Collectors.toList());
        //设置主键
        if (pkList.size() > 0) {
          builder.append("设置主键名称为");
          builder.append(detail.getTableName());
          builder.append("_pkey");
          builder.append(" 主键字段为(");
          builder.append(String.join(",", pkList));
        }
        //设置表描述
        if(StringUtils.isNotBlank(detail.getTableName())){
          builder.append(" 设置表描述为");
          builder.append(detail.getDescribe());
        }
        res.append(builder);
        res.append(",");
      }

    return res;
  }


}
