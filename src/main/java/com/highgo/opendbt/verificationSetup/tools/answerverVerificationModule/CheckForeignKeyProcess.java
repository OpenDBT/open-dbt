package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 外键校验模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkForeignKeyProcess")
@CheckEventAnnotation(value = TableInfoEvent.FOREIGN_KEY)
public class CheckForeignKeyProcess implements CheckProcess<TCheckFk, TCheckFk> {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;

  /**
   * @description: 比较索引
   * @author:
   * @date: 2023/3/23 16:08
   * @param: [checkFKS 校验点外键信息, fks 答案外键信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckFk> checkFKS, List<TCheckFk> fks) {
    if (checkFKS.isEmpty() || checkFKS.size() == 0) {
      return true;
    }
    for (TCheckFk fk : checkFKS) {
      if (CheckStatus.INSERT.toString().equals(fk.getCheckStatus()) || CheckStatus.UPDATE.toString().equals(fk.getCheckStatus())) {
        List<TCheckFk> fkList = fks.stream()
          .filter(item -> item.getFkName().equalsIgnoreCase(fk.getFkName()))
          .collect(Collectors.toList());
        //校验外键不存在抛出异常
        BusinessResponseEnum.FKNOTEXIST.assertIsNotEmpty(fkList, fk.getFkName());
        //答案中的对应字段
        TCheckFk tCheckFK = fkList.get(0);
        //比对答案中的外键信息
        fkComparison(fk, tCheckFK);
      }
      //删除
      if (CheckStatus.DEL.toString().equals(fk.getCheckStatus())) {
        //校验表中外键是否删除
        List<TCheckFk> fkList = fks.stream()
          .filter(item -> item.getFkName().equalsIgnoreCase(fk.getFkName()))
          .collect(Collectors.toList());
        //校验字段不存在抛出异常
        BusinessResponseEnum.FKNOTDELETED.assertIsEmpty(fkList, fk.getFkName());
      }
    }

    return true;
  }

  /**
   * @param fk      校验外键
   * @param checkFk 答案外键
   * @description:
   * @author:
   * @date: 2023/3/23 15:08
   * @return: void
   **/
  private void fkComparison(TCheckFk fk, TCheckFk checkFk) {
    //更新约束字段
    replaceRenameFields(fk);
    //外键字段
    BusinessResponseEnum.FKFIELDSAREDIFFERENT.assertIsEqualsArrayString(fk.getFkFields(), checkFk.getFkFields(), fk.getFkName(), fk.getFkFields(), checkFk.getFkFields());
    //参照表
    BusinessResponseEnum.REFERENCEAREDIFFERENT.assertIsEquals(fk.getReference(), checkFk.getReference(), fk.getFkName(), fk.getReference(), checkFk.getReference());
    //更新约束字段
    replaceRenameReferenceFields(fk);
    //参照表字段
    BusinessResponseEnum.REFERENCEFIELDSDIFFERENT.assertIsEqualsArrayString(fk.getReferenceFields(), checkFk.getReferenceFields(), fk.getFkName(), fk.getReferenceFields(), checkFk.getReferenceFields());
    //更新规则
    BusinessResponseEnum.UPDATERULEDIFFERENT.assertIsEquals(fk.getUpdateRule(), checkFk.getUpdateRule(), fk.getFkName(), fk.getUpdateRule(), checkFk.getUpdateRule());
    //删除规则
    BusinessResponseEnum.DELETERULEDIFFERENT.assertIsEquals(fk.getDeleteRule(), checkFk.getDeleteRule(), fk.getFkName(), fk.getDeleteRule(), checkFk.getDeleteRule());
  }

  //若先添加约束校验点，约束字段名称后来被修改,则校验点保存的约束字段和修改后的不一致，导致校验时误报约束字段不一致错误
  private void replaceRenameReferenceFields(TCheckFk fk) {
    //查询字段有没有被重命名
    String[] fields = fk.getReferenceFields().split(",");
    Long sceneDetailId = fk.getSceneDetailId();
    if (sceneDetailId != null) {
      //包含使用字段的sceneField
      List<TSceneField> sceneFields = sceneFieldService.list(new QueryWrapper<TSceneField>()
        .eq("scene_detail_id", sceneDetailId)).stream()
        .filter(item -> Arrays.asList(fields).contains(item.getFieldName())).collect(Collectors.toList());
      if (sceneFields.isEmpty()) {
        return;
      }
      List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("scene_detail_id", sceneDetailId)
        .eq("exercise_id", fk.getExerciseId())
        .eq("check_status", "UPDATE").in("scene_field_id", sceneFields.stream().map(TSceneField::getId).collect(Collectors.toList())));
      if (checkFields.isEmpty()) {
        return;
      }
      for (TCheckField field : checkFields) {
        List<TSceneField> sceneFieldList = sceneFields.stream().filter(item -> item.getId().equals(field.getSceneFieldId())).collect(Collectors.toList());
        if (!field.getFieldName().equals(sceneFieldList.get(0).getFieldName())) {
          fk.setReferenceFields(fk.getReferenceFields().replace(sceneFieldList.get(0).getFieldName(), field.getFieldName()));
        }
      }

    }
  }

  //若先添加约束校验点，约束字段名称后来被修改,则校验点保存的约束字段和修改后的不一致，导致校验时误报约束字段不一致错误
  private void replaceRenameFields(TCheckFk fk) {
    //查询字段有没有被重命名
    String[] fields = fk.getFkFields().split(",");
    Long sceneDetailId = fk.getSceneDetailId();
    if (sceneDetailId != null) {
      //包含使用字段的sceneField
      List<TSceneField> sceneFields = sceneFieldService.list(new QueryWrapper<TSceneField>()
        .eq("scene_detail_id", sceneDetailId)).stream()
        .filter(item -> Arrays.asList(fields).contains(item.getFieldName())).collect(Collectors.toList());
      if (sceneFields.isEmpty()) {
        return;
      }
      List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("scene_detail_id", sceneDetailId)
        .eq("exercise_id", fk.getExerciseId())
        .eq("check_status", "UPDATE").in("scene_field_id", sceneFields.stream().map(TSceneField::getId).collect(Collectors.toList())));
      if (checkFields.isEmpty()) {
        return;
      }
      for (TCheckField field : checkFields) {
        List<TSceneField> sceneFieldList = sceneFields.stream().filter(item -> item.getId().equals(field.getSceneFieldId())).collect(Collectors.toList());
        if (!field.getFieldName().equals(sceneFieldList.get(0).getFieldName())) {
          fk.setFkFields(fk.getFkFields().replace(sceneFieldList.get(0).getFieldName(), field.getFieldName()));
        }
      }

    }
  }

}
