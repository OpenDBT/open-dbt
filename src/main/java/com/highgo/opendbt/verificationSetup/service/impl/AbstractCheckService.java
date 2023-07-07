package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.temp.domain.entity.*;
import com.highgo.opendbt.temp.service.*;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 模板模式抽象类
 * @Title: AbstractCheckService
 * @Package com.highgo.opendbt.verificationSetup.service.impl
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/16 14:13
 */
@Service
public abstract class AbstractCheckService<M extends BaseMapper<T>, T extends Object> extends ServiceImpl<M, T> {
  private static final Logger logger = LoggerFactory.getLogger(AbstractCheckService.class);


  @Autowired
  private TCheckDetailService checkDetailService;

  @Autowired
  private TCheckFieldService checkFieldService;
  @Autowired
  private TCheckFkService checkFkService;
  @Autowired
  private TCheckConstraintService checkConstraintService;
  @Autowired
  private TCheckSeqService checkSeqService;
  @Autowired
  private TCheckIndexService checkIndexService;

  @Autowired
  private TCheckFieldTempService checkFieldTempService;
  @Autowired
  private TCheckDetailTempService checkDetailTempService;
  @Autowired
  private TCheckFkTempService checkFkTempService;
  @Autowired
  private TCheckConstraintTempService checkConstraintTempService;
  @Autowired
  private TCheckSeqTempService checkSeqTempService;
  @Autowired
  private TCheckIndexTempService checkIndexTempService;


  public StringBuilder addOtherSql(StringBuilder sql, Long exerciseId, String tableName, boolean isSave, Long sceneDetailId, String addType, Long id) {
    StringBuilder answerSql = new StringBuilder();
    //正式表
    if (isSave) {
      //新增表sql
      insertTableSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //新增字段sql
      insertFieldSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加外键的校验sql
      fkSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加约束的校验sql
      constraintsSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加序列的校验sql
      seqSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加索引的校验sql
      indexSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      if (!addType.equals("FIELD")) {
        //本次sql
        answerSql.append(sql);
      }
      //编辑字段sql
      answerSql = editFiedlSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      if (addType.equals("FIELD")) {
        //本次sql
        answerSql.append(sql);
      }
      //编辑表sql
      answerSql = editTableSql(exerciseId, tableName, sceneDetailId, id, answerSql);
    } else {//临时表
      //临时表新增表sql
      tempInsertTableSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //临时表新增字段sql
      tempInsertFieldSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加外键的校验sql
      tempFkSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加约束的校验sql
      tempConstraintSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加序列的校验sql
      tempSeqSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      //增加索引的校验sql
      tempIndexSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      if (!addType.equals("FIELD")) {
        //本次sql
        answerSql.append(sql);
      }
      //临时表字段编辑sql
      tempEditFieldSql(exerciseId, tableName, sceneDetailId, id, answerSql);
      if (addType.equals("FIELD")) {
        //本次sql
        answerSql.append(sql);
      }
      //临时表编辑sql
      tempEditTableSql(exerciseId, tableName, sceneDetailId, id, answerSql);
    }

    return answerSql;
  }

  private void tempEditTableSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckDetailTemp> temps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .ne("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (temps != null && temps.size() > 0 && (id == null || !id.equals(temps.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(CopyUtils.copyListProperties(temps, TCheckDetail.class));
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempEditFieldSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckFieldTemp> fields = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .ne("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (fields != null && fields.size() > 0 && (id == null || !id.equals(fields.get(0).getId()))) {
      StringBuilder field = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(CopyUtils.copyListProperties(fields, TCheckField.class));
      answerSql.append(field); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempIndexSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckIndexTemp> indices = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (indices != null && indices.size() > 0 && (id == null || !id.equals(indices.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(CopyUtils.copyListProperties(indices, TCheckIndex.class));
      answerSql.append(temp);// 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempSeqSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckSeqTemp> seqs = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (seqs != null && seqs.size() > 0 && (id == null || !id.equals(seqs.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(CopyUtils.copyListProperties(seqs, TCheckSeq.class));
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempConstraintSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckConstraintTemp> constraints = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (constraints != null && constraints.size() > 0 && (id == null || !id.equals(constraints.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(CopyUtils.copyListProperties(constraints, TCheckConstraint.class));
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempFkSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckFkTemp> fks = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (fks != null && fks.size() > 0 && (id == null || !id.equals(fks.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(CopyUtils.copyListProperties(fks, TCheckFk.class));
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempInsertFieldSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckFieldTemp> otherFields = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (otherFields != null && otherFields.size() > 0 && (id == null || !id.equals(otherFields.get(0).getId()))) {
      StringBuilder field = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(CopyUtils.copyListProperties(otherFields, TCheckField.class));
      answerSql.append(field); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void tempInsertTableSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckDetailTemp> otherTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (otherTemps != null && otherTemps.size() > 0 && (id == null || !id.equals(otherTemps.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(CopyUtils.copyListProperties(otherTemps, TCheckDetail.class));
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private StringBuilder editTableSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckDetail> temps = checkDetailService.list(new QueryWrapper<TCheckDetail>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .ne("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (temps != null && temps.size() > 0 && (id == null || !id.equals(temps.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(temps);
      temp.append(answerSql); // 将原来的内容追加到临时变量的末尾
      answerSql = temp; // 将临时变量赋值给answerSql
    }
    return answerSql;
  }

  private StringBuilder editFiedlSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckField> fields = checkFieldService.list(new QueryWrapper<TCheckField>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .ne("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (fields != null && fields.size() > 0 && (id == null || !id.equals(fields.get(0).getId()))) {
      StringBuilder field = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(fields);
      field.append(answerSql); // 将原来的内容追加到临时变量的末尾
      answerSql = field; // 将临时变量赋值给answerSql
    }
    return answerSql;
  }

  private void indexSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckIndex> indices = checkIndexService.list(new QueryWrapper<TCheckIndex>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (indices != null && indices.size() > 0 && (id == null || !id.equals(indices.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices);
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void seqSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckSeq> seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (seqs != null && seqs.size() > 0 && (id == null || !id.equals(seqs.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(seqs);
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void constraintsSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckConstraint> constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (constraints != null && constraints.size() > 0 && (id == null || !id.equals(constraints.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints);
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void fkSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckFk> fks = checkFkService.list(new QueryWrapper<TCheckFk>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    if (fks != null && fks.size() > 0 && (id == null || !id.equals(fks.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(fks);
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void insertFieldSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckField> otherfields = checkFieldService.list(new QueryWrapper<TCheckField>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (otherfields != null && otherfields.size() > 0 && (id == null || !id.equals(otherfields.get(0).getId()))) {
      StringBuilder field = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(otherfields);
      answerSql.append(field); // 将原来的内容追加到临时变量的末尾
    }
  }

  private void insertTableSql(Long exerciseId, String tableName, Long sceneDetailId, Long id, StringBuilder answerSql) {
    List<TCheckDetail> otherTemps = checkDetailService.list(new QueryWrapper<TCheckDetail>()
      .eq("exercise_id", exerciseId)
      .eq(sceneDetailId == null || sceneDetailId == -1, "table_name", tableName)
      .eq("check_status", "INSERT")
      .eq(sceneDetailId != null && sceneDetailId != -1, "scene_detail_id", sceneDetailId));
    //判定新增表增加字段，将建表语句增加到校验中
    if (otherTemps != null && otherTemps.size() > 0 && (id == null || !id.equals(otherTemps.get(0).getId()))) {
      StringBuilder temp = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(otherTemps);
      answerSql.append(temp); // 将原来的内容追加到临时变量的末尾
    }
  }


}
