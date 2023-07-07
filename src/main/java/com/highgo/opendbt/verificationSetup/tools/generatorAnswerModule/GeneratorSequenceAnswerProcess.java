package com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule;

import com.highgo.opendbt.common.utils.WrapUtil;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneSeq;
import com.highgo.opendbt.verificationSetup.service.TSceneSeqService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 生成序列校验答案
 * @Title: GeneratorTableProcess
 * @Package com.highgo.opendbt.tools.answerGenerator
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorSequenceAnswerProcess")
@GeneratorAnswerEventAnnotation(value = TableInfoEvent.SEQUENCE)
public class GeneratorSequenceAnswerProcess implements GeneratorAnswerProcess<TCheckSeq> {
  @Autowired
  TSceneSeqService sceneSeqService;

  @Override
  public StringBuilder generatorAnswer(List<TCheckSeq> checkSeqs) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkSeqs.isEmpty() || checkSeqs.size() == 0) {
      return null;
    }
    for (TCheckSeq seq : checkSeqs) {
      //新增
      if (CheckStatus.INSERT.toString().equals(seq.getCheckStatus())) {
        builder.append(" CREATE SEQUENCE ");
        builder.append(seq.getSeqName());
        if (StringUtils.isNotBlank(seq.getTypeName())) {
          builder.append("  AS  ");
          builder.append(seq.getTypeName());
        }
        if (seq.getStep() != null) {
          builder.append("  INCREMENT BY   ");
          builder.append(seq.getStep());
        }
        if (seq.getMinValue() != null) {
          builder.append("  MINVALUE   ");
          builder.append(seq.getMinValue());
        } else {
          builder.append("  NO MINVALUE   ");
        }
        if (seq.getMaxValue() != null) {
          builder.append("  MAXVALUE    ");
          builder.append(seq.getMaxValue());
        } else {
          builder.append("  NO MAXVALUE    ");
        }
        if (seq.getStartValue() != null) {
          builder.append("  START WITH     ");
          builder.append(seq.getStartValue());
        }
        if (seq.getCacheSize() != null) {
          builder.append("  CACHE   ");
          builder.append(seq.getCacheSize());
        }
        if (seq.getCycle()) {
          builder.append("  CYCLE    ");
        } else {
          builder.append("  NO  CYCLE    ");
        }
        if (StringUtils.isNotBlank(seq.getField())) {
          builder.append(" OWNED BY     ");
          builder.append(seq.getTableName());
          builder.append(".");
          builder.append(seq.getField());
        }
        WrapUtil.addWrapper(builder);
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(seq.getCheckStatus())) {
        TSceneSeq sceneSeq = sceneSeqService.getById(seq.getSceneSeqId());
        //删除
        builder.append(" DROP SEQUENCE ");
        builder.append(sceneSeq.getSeqName());
        builder.append(" CASCADE;");
        //增加
        builder.append(" CREATE SEQUENCE ");
        builder.append(seq.getSeqName());
        if (StringUtils.isNotBlank(seq.getTypeName())) {
          builder.append("  AS  ");
          builder.append(seq.getTypeName());
        }
        if (seq.getStep() != null) {
          builder.append("  INCREMENT BY   ");
          builder.append(seq.getStep());
        }
        if (seq.getMinValue() != null) {
          builder.append("  MINVALUE   ");
          builder.append(seq.getMinValue());
        } else {
          builder.append("  NO MINVALUE   ");
        }
        if (seq.getMaxValue() != null) {
          builder.append("  MAXVALUE    ");
          builder.append(seq.getMaxValue());
        } else {
          builder.append("  NO MAXVALUE    ");
        }
        if (seq.getStartValue() != null) {
          builder.append("  START WITH     ");
          builder.append(seq.getStartValue());
        }
        if (seq.getCacheSize() != null) {
          builder.append("  CACHE   ");
          builder.append(seq.getCacheSize());
        }
        if (seq.getCycle()) {
          builder.append("  CYCLE    ");
        } else {
          builder.append("  NO  CYCLE    ");
        }
        if (StringUtils.isNotBlank(seq.getField())) {
          builder.append(" OWNED BY ");
          builder.append(seq.getTableName());
          builder.append(".");
          builder.append(seq.getField());
        }
        WrapUtil.addWrapper(builder);
        if (StringUtils.isNotBlank(seq.getField())) {
          builder.append("ALTER TABLE ");
          builder.append(seq.getTableName());
          builder.append(" ALTER COLUMN ");
          builder.append(seq.getField());
          builder.append(" SET DEFAULT ");
          builder.append(" nextval");
          builder.append("('");
          builder.append(seq.getSeqName());
          builder.append("'::regclass);");
        }
      }
      //删除
      if (CheckStatus.DEL.toString().equals(seq.getCheckStatus())) {

        builder.append(" DROP SEQUENCE ");
        builder.append(" \"");
        builder.append(seq.getSeqName());
        builder.append("\"");
        builder.append(" CASCADE");
        WrapUtil.addWrapper(builder);
      }
    }
    return builder;
  }
}
