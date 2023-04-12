package com.highgo.opendbt.verificationSetup.service.impl;

import cn.hutool.poi.word.TableUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.*;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.ResultSetMapper;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule.CheckEventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorCreateTableProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule.GeneratorDescriptionCreateTableProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorDescriptionModule.GeneratorDescriptionProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 校验点模块公共服务类
 * @Title: VerifyCommonServiceImpl
 * @Package com.highgo.opendbt.verificationSetup.service.impl
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/30 16:20
 */
@Service
public class VerifyCommonServiceImpl implements VerifyCommonService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckDetailService checkDetailService;
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TSceneIndexService sceneIndexService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  TSceneConstraintService sceneConstraintService;
  @Autowired
  TCheckConstraintService checkConstraintService;
  @Autowired
  TSceneFkService sceneForeignKeyService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  TSceneSeqService sceneSeqService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  GeneratorCreateTableProcess generatorCreateTableProcess;
  @Autowired
  private RunAnswerService runAnswerService;
  @Autowired
  private GeneratorDescriptionCreateTableProcess generatorDescriptionCreateTableProcess;
  @Autowired
  private TNewExerciseService exerciseService;

  /**
   * @description: 一键生成答案
   * @author:
   * @date: 2023/3/31 14:25
   * @param: [request, sceneId 场景id, exerciseId 习题id]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer
   **/
  @Override
  public StoreAnswer generatesAnswer(HttpServletRequest request, int sceneId, int exerciseId) {
    StoreAnswer storeAnswer = new StoreAnswer();
    //存储新增的答案
    Map<String, String> addAnswers = new HashMap<String, String>();
    //存储普通的答案
    Map<String, String> commonAnswers = new HashMap<String, String>();
    //根据场景id查询场景详情
    List<TSceneDetail> details = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", sceneId));
    //查询习题下场景详情表id为空的校验表
    List<TCheckDetail> addDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", exerciseId).isNull("scene_detail_id"));
    //不依赖场景新增表结构
    if (!addDetails.isEmpty()) {
      for (TCheckDetail checkDetail : addDetails) {
        StringBuilder builder = new StringBuilder();
        //查询该表结构下的字段
        List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>().eq("table_name", checkDetail.getTableName()).eq("exercise_id", exerciseId));
        //调用答案生成器生成答案
        builder.append(generatorCreateTableProcess.generatorAnswer(checkDetail, checkFields));
        //新增索引
        List<TCheckIndex> indices = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("table_name", checkDetail.getTableName()).eq("exercise_id", exerciseId));
        if (!indices.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices));
        }
        //新增约束
        List<TCheckConstraint> constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("table_name", checkDetail.getTableName()).eq("exercise_id", exerciseId));
        if (!constraints.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints));
        }
        //新增外键
        List<TCheckFk> fks = checkFkService.list(new QueryWrapper<TCheckFk>().eq("table_name", checkDetail.getTableName()).eq("exercise_id", exerciseId));
        if (!fks.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(fks));
        }
        //新增序列
        List<TCheckSeq> seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("table_name", checkDetail.getTableName()).eq("exercise_id", exerciseId));
        if (!seqs.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(seqs));
        }
        if (builder.length() > 0
          && !"null".equals(builder.toString())
          && !"".equals(builder.toString())) {
          addAnswers.put(checkDetail.getTableName(), builder.toString());
        }
      }
    }
    //依赖场景
    if (details != null && !details.isEmpty()) {
      for (TSceneDetail detail : details) {
        StringBuilder builder = new StringBuilder();
        //索引、约束等-》字段-》表  防止表和字段名称修改后 其他依赖表和字段的失效

        //新增索引
        List<TCheckIndex> indices = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("scene_detail_id", detail.getId()).eq("exercise_id", exerciseId));
        if (!indices.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices));
        }
        //新增约束
        List<TCheckConstraint> constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("scene_detail_id", detail.getId()).eq("exercise_id", exerciseId));
        if (!constraints.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints));
        }
        //新增外键
        List<TCheckFk> fks = checkFkService.list(new QueryWrapper<TCheckFk>().eq("scene_detail_id", detail.getId()).eq("exercise_id", exerciseId));
        if (!fks.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(fks));
        }
        //新增序列
        List<TCheckSeq> seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("scene_detail_id", detail.getId()).eq("exercise_id", exerciseId));
        if (!seqs.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(seqs));
        }
        //查询字段
        List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>().eq("scene_detail_id", detail.getId()).eq("exercise_id", exerciseId));
        if (!checkFields.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(checkFields));
        }
        //查询表
        List<TCheckDetail> tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", exerciseId).eq("scene_detail_id", detail.getId()));

        //生成答案
        if (!tCheckDetails.isEmpty()) {
          builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(tCheckDetails));
        }
        if (builder.length() > 0
          && !"null".equals(builder.toString())
          && !"".equals(builder.toString())) {
          commonAnswers.put(detail.getId().toString(), builder.toString());
        }

      }
    }
    storeAnswer.setAddAnswers(addAnswers);
    storeAnswer.setCommonAnswers(commonAnswers);
    return storeAnswer;
  }

  /**
   * @description: 测试运行
   * 表与表用###间隔，key与value用@@@间隔
   * @author:
   * @date: 2023/3/30 18:22
   * @param: [request, sceneId 场景id, exerciseId 习题id]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer
   **/
  @Override
  public boolean testRun(HttpServletRequest request, TestRunModel model) {
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");

    //DML类型题目
    if (model.getExerciseType() == 7) {
      veryDDLTypeExercise(model, userInfo);
    }
    //视图类型题目
    if (model.getExerciseType() == 8) {
      veryVIEWDDLTypeExercise(model, userInfo);
    }
    return true;
  }

  //视图类型题目
  private void veryVIEWDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    //解析答案获取key：视图名称和value:操作类型
    Map<String, String> answer = generatorViewNameAndType(model.getStandardAnswer());
    //循环map
    answer.entrySet().forEach(item -> {
      //视图名称
      String viewName = item.getKey();
      //视图类型
      String viewType = item.getValue();
      //删除类型
      if (CheckStatus.DEL.toString().equalsIgnoreCase(viewType)) {
        List<ViewModel> info = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), model.getStandardAnswer(), viewName, TableInfoEvent.TABLE_EXISTS, ViewModel.class);
        //删除失败提示
        BusinessResponseEnum.FAILDELETEVIEW.assertIsFalse(info.get(0).getExists(), viewName);
      }

      //新增和修改类型
      if (CheckStatus.INSERT.toString().equalsIgnoreCase(viewType) || CheckStatus.UPDATE.toString().equalsIgnoreCase(viewType)) {
        //根据习题id查询标准答案
        TNewExercise exercise = exerciseService.getById(model.getExerciseId());
        //执行学生答案的到视图结构
        List<TSceneField> studentFields = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), model.getStandardAnswer(), viewName, TableInfoEvent.FIELD, TSceneField.class);
        //执行正确答案得到视图结构
        List<TSceneField> teacherFields = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), exercise.getStandardAnswser(), viewName, TableInfoEvent.FIELD, TSceneField.class);
        //比较视图结构，不同抛出异常
        compareView(teacherFields, studentFields);
      }


    });
  }

  /**
   * @description: 比较标准答案和学生答案判断作答是否正确
   * @author:
   * @date: 2023/4/10 16:18
   * @param: [teacherFields 标准答案返回的视图结构, studentFields学生答案返回的视图结构]
   * @return: void
   **/
  private void compareView(List<TSceneField> teacherFields, List<TSceneField> studentFields) {
    //学生答案查询到的字段为空
    BusinessResponseEnum.NOTFOUNDVIEW.assertIsNotEmpty(studentFields);
    teacherFields.forEach(field -> {
      List<TSceneField> fieldList = studentFields.stream().filter(item -> item.getFieldName().equalsIgnoreCase(field.getFieldName())).collect(Collectors.toList());
      //校验字段不存在抛出异常
      BusinessResponseEnum.NOFIELDSFOUND.assertIsNotEmpty(fieldList, field.getFieldName());
      //答案中的对应字段
      TSceneField sceneField = fieldList.get(0);
      //比对答案中的字段
      fieldComparison(field, sceneField);
    });
  }

  private void fieldComparison(TSceneField field, TSceneField sceneField) {
    //判断字段类型是否相同
    BusinessResponseEnum.DIFFERENTFIELDTYPES.assertIsTrue(field.getFieldType().equalsIgnoreCase(sceneField.getFieldType())
      , field.getFieldName(), field.getFieldType(), sceneField.getFieldType());
    //判断字段长度是否相同
    if ("varchar".equalsIgnoreCase(field.getFieldType())) {
      BusinessResponseEnum.FIELDLENGTHISDIFFERENT.assertIsEquals(field.getFieldLength(), sceneField.getFieldLength(), field.getFieldName(), field.getFieldLength(), sceneField.getFieldLength());
    }
    //判断默认值是否相同
    BusinessResponseEnum.FIELDDEFAULTISDIFFERENT.assertIsEquals(field.getFieldDefault(), sceneField.getFieldDefault(), field.getFieldName(), field.getFieldDefault(), sceneField.getFieldDefault());
    //判断是否非空
    BusinessResponseEnum.WHETHERITISNOTEMPTYISDIFFERENT.assertIsEquals(field.getFieldNonNull(), sceneField.getFieldNonNull(), field.getFieldName(), field.getFieldNonNull(), sceneField.getFieldNonNull());
    //判断字段描述
    BusinessResponseEnum.FIELDDESCRIPTIONSDIFFER.assertIsEquals(field.getFieldComment(), sceneField.getFieldComment(), field.getFieldName(), field.getFieldComment(), sceneField.getFieldComment());
    //判断是否自增
    //BusinessResponseEnum.WHETHERSELFINCREASINGISDIFFERENT.assertIsEquals(field.getAutoIncrement(), tCheckField.getAutoIncrement(), field.getFieldName(), field.getAutoIncrement(), tCheckField.getAutoIncrement());
    //判断小数点位数
    if ("numeric".equalsIgnoreCase(sceneField.getFieldType())) {
      BusinessResponseEnum.DIFFERENTDECIMALPLACES.assertIsEquals(field.getDecimalNum(), sceneField.getDecimalNum(), field.getFieldName(), field.getDecimalNum(), sceneField.getDecimalNum());
    }
  }

  //解析答案获取视图名称和操作类型
  private Map<String, String> generatorViewNameAndType(String standardAnswer) {
    Map<String, String> map = new HashMap<>();
    String[] answers = standardAnswer.trim().split(";");
    for (String answer : answers) {
      String answerTrim=answer.toLowerCase().replaceAll(" ", "");
      //判断是否为视图语句
      boolean isView = answerTrim.startsWith("createview") || answerTrim.startsWith("alterview") || answerTrim.startsWith("dropview");
      BusinessResponseEnum.ISVIEW.assertIsTrue(isView);
      //新增的视图
      if (answerTrim.startsWith("createview")) {
        map.put(answerTrim.substring(answer.indexOf("createview") + 11, answerTrim.indexOf("asselect")), CheckStatus.INSERT.toString());
      }
      //修改的视图
      if (answerTrim.startsWith("alterview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("alterview") + 10, answerTrim.indexOf("asselect")), CheckStatus.UPDATE.toString());
      }
      //删除的视图
      if (answerTrim.startsWith("dropview")) {
        map.put(answerTrim.substring(answer.indexOf("dropview") + 9, answerTrim.indexOf(";")), CheckStatus.DEL.toString());
      }
    }
    return map;

  }

  //DML类型题目
  private void veryDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    {
      //解析答案
      StoreAnswer storeAnswer = gengratorAnswer(model.getStandardAnswer());
      //非新增表答案
      Map<String, String> commonAnswers = storeAnswer.getCommonAnswers();
      //新增表答案
      Map<String, String> addAnswers = storeAnswer.getAddAnswers();
      //根据场景id查询t_scene_detail
      List<TSceneDetail> details = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", model.getSceneId()));
      //依赖场景
      if (details != null && !details.isEmpty()) {
        for (TSceneDetail detail : details) {
          if (StringUtils.isNotBlank(commonAnswers.get(detail.getId().toString()))) {
            //初始化场景并开启新的模式,执行答案获取相关答案信息
            VerificationList answerVerify = extractAnswer(getTableName(detail, model) == null ? detail.getTableName() : getTableName(detail, model), userInfo, model.getSceneId(), model.getExerciseId(), new StringBuilder(commonAnswers.get(detail.getId().toString())));
            //查询相关校验点信息
            VerificationList checkVerify = getVerify(detail.getId(), model.getExerciseId());
            //校验模块进行校验
            checkAnswer(checkVerify, answerVerify);
          }

        }
      }
      //不依赖场景
      List<TCheckDetail> addDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", model.getExerciseId()).isNull("scene_detail_id"));
      //不依赖场景新增表结构
      if (!addDetails.isEmpty()) {
        for (TCheckDetail detail : addDetails) {
          //初始化场景并开启新的模式,执行答案获取相关答案信息
          VerificationList answerVerify = extractAnswer(detail.getTableName(), userInfo, model.getSceneId(), model.getExerciseId(), new StringBuilder(addAnswers.get(detail.getTableName())));
          //查询相关校验点信息
          VerificationList checkVerify = getVerify(detail.getId(), model.getExerciseId());
          //校验模块进行校验
          checkAnswer(checkVerify, answerVerify);
        }
      }

    }
  }

  /**
   * @description: 一键恢复（删除所有校验点）
   * @author:
   * @date: 2023/4/6 10:16
   * @param: [request, model 习题id，场景id，类型]
   * @return: boolean
   **/
  @Override
  public boolean recovery(HttpServletRequest request, RecoveryModel model) {
    //查询场景信息
    TSceneDetail sceneDetail = sceneDetailService.getById(model.getSceneDetailId());
    BusinessResponseEnum.NOTFOUNDTSCENEDETAIL.assertNotNull(sceneDetail);
    //清空表校验
    if (TableInfoEvent.TABLE.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeTableVerify(model);
    }
    //清空字段校验
    if (TableInfoEvent.FIELD.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeFieldVerify(model);
    }
    //清空索引校验
    if (TableInfoEvent.INDEX.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeIndexVerify(model);
    }
    //清空约束校验
    if (TableInfoEvent.CONSTRAINT.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeConstraintVerify(model);
    }
    //清空外键校验
    if (TableInfoEvent.FOREIGN_KEY.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeFkVerify(model);
    }
    //清空序列校验
    if (TableInfoEvent.SEQUENCE.toString().equalsIgnoreCase(model.getRecoverType())) {
      return removeSeqVerify(model);
    }
    //清空所有校验
    if ("ALL".equalsIgnoreCase(model.getRecoverType())) {
      return removeTableVerify(model)
        && removeFieldVerify(model)
        && removeIndexVerify(model)
        && removeConstraintVerify(model)
        && removeFkVerify(model)
        && removeSeqVerify(model);
    }
    return false;
  }

  /**
   * @description: 自动生成描述
   * @author:
   * @date: 2023/4/6 13:47
   * @param: [request, model 习题id，场景id]
   * @return: java.lang.String
   **/
  @Override
  public String generateDescriptions(HttpServletRequest request, GeneratorDescription model) {
    StringBuilder res = new StringBuilder();
    //习题中新增表生成的校验点描述
    newTableTODescription(model, res);
    //习题中依赖场景表生成的校验点描述
    historyTableTODescription(model, res);
    return res.toString();
  }

  //习题中依赖场景表生成的校验点描述
  private void historyTableTODescription(GeneratorDescription model, StringBuilder res) {
    //场景详情集合
    List<TSceneDetail> sceneDetails = sceneDetailService.list(new QueryWrapper<TSceneDetail>()
      .eq("scene_id", model.getSceneId()));
    for (TSceneDetail sceneDetail : sceneDetails) {
      //生成索引描述
      generatorIndexDescription(sceneDetail, model, res);
      //生成约束描述
      generatorConstraintDescription(sceneDetail, model, res);
      //生成外键描述
      generatorFkDescription(sceneDetail, model, res);
      //生成序列描述
      generatorSequenceDescription(sceneDetail, model, res);
      //生成字段描述
      generatorFieldDescription(sceneDetail, model, res);
      //生成表描述
      generatorTableDescription(sceneDetail, model, res);
      res.setLength(res.length() - 1);
      res.append("。");
    }
  }

  //生成表描述
  private void generatorTableDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckDetail> details = checkDetailService.list(new QueryWrapper<TCheckDetail>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!details.isEmpty()) {
      StringBuilder tableDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorDescriptions(details);
      if (tableDes != null) {
        res.append(tableDes);
      }
    }
  }

  //生成字段描述
  private void generatorFieldDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckField> fields = checkFieldService.list(new QueryWrapper<TCheckField>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!fields.isEmpty()) {
      StringBuilder fieldDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorDescriptions(fields);
      if (fieldDes != null) {
        res.append(fieldDes);
      }
    }
  }

  //生成序列描述
  private void generatorSequenceDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckSeq> seqList = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!seqList.isEmpty()) {
      StringBuilder seqDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorDescriptions(seqList);
      if (seqDes != null) {
        res.append(seqDes);
      }
    }
  }

  //生成外键描述
  private void generatorFkDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckFk> fkList = checkFkService.list(new QueryWrapper<TCheckFk>().eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!fkList.isEmpty()) {
      StringBuilder fkDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorDescriptions(fkList);
      if (fkDes != null) {
        res.append(fkDes);
      }
    }
  }

  //生成约束相关描述
  private void generatorConstraintDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckConstraint> constraintList = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!constraintList.isEmpty()) {
      StringBuilder constraintDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorDescriptions(constraintList);
      if (constraintDes != null) {
        res.append(constraintDes);
      }
    }
  }

  //生成索引相关描述
  private void generatorIndexDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckIndex> indexList = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", detail.getId()));
    if (!indexList.isEmpty()) {
      StringBuilder indexDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorDescriptions(indexList);
      if (indexDes != null) {
        res.append(indexDes);
      }
    }
  }

  //习题中新增表生成的相关校验点描述
  private void newTableTODescription(GeneratorDescription model, StringBuilder res) {
    //新增的表
    List<TCheckDetail> details = checkDetailService.list(new QueryWrapper<TCheckDetail>()
      .eq("exercise_id", model.getExerciseId())
      .isNull("scene_detail_id"));
    if (!details.isEmpty()) {
      for (TCheckDetail detail : details) {
        //新建表和新建字段描述
        createTableAndFieldsDescription(detail, model, res);
        //新增的索引
        createIndexDescription(detail, model, res);
        //新增的约束
        createConstraintDescription(detail, model, res);
        //新增的外键
        createFKDescription(detail, model, res);
        //新增的序列
        createSeqDescription(detail, model, res);
        res.setLength(res.length() - 1);
        res.append("。");
      }
    }
  }

  //新增的序列
  private void createSeqDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckSeq> seqList = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("exercise_id", model.getExerciseId())
      .eq("table_name", detail.getTableName()));
    if (!seqList.isEmpty()) {
      StringBuilder seqDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorDescriptions(seqList);
      if (seqDes != null) {
        res.append(seqDes);
      }
    }
  }

  //新增的外键
  private void createFKDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckFk> fkList = checkFkService.list(new QueryWrapper<TCheckFk>().eq("exercise_id", model.getExerciseId())
      .eq("table_name", detail.getTableName()));
    if (!fkList.isEmpty()) {
      StringBuilder fkDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorDescriptions(fkList);
      if (fkDes != null) {
        res.append(fkDes);
      }
    }
  }

  //新增的约束
  private void createConstraintDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckConstraint> constraintList = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("exercise_id", model.getExerciseId())
      .eq("table_name", detail.getTableName()));
    if (!constraintList.isEmpty()) {
      StringBuilder constraintDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorDescriptions(constraintList);
      if (constraintDes != null) {
        res.append(constraintDes);
      }
    }
  }

  //新增的索引
  private void createIndexDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res) {
    List<TCheckIndex> indexList = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("exercise_id", model.getExerciseId())
      .eq("table_name", detail.getTableName()));
    if (!indexList.isEmpty()) {
      StringBuilder indexDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorDescriptions(indexList);
      if (indexDes != null) {
        res.append(indexDes);
      }
    }

  }

  //新建表和新建字段描述
  private void createTableAndFieldsDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res) {
    //新增的表中字段
    List<TCheckField> fields = checkFieldService.list(new QueryWrapper<TCheckField>()
      .eq("exercise_id", model.getExerciseId())
      .eq("table_name", detail.getTableName()));
    //生成新增表的描述
    StringBuilder answer = generatorDescriptionCreateTableProcess.generatorAnswer(detail, fields);
    if (answer != null) {
      res.append(answer);
    }
  }

  //表可能被重命名，根据修改后表名查询表结构
  private String getTableName(TSceneDetail detail, TestRunModel model) {
    String tableName = null;
    //查询场景表校验表
    List<TCheckDetail> tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", model.getExerciseId()).eq("scene_detail_id", detail.getId()));
    for (TCheckDetail item : tCheckDetails) {
      if (!detail.getTableName().equalsIgnoreCase(item.getTableName())) {
        tableName = item.getTableName();
      }
    }
    return tableName;
  }

  //答案校验
  private void checkAnswer(VerificationList checkVerify, VerificationList answerVerify) {
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.TABLE).verify(checkVerify.getCheckDetails(), answerVerify.getCheckDetails());
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.FIELD).verify(checkVerify.getCheckFields(), answerVerify.getCheckFields());
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.INDEX).verify(checkVerify.getCheckIndexList(), answerVerify.getCheckIndexList());
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).verify(checkVerify.getCheckConstraints(), answerVerify.getCheckConstraints());
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).verify(checkVerify.getCheckFks(), answerVerify.getCheckFks());
    CheckEventProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).verify(checkVerify.getCheckSeqs(), answerVerify.getCheckSeqs());
  }

  //查询校验点
  private VerificationList getVerify(Long id, Integer exerciseId) {
    VerificationList verificationList = new VerificationList();
    //查询表
    List<TCheckDetail> tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", exerciseId).eq("scene_detail_id", id));
    verificationList.setCheckDetails(tCheckDetails);
    //查询字段
    List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>().eq("scene_detail_id", id).eq("exercise_id", exerciseId));
    verificationList.setCheckFields(checkFields);
    //新增索引
    List<TCheckIndex> indices = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("scene_detail_id", id).eq("exercise_id", exerciseId));
    verificationList.setCheckIndexList(indices);
    //新增约束
    List<TCheckConstraint> constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("scene_detail_id", id).eq("exercise_id", exerciseId));
    verificationList.setCheckConstraints(constraints);
    //新增外键
    List<TCheckFk> fks = checkFkService.list(new QueryWrapper<TCheckFk>().eq("scene_detail_id", id).eq("exercise_id", exerciseId));
    verificationList.setCheckFks(fks);
    //新增序列
    List<TCheckSeq> seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("scene_detail_id", id).eq("exercise_id", exerciseId));
    verificationList.setCheckSeqs(seqs);
    return verificationList;
  }

  //解析答案
  private StoreAnswer gengratorAnswer(String answer) {
    Map<String, String> commonAnswers = new HashMap<>();
    Map<String, String> addAnswers = new HashMap<>();
    //每个表的答案
    String[] tableAnswers = answer.split("###");
    for (String tableAnswer : tableAnswers) {
      String[] answers = tableAnswer.split("@@@");
      Boolean isNumber = answers[0].trim().matches("^[0-9]*$");
      if (isNumber) {
        commonAnswers.put(answers[0].trim(), answers[1]);
      } else {
        addAnswers.put(answers[0].trim(), answers[1]);
      }
    }
    StoreAnswer storeAnswer = new StoreAnswer();
    storeAnswer.setAddAnswers(addAnswers);
    storeAnswer.setCommonAnswers(commonAnswers);
    return storeAnswer;
  }

  //执行初始化场景，执行答案
  private VerificationList extractAnswer(String tableName, UserInfo userInfo, int sceneId,
                                         int exerciseId, StringBuilder builder) {
    Statement statement = null;
    Connection connection = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    try {
      //初始化场景并开启新的模式
      runAnswerService.getSchemaConnection(userInfo, sceneId, exerciseId, schemaConnection, 0);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        //新模式下执行答案
        statement.executeUpdate(builder.toString());
        //提取相关表信息
        return informationExtraction(statement, tableName, schemaConnection.getSchemaName());
      }
    } catch (Exception e) {
      logger.error("获取失败", e);
      throw new APIException(e.getMessage());
    } finally {
      CloseUtil.close(statement);
      CloseUtil.close(connection);
      //删除模式
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
    }
    return null;
  }

  //提取相关表信息
  private VerificationList informationExtraction(Statement statement, String tableName, String schemaName) throws
    SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    VerificationList verificationList = new VerificationList();
    verificationList.setCheckDetails(getInfo(statement, tableName, schemaName, TableInfoEvent.TABLE, TCheckDetail.class));
    verificationList.setCheckFields(getInfo(statement, tableName, schemaName, TableInfoEvent.FIELD, TCheckField.class));
    verificationList.setCheckIndexList(getInfo(statement, tableName, schemaName, TableInfoEvent.INDEX, TCheckIndex.class));
    verificationList.setCheckConstraints(getInfo(statement, tableName, schemaName, TableInfoEvent.CONSTRAINT, TCheckConstraint.class));
    verificationList.setCheckFks(getInfo(statement, tableName, schemaName, TableInfoEvent.FOREIGN_KEY, TCheckFk.class));
    verificationList.setCheckSeqs(getInfo(statement, tableName, schemaName, TableInfoEvent.SEQUENCE, TCheckSeq.class));
    return verificationList;
  }


  //获取信息通用工具
  private <
    T> List<T> getInfo(Statement statement, String tableName, String schemaName, TableInfoEvent event, Class<T> clazz) throws
    SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    //获取sql
    EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
    String executeSql = eventProcess.execute(schemaName, tableName);
    //得到结果
    ResultSet resultSetDetail = statement.executeQuery(executeSql);
    return new ResultSetMapper<T>().mapRersultSetToObject(resultSetDetail, clazz);
  }

  private boolean removeSeqVerify(RecoveryModel model) {
    return checkSeqService.remove(new QueryWrapper<TCheckSeq>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

  private boolean removeFkVerify(RecoveryModel model) {
    return checkFkService.remove(new QueryWrapper<TCheckFk>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

  private boolean removeConstraintVerify(RecoveryModel model) {
    return checkConstraintService.remove(new QueryWrapper<TCheckConstraint>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

  private boolean removeIndexVerify(RecoveryModel model) {
    return checkIndexService.remove(new QueryWrapper<TCheckIndex>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

  private boolean removeFieldVerify(RecoveryModel model) {
    return checkFieldService.remove(new QueryWrapper<TCheckField>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

  private boolean removeTableVerify(RecoveryModel model) {
    return checkDetailService.remove(new QueryWrapper<TCheckDetail>()
      .eq("exercise_id", model.getExerciseId())
      .eq("scene_detail_id", model.getSceneDetailId()));
  }

}
