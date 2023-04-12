package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.ConstraintType;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 约束校验模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkConstraintProcess")
@CheckEventAnnotation(value = TableInfoEvent.CONSTRAINT)
public class CheckConstraintProcess implements CheckProcess<TCheckConstraint, TCheckConstraint> {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;

  /**
   * @description:
   * @author:
   * @date: 2023/3/23 16:07
   * @param: [checkConstraints 校验点约束信息, constraints 答案约束信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckConstraint> checkConstraints, List<TCheckConstraint> constraints) {
    if (checkConstraints.isEmpty() || checkConstraints.size() == 0) {
      return true;
    }
    for (TCheckConstraint constraint : checkConstraints) {
      if (CheckStatus.INSERT.toString().equals(constraint.getCheckStatus()) || CheckStatus.UPDATE.toString().equals(constraint.getCheckStatus())) {
        List<TCheckConstraint> constraintList = constraints.stream()
          .filter(item -> item.getCrName().equalsIgnoreCase(constraint.getCrName()))
          .collect(Collectors.toList());
        //校验约束不存在抛出异常
        BusinessResponseEnum.CONSTRAINTDOESNOTEXIST.assertIsNotEmpty(constraintList, constraint.getCrName());
        //答案中的对应字段
        TCheckConstraint tCheckConstraint = constraintList.get(0);
        //比对答案中的字段
        constraintComparison(constraint, tCheckConstraint);
      }
      //删除
      if (CheckStatus.DEL.toString().equals(constraint.getCheckStatus())) {
        //校验表中字段是否删除
        List<TCheckConstraint> constraintList = constraints.stream()
          .filter(item -> item.getCrName().equalsIgnoreCase(constraint.getCrName()))
          .collect(Collectors.toList());
        //校验字段不存在抛出异常
        BusinessResponseEnum.CONSTRAINTNOTDELETED.assertIsEmpty(constraintList, constraint.getCrName());
      }
    }

    return true;
  }

  /**
   * @param constraint
   * @param checkConstraint
   * @description:
   * @author:
   * @date: 2023/3/23 15:08
   * @param: [constraint 校验约束, checkConstraint 答案约束]
   * @return: void
   */
  private void constraintComparison(TCheckConstraint constraint, TCheckConstraint checkConstraint) {
    //更新约束字段
    replaceRenameFields(constraint);
    //约束字段
    BusinessResponseEnum.CONSTRAINTFIELDSAREDIFFERENT.assertIsEqualsArrayString(constraint.getCrFields(), checkConstraint.getCrFields(), constraint.getCrName(), constraint.getCrFields(), checkConstraint.getCrFields());
    //约束类型
    BusinessResponseEnum.CONSTRAINTTYPESAREDIFFERENT.assertIsEquals(constraint.getCrType(), checkConstraint.getCrType(), constraint.getCrName(), constraint.getCrType(), checkConstraint.getCrType());
    if ((ConstraintType.C.toString()).equals(constraint.getCrType()) || (ConstraintType.X.toString()).equals(constraint.getCrType())) {
      //表达式
      BusinessResponseEnum.EXPRESSIONISDIFFERENT.assertIsEquals(constraint.getCrExpression(), checkConstraint.getCrExpression(), constraint.getCrName(), constraint.getCrExpression(), checkConstraint.getCrExpression());
    }
    if ((ConstraintType.X.toString()).equals(constraint.getCrType())) {
      //排他约束索引类型
      BusinessResponseEnum.EXCLUSIVECONSTRAINTINDEXTYPESAREDIFFERENT.assertIsEquals(constraint.getCrIndexType(), checkConstraint.getCrIndexType(), constraint.getCrName(), constraint.getCrIndexType(), checkConstraint.getCrIndexType());
    }
  }

  //若先添加约束校验点，约束字段名称后来被修改,则校验点保存的约束字段和修改后的不一致，导致校验时误报约束字段不一致错误
  private void replaceRenameFields(TCheckConstraint constraint) {
    //查询字段有没有被重命名
    String[] fields = constraint.getCrFields().split(",");
    Long sceneDetailId = constraint.getSceneDetailId();
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
        .eq("exercise_id", constraint.getExerciseId())
        .eq("check_status", "UPDATE").in("scene_field_id", sceneFields.stream().map(TSceneField::getId).collect(Collectors.toList())));
      if (checkFields.isEmpty()) {
        return;
      }
      for (TCheckField field : checkFields) {
        List<TSceneField> sceneFieldList = sceneFields.stream().filter(item -> item.getId().equals(field.getSceneFieldId())).collect(Collectors.toList());
        if (!field.getFieldName().equals(sceneFieldList.get(0).getFieldName())) {
          constraint.setCrFields(constraint.getCrFields().replace(sceneFieldList.get(0).getFieldName(), field.getFieldName()));
        }
      }

    }
  }


}
