package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 校验表模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkEventAnnotation")
@CheckEventAnnotation(value = TableInfoEvent.TABLE)
public class CheckDetailProcess implements CheckProcess<TCheckDetail, TCheckDetail> {
  /**
   * @description:
   * @author:
   * @date: 2023/3/23 9:51
   * @param: [checkDetails 校验点表信息, details 答案表信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckDetail> checkDetails, List<TCheckDetail> details) {
    if (checkDetails.isEmpty() || checkDetails.size() == 0) {
      return true;
    }
    for (TCheckDetail detail : checkDetails) {
      //新增
      //修改
      if ((CheckStatus.INSERT.toString().equals(detail.getCheckStatus())) || (CheckStatus.UPDATE.toString().equals(detail.getCheckStatus()))) {
        //答案中没有相关表信息抛出异常
        BusinessResponseEnum.TABLECREATIONNOTIMPLEMENTED.assertIsNotEmpty(details, detail.getTableName());
        //答案表信息
        TCheckDetail answerDetail = details.get(0);
        if (StringUtils.isBlank(detail.getDescribe())) {
          if (!(StringUtils.isBlank(detail.getDescribe()) && StringUtils.isBlank(answerDetail.getDescribe()))) {
            //表描述不一致抛出异常
            BusinessResponseEnum.TABLEDESCRIPTIONINFORMATIONERROR.assertIsTrue(false, detail.getDescribe(), answerDetail.getDescribe());
          }
        } else {
          //表描述不一致抛出异常
          BusinessResponseEnum.TABLEDESCRIPTIONINFORMATIONERROR.assertIsTrue(detail.getDescribe().equalsIgnoreCase(answerDetail.getDescribe())
            , detail.getDescribe(), answerDetail.getDescribe());
        }

      }
      //删除
      if (CheckStatus.DEL.toString().equals(detail.getCheckStatus())) {
        //答案中没有删除相关表信息抛出异常
        BusinessResponseEnum.TABLENOTDELETED.assertIsEmpty(details, detail.getTableName());
      }
    }

    return true;
  }


}
