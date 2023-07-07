package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 字段校验模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkFieldProcess")
@CheckEventAnnotation(value = TableInfoEvent.FIELD)
public class CheckFieldProcess implements CheckProcess<TCheckField, TCheckField> {
  Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * @description:
   * @author:
   * @date: 2023/3/23 13:20
   * @param: [checkFields 校验点, fields 答案字段信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckField> checkFields, List<TCheckField> fields) {
    if (checkFields.isEmpty() || checkFields.size() == 0) {
      return true;
    }
    for (TCheckField field : checkFields) {
      if (CheckStatus.INSERT.toString().equals(field.getCheckStatus()) || CheckStatus.UPDATE.toString().equals(field.getCheckStatus())) {
        List<TCheckField> fieldList = fields.stream()
          .filter(item -> item.getFieldName().equalsIgnoreCase(field.getFieldName()))
          .collect(Collectors.toList());
        //校验字段不存在抛出异常
        BusinessResponseEnum.NOFIELDSFOUND.assertIsNotEmpty(fieldList, field.getFieldName());
        //答案中的对应字段
        TCheckField tCheckField = fieldList.get(0);
        //比对答案中的字段
        fieldComparison(field, tCheckField);
      }
      //删除
      if (CheckStatus.DEL.toString().equals(field.getCheckStatus())) {
        //校验表中字段是否删除
        List<TCheckField> fieldList = fields.stream()
          .filter(item -> item.getFieldName().equalsIgnoreCase(field.getFieldName()))
          .collect(Collectors.toList());
        //校验字段不存在抛出异常
        BusinessResponseEnum.FIELDNOTDELETED.assertIsEmpty(fieldList, field.getFieldName());
      }
    }

    return true;
  }

  /**
   * @description:
   * @author:
   * @date: 2023/3/23 13:54
   * @param: [field 校验字段信息, tCheckField 答案字段信息]
   * @return: void
   **/
  private void fieldComparison(TCheckField field, TCheckField tCheckField) {
    //判断字段类型是否相同
    BusinessResponseEnum.DIFFERENTFIELDTYPES.assertIsTrue(field.getFieldType().equalsIgnoreCase(tCheckField.getFieldType())||(field.getFieldType().equalsIgnoreCase("char")&&tCheckField.getFieldType().equalsIgnoreCase("bpchar"))
      , field.getFieldName(), field.getFieldType(), tCheckField.getFieldType());
    //判断字段长度是否相同
    if("varchar".equalsIgnoreCase(field.getFieldType())){
      BusinessResponseEnum.FIELDLENGTHISDIFFERENT.assertIsEquals(field.getFieldLength(), tCheckField.getFieldLength(), field.getFieldName(), field.getFieldLength(), tCheckField.getFieldLength());
    }
    //判断默认值是否相同
    BusinessResponseEnum.FIELDDEFAULTISDIFFERENT.assertIsEquals(field.getFieldDefault(), tCheckField.getFieldDefault(), field.getFieldName(), field.getFieldDefault(), tCheckField.getFieldDefault());
    //判断是否非空
    BusinessResponseEnum.WHETHERITISNOTEMPTYISDIFFERENT.assertIsEquals(field.getFieldNonNull(), tCheckField.getFieldNonNull(), field.getFieldName(), field.getFieldNonNull(), tCheckField.getFieldNonNull());
    //判断字段描述
    BusinessResponseEnum.FIELDDESCRIPTIONSDIFFER.assertIsEquals(field.getFieldComment(), tCheckField.getFieldComment(), field.getFieldName(), field.getFieldComment(), tCheckField.getFieldComment());
    //判断是否自增
    //BusinessResponseEnum.WHETHERSELFINCREASINGISDIFFERENT.assertIsEquals(field.getAutoIncrement(), tCheckField.getAutoIncrement(), field.getFieldName(), field.getAutoIncrement(), tCheckField.getAutoIncrement());
    //判断小数点位数
    if("numeric".equalsIgnoreCase(tCheckField.getFieldType())){
      BusinessResponseEnum.DIFFERENTDECIMALPLACES.assertIsEquals(field.getDecimalNum(), tCheckField.getDecimalNum(), field.getFieldName(), field.getDecimalNum(), tCheckField.getDecimalNum());
    }
  }

}
