package com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneSeq;
import com.highgo.opendbt.verificationSetup.service.TSceneSeqService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 生成序列校验描述
 * @Title: GeneratorDescriptionSequenceProcess
 * @Package com.highgo.opendbt.tools.generatorDescription
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/24 10:46
 */
@Component("generatorDescriptionSequenceProcess")
@GeneratorDescriptionEventAnnotation(value = TableInfoEvent.SEQUENCE)
public class GeneratorDescriptionSequenceProcess implements GeneratorDescriptionProcess<TCheckSeq> {
  @Autowired
  TSceneSeqService sceneSeqService;

  @Override
  public StringBuilder generatorDescriptions(List<TCheckSeq> checkSeqs) {

    StringBuilder builder = new StringBuilder();
    //没有校验点不生产答案
    if (checkSeqs.isEmpty() || checkSeqs.size() == 0) {
      return null;
    }
    for (TCheckSeq seq : checkSeqs) {
      //新增
      if (CheckStatus.INSERT.toString().equals(seq.getCheckStatus())) {
        builder.append("创建序列");
        builder.append(seq.getSeqName());
        if (StringUtils.isNotBlank(seq.getTypeName())) {
          builder.append("指定序列的数据类型为");
          builder.append(seq.getTypeName());
        }
        if (seq.getStep() != null) {
          builder.append("步长设置为");
          builder.append(seq.getStep());
        }
        if (seq.getMinValue() != null) {
          builder.append("最小值设置为");
          builder.append(seq.getMinValue());
        }
        if (seq.getMaxValue() != null) {
          builder.append("最大值设置为");
          builder.append(seq.getMaxValue());
        }
        if (seq.getStartValue() != null) {
          builder.append("起始值为");
          builder.append(seq.getStartValue());
        }
        if (seq.getCacheSize() != null) {
          builder.append("缓冲尺寸为");
          builder.append(seq.getCacheSize());
        }
        if (seq.getCycle()) {
          builder.append("设置为循环");
        } else {
          builder.append("设置为非循环");
        }
        if (StringUtils.isNotBlank(seq.getField())) {
          builder.append("序列与表");
          builder.append(seq.getTableName());
          builder.append("中字段");
          builder.append(seq.getField());
          builder.append("关联，以便删除表时一块删除");
        }
      }
      //修改
      if (CheckStatus.UPDATE.toString().equals(seq.getCheckStatus())) {
        TSceneSeq sceneSeq = sceneSeqService.getById(seq.getSceneSeqId());
        builder.append("修改序列");
        builder.append(seq.getSeqName());
        if (!Objects.equals(sceneSeq.getSeqName(), seq.getSeqName())) {
          builder.append(" 名称修改为");
          builder.append(seq.getSeqName());
        }
        if (!Objects.equals(sceneSeq.getTypeName(), seq.getTypeName())) {
          builder.append(" 指定序列的数据类型为");
          builder.append(seq.getTypeName());
        }
        if (!Objects.equals(sceneSeq.getStep(), seq.getStep())) {
          builder.append(" 步长由");
          builder.append(sceneSeq.getStep());
          builder.append("修改为");
          builder.append(seq.getStep());
        }
        if (!Objects.equals(sceneSeq.getMinValue(), seq.getMinValue())) {
          builder.append(" 最小值由");
          builder.append(sceneSeq.getMinValue());
          builder.append("修改为");
          builder.append(seq.getMinValue());
        }
        if (!Objects.equals(sceneSeq.getMaxValue(), seq.getMaxValue())) {
          builder.append(" 最大值由");
          builder.append(sceneSeq.getMaxValue());
          builder.append("修改为");
          builder.append(seq.getMaxValue());
        }
        if (!Objects.equals(sceneSeq.getStartValue(), seq.getStartValue())) {
          builder.append(" 起始值由");
          builder.append(sceneSeq.getStartValue());
          builder.append("修改为");
          builder.append(seq.getStartValue());
        }
        if (!Objects.equals(sceneSeq.getCacheSize(), seq.getCacheSize())) {
          builder.append(" 缓冲尺寸由");
          builder.append(sceneSeq.getCacheSize());
          builder.append("修改为");
          builder.append(seq.getCacheSize());
        }
        if (!Objects.equals(sceneSeq.getCycle(), seq.getCycle())) {
          builder.append(" 是否循环由");
          builder.append(sceneSeq.getCycle());
          builder.append("修改为");
          builder.append(seq.getCycle());
        }
        if (StringUtils.isNotBlank(seq.getField())) {
          builder.append(" 序列与表");
          builder.append(seq.getTableName());
          builder.append("中字段");
          builder.append(seq.getField());
          builder.append("关联，以便删除表时一块删除");
        }
        //序列可以切换表关联，目前只在表中切换字段
        if (!Objects.equals(sceneSeq.getField(), seq.getField())) {
          builder.append(" 序列与表字段的关联由表");
          builder.append(seq.getTableName());
          builder.append("中字段");
          builder.append(sceneSeq.getField());
          builder.append("修改为");
          builder.append("表");
          builder.append(seq.getTableName());
          builder.append("中字段");
          builder.append(seq.getField());
        }
      }
      //删除
      if (CheckStatus.DEL.toString().equals(seq.getCheckStatus())) {
        builder.append(" 删除序列");
        builder.append(seq.getSeqName());
      }
    }
    builder.append(",");
    return builder;
  }
}
