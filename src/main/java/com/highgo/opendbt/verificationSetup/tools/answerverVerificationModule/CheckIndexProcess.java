package com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
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
 * @Description: 索引校验模块
 * @Title: GeneratCheckSql
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/21 18:12
 */
@Component("checkIndexProcess")
@CheckEventAnnotation(value = TableInfoEvent.INDEX)
public class CheckIndexProcess implements CheckProcess<TCheckIndex, TCheckIndex> {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;

  /**
   * @description: 比较索引
   * @author:
   * @date: 2023/3/23 16:08
   * @param: [checkIndexs 校验点索引信息, indexs 答案索引信息]
   * @return: boolean
   **/
  public boolean verify(List<TCheckIndex> checkIndexs, List<TCheckIndex> indexs) {
    if (checkIndexs.isEmpty() || checkIndexs.size() == 0) {
      return true;
    }
    for (TCheckIndex index : checkIndexs) {
      if (CheckStatus.INSERT.toString().equals(index.getCheckStatus()) || CheckStatus.UPDATE.toString().equals(index.getCheckStatus())) {
        List<TCheckIndex> indexList = indexs.stream()
          .filter(item -> item.getIndexName().equalsIgnoreCase(index.getIndexName()))
          .collect(Collectors.toList());
        //校验索引不存在抛出异常
        BusinessResponseEnum.INDEXDOESNOTEXIST.assertIsNotEmpty(indexList, index.getIndexName());
        //答案中的对应字段
        TCheckIndex tCheckIndex = indexList.get(0);
        //比对答案中的字段
        indexComparison(index, tCheckIndex);
      }
      //删除
      if (CheckStatus.DEL.toString().equals(index.getCheckStatus())) {
        //校验表中字段是否删除
        List<TCheckIndex> indexList = indexs.stream()
          .filter(item -> item.getIndexName().equalsIgnoreCase(index.getIndexName()))
          .collect(Collectors.toList());
        //校验字段不存在抛出异常
        BusinessResponseEnum.INDEXNOTDELETED.assertIsEmpty(indexList, index.getIndexName());
      }
    }

    return true;
  }

  /**
   * @description:
   * @author:
   * @date: 2023/3/23 15:08
   * @param: [index 校验索引, tCheckIndex 答案索引]
   * @return: void
   **/
  private void indexComparison(TCheckIndex index, TCheckIndex tCheckIndex) {
    //替换修改的字段
    replaceRenameFields(index);
    //索引字段
    BusinessResponseEnum.INDEXFIELDSAREDIFFERENT.assertIsEqualsArrayString(index.getIndexFields(), tCheckIndex.getIndexFields(), index.getIndexName(), index.getIndexFields(), tCheckIndex.getIndexFields());

    //索引类型
    BusinessResponseEnum.INDEXTYPESAREDIFFERENT.assertIsEquals(index.getIndexType(), tCheckIndex.getIndexType(), index.getIndexName(), index.getIndexType(), tCheckIndex.getIndexType());
    //是否唯一索引
    BusinessResponseEnum.ISTHEUNIQUEINDEXDIFFERENT.assertIsEquals(index.getIndexUnique(), tCheckIndex.getIndexUnique(), index.getIndexName(), index.getIndexUnique(), tCheckIndex.getIndexUnique());
    //描述
    // BusinessResponseEnum.INDEXDESCRIPTIONISDIFFERENT.assertIsEquals(index.getDescription(), tCheckIndex.getDescription(), index.getIndexName(), index.getDescription(), tCheckIndex.getDescription());
  }

  //若先添加索引校验点，索引字段名称后来被修改,则校验点保存的索引字段和修改后的不一致，导致校验时误报索引字段不一致错误
  private void replaceRenameFields(TCheckIndex index) {
    //查询字段有没有被重命名
    String[] fields = index.getIndexFields().split(",");
    Long sceneDetailId = index.getSceneDetailId();
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
        .eq("exercise_id", index.getExerciseId())
        .eq("check_status", "UPDATE").in("scene_field_id", sceneFields.stream().map(TSceneField::getId).collect(Collectors.toList())));
      if (checkFields.isEmpty()) {
        return;
      }
      for (TCheckField field : checkFields) {
        List<TSceneField> sceneFieldList = sceneFields.stream().filter(item -> item.getId().equals(field.getSceneFieldId())).collect(Collectors.toList());
        if (!field.getFieldName().equals(sceneFieldList.get(0).getFieldName())) {
          index.setIndexFields(index.getIndexFields().replace(sceneFieldList.get(0).getFieldName(), field.getFieldName()));
        }
      }

    }
  }

}
