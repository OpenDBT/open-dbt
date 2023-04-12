package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 序列校验模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkSequenceProcess")
@CheckEventAnnotation(value = TableInfoEvent.SEQUENCE)
public class CheckSequenceProcess implements CheckProcess<TCheckSeq, TCheckSeq> {
  Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * @description: 比较序列
   * @author:
   * @date: 2023/3/23 16:08
   * @param: [checkSeqs 校验点序列信息, seqs 答案序列信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckSeq> checkSeqs, List<TCheckSeq> seqs) {
    if (checkSeqs.isEmpty() || checkSeqs.size() == 0) {
      return true;
    }
    for (TCheckSeq seq : checkSeqs) {
      if (CheckStatus.INSERT.toString().equals(seq.getCheckStatus()) || CheckStatus.UPDATE.toString().equals(seq.getCheckStatus())) {
        List<TCheckSeq> seqList = seqs.stream()
          .filter(item -> item.getSeqName().equalsIgnoreCase(seq.getSeqName()))
          .collect(Collectors.toList());
        //校验外键不存在抛出异常
        BusinessResponseEnum.SEQNOTEXIST.assertIsNotEmpty(seqList, seq.getSeqName());
        //答案中的对应字段
        TCheckSeq tCheckSeq = seqList.get(0);
        //比对答案中的外键信息
        seqComparison(seq, tCheckSeq);
      }
      //删除
      if (CheckStatus.DEL.toString().equals(seq.getCheckStatus())) {
        //校验表中序列是否删除
        List<TCheckSeq> seqList = seqs.stream()
          .filter(item -> item.getSeqName().equalsIgnoreCase(seq.getSeqName()))
          .collect(Collectors.toList());
        //序列存在抛出异常
        BusinessResponseEnum.SEQNOTDELETED.assertIsEmpty(seqList, seq.getSeqName());
      }
    }

    return true;
  }

  /**
   * @param seq      校验序列
   * @param checkSeq 答案序列
   * @description:
   * @author:
   * @date: 2023/3/23 15:08
   * @return: void
   **/
  private void seqComparison(TCheckSeq seq, TCheckSeq checkSeq) {
    //步长
    BusinessResponseEnum.STEPDIFFERENT.assertIsEquals(seq.getStep(), checkSeq.getStep(), seq.getSeqName(), seq.getStep(), checkSeq.getStep());
    //最小值
    BusinessResponseEnum.MINVALUEDIFFERENT.assertIsEquals(seq.getMinValue(), checkSeq.getMinValue(), seq.getSeqName(), seq.getMinValue(), checkSeq.getMinValue());
    //最大值
    BusinessResponseEnum.MAXVALUEDIFFERENT.assertIsEquals(seq.getMaxValue(), checkSeq.getMaxValue(), seq.getSeqName(), seq.getMaxValue(), checkSeq.getMaxValue());
    //最新值
    //BusinessResponseEnum.LATESTVALUEDIFFERENT.assertIsEquals(seq.getLatestValue(), checkSeq.getLatestValue(), seq.getSeqName(), seq.getLatestValue(), checkSeq.getLatestValue());
    //是否循环
    BusinessResponseEnum.CYCLEDIFFERENT.assertIsEquals(seq.getCycle(), checkSeq.getCycle(), seq.getSeqName(), seq.getCycle(), checkSeq.getCycle());
    //列拥有
    BusinessResponseEnum.FIELDDIFFERENT.assertIsEquals(seq.getField(), checkSeq.getField(), seq.getSeqName(), seq.getField(), checkSeq.getField());
    //描述
    BusinessResponseEnum.REMARKDIFFERENT.assertIsEquals(seq.getRemark(), checkSeq.getRemark(), seq.getSeqName(), seq.getRemark(), checkSeq.getRemark());
    //缓存
    BusinessResponseEnum.MAXVALUEDIFFERENT.assertIsEquals(seq.getCacheSize(), checkSeq.getCacheSize(), seq.getSeqName(), seq.getCacheSize(), checkSeq.getCacheSize());
    //开始值
    BusinessResponseEnum.MAXVALUEDIFFERENT.assertIsEquals(seq.getStartValue(), checkSeq.getStartValue(), seq.getSeqName(), seq.getStartValue(), checkSeq.getStartValue());

  }


}
