package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.*;
import com.highgo.opendbt.temp.service.*;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.*;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.FunctionUtil;
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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.highgo.opendbt.common.utils.WrapUtil.addWrap;

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
  @Autowired
  private SqlSessionFactory sqlSessionFactory;
  @Autowired
  private TCheckFieldTempService checkFieldTempService;
  @Autowired
  private TCheckDetailTempService checkDetailTempService;
  @Autowired
  private TCheckConstraintTempService checkConstraintTempService;
  @Autowired
  private TCheckFkTempService checkFkTempService;
  @Autowired
  private TCheckIndexTempService checkIndexTempService;
  @Autowired
  private TCheckSeqTempService checkSeqTempService;

  /**
   * @description: 一键生成答案
   * @author:
   * @date: 2023/3/31 14:25
   * @param: [request, sceneId 场景id, exerciseId 习题id]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer
   **/
  @Override
  public String generatesAnswer(HttpServletRequest request, int sceneId, Long exerciseId) {
    //判断是否临时表
    boolean save = exerciseService.isSave(exerciseId);
    //存储新增的答案
    StringBuilder answers = new StringBuilder();
    //存储普通的答案
    List<TSceneDetail> details = null;
    //根据场景id查询场景详情
    if (sceneId != -1) {
      details = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", sceneId));
    }
    //新增的表
    List<TCheckDetail> addDetails = getInsertCheckDetails(exerciseId, save);
    //新增表的相关sql
    getInsertTableSql(exerciseId, answers, addDetails, save);
    //原始表的相关sql
    getEditTableSql(exerciseId, answers, details, save);
    return answers.toString();
  }

  private void getEditTableSql(Long exerciseId, StringBuilder commonAnswers, List<TSceneDetail> details, boolean save) {
    if (details != null && !details.isEmpty()) {
      for (TSceneDetail detail : details) {
        StringBuilder builder = new StringBuilder();
        //索引、约束等-》字段-》表  防止表和字段名称修改后 其他依赖表和字段的失效

        //查询新增字段
        addInsertFieldSql(exerciseId, save, detail, builder);
        //新增索引
        getIndexSql(exerciseId, save, detail, builder);
        //新增约束
        getConstraintSql(exerciseId, save, detail, builder);
        //新增外键
        getFkSql(exerciseId, save, detail, builder);
        //新增序列
        getSeqSql(exerciseId, save, detail, builder);
        //查询字段
        getEditFieldSql(exerciseId, save, detail, builder);
        //查询表
        getTableSql(exerciseId, save, detail, builder);
        if (builder.length() > 0
          && !"null".equals(builder.toString())
          && !"".equals(builder.toString())) {
          commonAnswers.append(builder);
        }

      }
    }
  }

  private void getTableSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckDetail> tCheckDetails = null;
    if (save) {
      tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", exerciseId)
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", exerciseId)
        .eq("scene_detail_id", detail.getId()));
      tCheckDetails = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }
    //生成答案
    if (tCheckDetails != null && !tCheckDetails.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(tCheckDetails));
    }
  }

  private void getEditFieldSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckField> checkEditFields = null;
    if (save) {
      checkEditFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("scene_detail_id", detail.getId())
        .ne("check_status", "INSERT")
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("scene_detail_id", detail.getId())
        .ne("check_status", "INSERT")
        .eq("exercise_id", exerciseId));
      checkEditFields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }
    if (checkEditFields != null && !checkEditFields.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(checkEditFields));
    }
  }

  private void addInsertFieldSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckField> checkFields = null;
    if (save) {
      checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("scene_detail_id", detail.getId())
        .eq("check_status", "INSERT")
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("scene_detail_id", detail.getId())
        .eq("check_status", "INSERT")
        .eq("exercise_id", exerciseId));
      checkFields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }

    if (checkFields != null && !checkFields.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(checkFields));
    }
  }

  private void getFkSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckFk> fks = null;
    if (save) {
      fks = checkFkService.list(new QueryWrapper<TCheckFk>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckFkTemp> fkTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
      fks = CopyUtils.copyListProperties(fkTemps, TCheckFk.class);
    }
    if (fks != null && !fks.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(fks));
    }
  }

  private void getSeqSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckSeq> seqs = null;
    if (save) {
      seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckSeqTemp> seqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
      seqs = CopyUtils.copyListProperties(seqTemps, TCheckSeq.class);
    }

    if (seqs != null && !seqs.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(seqs));
    }
  }

  private void getIndexSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckIndex> indices = null;
    if (save) {
      indices = checkIndexService.list(new QueryWrapper<TCheckIndex>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckIndexTemp> indexTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
      indices = CopyUtils.copyListProperties(indexTemps, TCheckIndex.class);
    }

    if (indices != null && !indices.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices));
    }
  }

  private void getConstraintSql(Long exerciseId, boolean save, TSceneDetail detail, StringBuilder builder) {
    List<TCheckConstraint> constraints = null;
    if (save) {
      constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>()
        .eq("scene_detail_id", detail.getId())
        .eq("exercise_id", exerciseId));
      constraints = CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }

    if (constraints != null && !constraints.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints));
    }
  }

  private void getInsertTableSql(Long exerciseId, StringBuilder addAnswers, List<TCheckDetail> addDetails, boolean save) {
    if (!addDetails.isEmpty()) {
      for (TCheckDetail checkDetail : addDetails) {
        StringBuilder builder = new StringBuilder();
        //查询该表结构下的字段
        addTableAndField(exerciseId, save, checkDetail, builder);
        //新增索引
        addIndex(exerciseId, save, checkDetail, builder);
        //新增约束
        addConstraint(exerciseId, save, checkDetail, builder);
        //新增外键
        addFk(exerciseId, save, checkDetail, builder);
        //新增序列
        addSeq(exerciseId, save, checkDetail, builder);
        if (builder.length() > 0
          && !"null".equals(builder.toString())
          && !"".equals(builder.toString())) {
          addAnswers.append(builder);
        }
      }
    }
  }

  private void addSeq(Long exerciseId, boolean save, TCheckDetail checkDetail, StringBuilder builder) {
    List<TCheckSeq> seqs = null;
    if (save) {
      seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckSeqTemp> seqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
      seqs = CopyUtils.copyListProperties(seqTemps, TCheckSeq.class);
    }

    if (seqs != null && !seqs.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(seqs));
    }
  }

  private void addFk(Long exerciseId, boolean save, TCheckDetail checkDetail, StringBuilder builder) {
    List<TCheckFk> fks = null;
    if (save) {
      fks = checkFkService.list(new QueryWrapper<TCheckFk>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckFkTemp> fkTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
      fks = CopyUtils.copyListProperties(fkTemps, TCheckFk.class);
    }
    if (fks != null && !fks.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(fks));
    }
  }

  private void addConstraint(Long exerciseId, boolean save, TCheckDetail checkDetail, StringBuilder builder) {
    List<TCheckConstraint> constraints = null;
    if (save) {
      constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
      constraints = CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }
    if (constraints != null && !constraints.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints));
    }
  }

  private void addIndex(Long exerciseId, boolean save, TCheckDetail checkDetail, StringBuilder builder) {
    List<TCheckIndex> indices = null;
    if (save) {
      indices = checkIndexService.list(new QueryWrapper<TCheckIndex>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckIndexTemp> indexTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
      indices = CopyUtils.copyListProperties(indexTemps, TCheckIndex.class);
    }

    if (indices != null && !indices.isEmpty()) {
      builder.append(GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices));
    }
  }

  private void addTableAndField(Long exerciseId, boolean save, TCheckDetail checkDetail, StringBuilder builder) {
    List<TCheckField> checkFields = null;
    if (save) {
      checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("table_name", checkDetail.getTableName())
        .eq("exercise_id", exerciseId));
      checkFields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }
    //调用答案生成器生成答案
    if (checkFields != null && checkFields.size() > 0) {
      builder.append(generatorCreateTableProcess.generatorAnswer(checkDetail, checkFields));
    }

  }

  private List<TCheckDetail> getInsertCheckDetails(Long exerciseId, boolean save) {
    List<TCheckDetail> addDetails = null;
    if (save) {
      //查询习题下场景详情表id为空的校验表
      addDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", exerciseId)
        .isNull("scene_detail_id"));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", exerciseId)
        .isNull("scene_detail_id"));
      addDetails = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }
    return addDetails;
  }

  /**
   * @description: 学生端测试运行
   * 表与表用###间隔，key与value用@@@间隔
   * @author:
   * @date: 2023/3/30 18:22
   * @param: [request, sceneId 场景id, exerciseId 习题id]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer
   **/
  @Override
  public Object testRun(HttpServletRequest request, TestRunModel model) {
    UserInfo userInfo = Authentication.getCurrentUser(request);
    //DDL类型题目
    if (model.getExerciseType() == 7) {
      veryDDLTypeExercise(model, userInfo);
    }
    //视图类型题目
    if (model.getExerciseType() == 8) {
      veryVIEWDDLTypeExercise(model, userInfo);
    }
    //函数类型题目
    if (model.getExerciseType() == 9) {
      veryFUNCTIONDDLTypeExercise(model, userInfo);
    }
    return true;
  }

  //函数类型题目
  private void veryFUNCTIONDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    ResponseModel teacherAnswerResultMap = new ResponseModel();
    ResponseModel studentAnswerResultMap = new ResponseModel();
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    model.setVerySql(exercise.getVerySql());
    //执行学生答案返回
    FunctionUtil.executeFunctionSql(userInfo, model, studentAnswerResultMap);
    model.setStandardAnswer(exercise.getStandardAnswser());
    //执行标准答案返回
    FunctionUtil.executeFunctionSql(userInfo, model, teacherAnswerResultMap);
    //比较返回结果
    compareFunction(teacherAnswerResultMap, studentAnswerResultMap);
  }

  //比较返回结果
  private void compareFunction(ResponseModel teacherAnswerResultMap, ResponseModel studentAnswerResultMap) {
    //判断是否是校验sql查询
    Boolean select = teacherAnswerResultMap.isSelect();
    if (select) {
      //是校验sql查询
      ResultSetInfo teacherResultSetInfo = teacherAnswerResultMap.getResultSetInfo();
      ResultSetInfo studentResultSetInfo = studentAnswerResultMap.getResultSetInfo();
      //比较教师和学生的结果集是否一样
      FunctionUtil.compareResultSet(teacherResultSetInfo, studentResultSetInfo);
    } else {
      List<List<Map<String, Object>>> teacherLists = null;//teacherAnswerResultMap.getFunctionResult();
      List<List<Map<String, Object>>> studentLists = null;//studentAnswerResultMap.getFunctionResult();
      //判断教师和学生得到的结果集个数是否相同
      BusinessResponseEnum.RESULTNUMDIFFENT.assertIsTrue(teacherLists.size() == studentLists.size(), teacherLists.size());
      for (int i = 0; i < teacherLists.size(); i++) {
        //比较两个结果
        compareResult(teacherLists.get(0), studentLists.get(0));
      }
    }
  }

  private void compareResult(List<Map<String, Object>> teachResult, List<Map<String, Object>> studentResult) {
    //结果中的个数不同抛出异常
    BusinessResponseEnum.RESULTNUMDIFFENT.assertIsTrue(teachResult.size() == studentResult.size(), teachResult.size());
    for (int i = 0; i < teachResult.size(); i++) {
      Map<String, Object> teacherMap = teachResult.get(i);
      Map<String, Object> studentMap = studentResult.get(i);
      //比较key是否相同
      BusinessResponseEnum.COLUMNNAMEDIFF.assertIsTrue(studentMap.keySet().equals(teacherMap.keySet()));

      for (Map.Entry<?, ?> entry : teacherMap.entrySet()) {
        BusinessResponseEnum.COLUMNNAMEDIFF.assertIsTrue(studentMap.containsKey(entry.getKey()));
        //教师value
        Object value = entry.getValue();
        //学生value
        Object studentValue = studentMap.get(entry.getKey());
        if (value instanceof ResultSetInfo) {
          //比较结果集
          FunctionUtil.compareResultSet((ResultSetInfo) value, (ResultSetInfo) studentValue);
        } else {
          //单个值或record比较是否相同
          BusinessResponseEnum.COLUMNVALUEDIFF.assertIsTrue(Objects.equals(value, studentValue), value, studentValue);
        }

      }
    }
  }


  //视图类型题目
  public void veryVIEWDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    //根据习题id查询标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不存在抛出异常
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    //解析答案获取key：视图名称和value:操作类型
    Map<String, String> answer = generatorViewNameAndType(exercise.getStandardAnswser());
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
    //判断是否为视图语句
    String lowerAnswer = standardAnswer.toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");
    boolean isView = lowerAnswer.contains("createview")
      || lowerAnswer.contains("createorreplaceview")
      || lowerAnswer.contains("alterview")
      || lowerAnswer.contains("dropview");
    BusinessResponseEnum.ISVIEW.assertIsTrue(isView);
    for (String answer : answers) {
      String answerTrim = answer.toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");

      //新增的视图
      if (answerTrim.startsWith("createview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("createview") + 10, answerTrim.indexOf("asselect")), CheckStatus.INSERT.toString());
      }
      if (answerTrim.startsWith("createorreplaceview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("createorreplaceview") + 19, answerTrim.indexOf("asselect")), CheckStatus.INSERT.toString());
      }
      //修改的视图
      if (answerTrim.startsWith("alterview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("alterview") + 9, answerTrim.indexOf("asselect")), CheckStatus.UPDATE.toString());
      }
      //删除的视图
      if (answerTrim.startsWith("dropview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("dropview") + 8, answerTrim.indexOf(";")), CheckStatus.DEL.toString());
      }
    }
    return map;

  }


  //视图类型题目
  public void testRunVIEWDDLTypeExercise(TestRunModel model, ResponseModel responseModel, UserInfo userInfo) {
    //查询到的视图结果
    HashMap<String, List<TSceneField>> map = new HashMap<>();
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
        //执行正确答案得到视图结构
        List<TSceneField> teacherFields = FunctionUtil.getViewInfo(userInfo, model, viewName, TableInfoEvent.FIELD, TSceneField.class, responseModel);
        map.put(viewName, teacherFields);
      }

    });

    responseModel.setViewInfo(map);

  }

  /**
   * @description:字段恢复
   * @author:
   * @date: 2023/6/8 13:43
   * @param: [request, id： checkFieldId, exerciseId： 习题id]
   * @return: boolean
   **/
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean fieldRecovery(HttpServletRequest request, Long id, Long exerciseId) {
    //判断该习题是否保存
    if (exerciseService.isSave(exerciseId)) {
      return checkFieldService.removeById(id);
    } else {
      return checkFieldTempService.removeById(id);
    }

  }

  @Override
  public boolean constraintRecovery(HttpServletRequest request, Long id, Long exerciseId) {
    //判断该习题是否保存
    if (exerciseService.isSave(exerciseId)) {
      return checkConstraintService.removeById(id);
    } else {
      return checkConstraintTempService.removeById(id);
    }
  }

  @Override
  public boolean indexRecovery(HttpServletRequest request, Long id, Long exerciseId) {
    //判断该习题是否保存
    if (exerciseService.isSave(exerciseId)) {
      return checkIndexService.removeById(id);
    } else {
      return checkIndexTempService.removeById(id);
    }
  }

  @Override
  public boolean seqRecovery(HttpServletRequest request, Long id, Long exerciseId) {
    //判断该习题是否保存
    if (exerciseService.isSave(exerciseId)) {
      return checkSeqService.removeById(id);
    } else {
      return checkSeqTempService.removeById(id);
    }
  }

  @Override
  public boolean fkRecovery(HttpServletRequest request, Long id, Long exerciseId) {
    //判断该习题是否保存
    if (exerciseService.isSave(exerciseId)) {
      return checkFkService.removeById(id);
    } else {
      return checkFkTempService.removeById(id);
    }
  }

  /**
   * @description: 习题保存时，迁移临时数据到正式表
   * @author:
   * @date: 2023/6/28 15:50
   * @param: [exerciseId]
   * @return: void
   **/
  @Override
  public void migrateTOrealityTable(Long exerciseId,Long historyExerciseId) {

    //迁移表校验
    tableMigrate(exerciseId,historyExerciseId);
    //迁移字段校验
    fieldMigrate(exerciseId,historyExerciseId);
    //约束
    constraintMigrate(exerciseId,historyExerciseId);
    //外键
    fkMigrate(exerciseId,historyExerciseId);
    //索引
    indexMigrate(exerciseId,historyExerciseId);
    //序列
    seqMigrate(exerciseId,historyExerciseId);
  }

  private void seqMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckSeqTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkSeqTempService, checkSeqService, TCheckSeq.class, modifyItem);
  }

  private void indexMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckIndexTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkIndexTempService, checkIndexService, TCheckIndex.class, modifyItem);
  }

  private void fkMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckFkTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkFkTempService, checkFkService, TCheckFk.class, modifyItem);
  }

  private void constraintMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckConstraintTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkConstraintTempService, checkConstraintService, TCheckConstraint.class, modifyItem);
  }

  private void fieldMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckFieldTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkFieldTempService, checkFieldService, TCheckField.class, modifyItem);
  }

  private void tableMigrate(Long exerciseId,Long historyExerciseId) {
    Consumer<TCheckDetailTemp> modifyItem = item -> {
      item.setExerciseId(exerciseId);
      item.setId(null);
    };
    migrateToReality(historyExerciseId, checkDetailTempService, checkDetailService, TCheckDetail.class, modifyItem);
  }

  //临时表迁移到正式表工具
  public static <T, R> void migrateToReality(Long exerciseId, IService<T> checkDetailTempService, IService<R> checkDetailService, Class<R> targetClass, Consumer<T> modifyItem) {
    List<T> detailTemps = checkDetailTempService.list(new QueryWrapper<T>()
      .eq("exercise_id", exerciseId));
    if (detailTemps != null && !detailTemps.isEmpty()) {
      List<T> temps = detailTemps.stream().map(item -> {
        modifyItem.accept(item);  // 调用传入的方法修改元素
        return item;
      }).collect(Collectors.toList());

      List<R> details = CopyUtils.copyListProperties(temps, targetClass);
      if (details != null && details.size() > 0) {
        boolean saveBatch = checkDetailService.saveBatch(details);
        if (!saveBatch) {
          throw new APIException("保存失败");
        }
        //删除历史数据
        boolean delTemps = checkDetailTempService.remove(new QueryWrapper<T>().eq("exercise_id", exerciseId));
        if (!delTemps) {
          throw new APIException("删除失败");
        }
      }


    }
  }


  //DML类型题目
  public void veryDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    boolean save = exerciseService.isSave(model.getExerciseId());
    //解析答案
    // StoreAnswer storeAnswer = gengratorAnswer(model.getStandardAnswer());
    //非新增表答案
    // Map<String, String> commonAnswers = storeAnswer.getCommonAnswers();
    //新增表答案
    // Map<String, String> addAnswers = storeAnswer.getAddAnswers();
    //根据场景id查询t_scene_detail
    List<TSceneDetail> details = sceneDetailService.list(new QueryWrapper<TSceneDetail>()
      .eq("scene_id", model.getSceneId()));
    //依赖场景
    if (details != null && !details.isEmpty()) {
      for (TSceneDetail detail : details) {
        //  if (StringUtils.isNotBlank(commonAnswers.get(detail.getId().toString()))) {
        //初始化场景并开启新的模式,执行答案获取相关答案信息
        VerificationList answerVerify = extractAnswer(
          getTableName(detail, model, save) == null ? detail.getTableName() : getTableName(detail, model, save)
          , userInfo, model.getSceneId(), model.getExerciseId()
          , new StringBuilder(model.getStandardAnswer()));
        //查询相关校验点信息
        VerificationList checkVerify = getVerify(detail.getId(), model.getExerciseId(), null, save);
        //校验模块进行校验
        checkAnswer(checkVerify, answerVerify);
        //}

      }
    }
    //不依赖场景
    List<TCheckDetail> addDetails = null;
    if (save) {
      addDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", model.getExerciseId())
        .isNull("scene_detail_id"));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", model.getExerciseId())
        .isNull("scene_detail_id"));
      addDetails = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }
    //不依赖场景新增表结构
    if (!addDetails.isEmpty()) {
      for (TCheckDetail detail : addDetails) {
        //初始化场景并开启新的模式,执行答案获取相关答案信息
        VerificationList answerVerify = extractAnswer(detail.getTableName(), userInfo, model.getSceneId()
          , model.getExerciseId(), new StringBuilder(model.getStandardAnswer()));
        //查询相关校验点信息
        VerificationList checkVerify = getVerify(-1L, model.getExerciseId(), detail.getTableName(), save);
        //校验模块进行校验
        checkAnswer(checkVerify, answerVerify);
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
    //判断是否临时表
    boolean save = exerciseService.isSave(model.getExerciseId());
    //习题中新增表生成的校验点描述
    newTableTODescription(model, res, save);
    //习题中依赖场景表生成的校验点描述
    historyTableTODescription(model, res, save);
    return res.toString();
  }

  //习题中依赖场景表生成的校验点描述
  private void historyTableTODescription(GeneratorDescription model, StringBuilder res, boolean save) {
    //场景详情集合
    List<TSceneDetail> sceneDetails = sceneDetailService.list(new QueryWrapper<TSceneDetail>()
      .eq("scene_id", model.getSceneId()));
    for (TSceneDetail sceneDetail : sceneDetails) {
      //生成索引描述
      generatorIndexDescription(sceneDetail, model, res, save);
      addWrap(res);
      //生成约束描述
      generatorConstraintDescription(sceneDetail, model, res, save);
      addWrap(res);
      //生成外键描述
      generatorFkDescription(sceneDetail, model, res, save);
      addWrap(res);
      //生成序列描述
      generatorSequenceDescription(sceneDetail, model, res, save);
      addWrap(res);
      //生成字段描述
      generatorFieldDescription(sceneDetail, model, res, save);
      addWrap(res);
      //生成表描述
      generatorTableDescription(sceneDetail, model, res, save);


    }
  }

  //生成表描述
  private void generatorTableDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckDetail> details = null;
    if (save) {
      details = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      details = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }

    if (!details.isEmpty()) {
      StringBuilder tableDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorDescriptions(details);
      if (tableDes != null) {
        res.append(tableDes);
      }
    }
  }

  //生成字段描述
  private void generatorFieldDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckField> fields = null;
    if (save) {
      fields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      fields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }

    if (!fields.isEmpty()) {
      StringBuilder fieldDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorDescriptions(fields);
      if (fieldDes != null) {
        res.append(fieldDes);
      }
    }
  }

  //生成序列描述
  private void generatorSequenceDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckSeq> seqList = null;
    if (save) {
      seqList = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckSeqTemp> checkSeqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      seqList = CopyUtils.copyListProperties(checkSeqTemps, TCheckSeq.class);
    }

    if (!seqList.isEmpty()) {
      StringBuilder seqDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorDescriptions(seqList);
      if (seqDes != null) {
        res.append(seqDes);
      }
    }
  }

  //生成外键描述
  private void generatorFkDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckFk> fkList = null;
    if (save) {
      fkList = checkFkService.list(new QueryWrapper<TCheckFk>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckFkTemp> checkFkTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      fkList = CopyUtils.copyListProperties(checkFkTemps, TCheckFk.class);
    }

    if (fkList != null && !fkList.isEmpty()) {
      StringBuilder fkDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorDescriptions(fkList);
      if (fkDes != null) {
        res.append(fkDes);
      }
    }
  }

  //生成约束相关描述
  private void generatorConstraintDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckConstraint> constraintList = null;
    if (save) {
      constraintList = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      constraintList = CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }

    if (!constraintList.isEmpty()) {
      StringBuilder constraintDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorDescriptions(constraintList);
      if (constraintDes != null) {
        res.append(constraintDes);
      }
    }
  }

  //生成索引相关描述
  private void generatorIndexDescription(TSceneDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckIndex> indexList = null;
    if (save) {
      indexList = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckIndexTemp> indexTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>().eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      indexList = CopyUtils.copyListProperties(indexTemps, TCheckIndex.class);
    }

    if (!indexList.isEmpty()) {
      StringBuilder indexDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorDescriptions(indexList);
      if (indexDes != null) {
        res.append(indexDes);
      }
    }
  }

  //习题中新增表生成的相关校验点描述
  private void newTableTODescription(GeneratorDescription model, StringBuilder res, boolean save) {
    //新增的表
    List<TCheckDetail> details = getInsertCheckDetails(model.getExerciseId(), save);

    if (!details.isEmpty()) {
      for (TCheckDetail detail : details) {
        //新建表和新建字段描述
        createTableAndFieldsDescription(detail, model, res, save);
        addWrap(res);
        //新增的索引
        createIndexDescription(detail, model, res, save);
        addWrap(res);
        //新增的约束
        createConstraintDescription(detail, model, res, save);
        addWrap(res);
        //新增的外键
        createFKDescription(detail, model, res, save);
        addWrap(res);
        //新增的序列
        createSeqDescription(detail, model, res, save);
      }
    }
  }


  //新增的序列
  private void createSeqDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckSeq> seqList = null;
    if (save) {
      seqList = checkSeqService.list(new QueryWrapper<TCheckSeq>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
    } else {
      List<TCheckSeqTemp> seqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
      seqList = CopyUtils.copyListProperties(seqTemps, TCheckSeq.class);
    }

    if (!seqList.isEmpty()) {
      StringBuilder seqDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorDescriptions(seqList);
      if (seqDes != null) {
        res.append(seqDes);
      }
    }
  }

  //新增的外键
  private void createFKDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckFk> fkList = null;
    if (save) {
      fkList = checkFkService.list(new QueryWrapper<TCheckFk>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
    } else {
      List<TCheckFkTemp> fkTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
      fkList = CopyUtils.copyListProperties(fkTemps, TCheckFk.class);
    }

    if (!fkList.isEmpty()) {
      StringBuilder fkDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorDescriptions(fkList);
      if (fkDes != null) {
        res.append(fkDes);
      }
    }
  }

  //新增的约束
  private void createConstraintDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckConstraint> constraintList = null;
    if (save) {
      constraintList = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
      constraintList = CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }

    if (!constraintList.isEmpty()) {
      StringBuilder constraintDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorDescriptions(constraintList);
      if (constraintDes != null) {
        res.append(constraintDes);
      }
    }
  }

  //新增的索引
  private void createIndexDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    List<TCheckIndex> indexList = null;
    if (save) {
      indexList = checkIndexService.list(new QueryWrapper<TCheckIndex>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
    } else {
      List<TCheckIndexTemp> indexTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>().eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
      indexList = CopyUtils.copyListProperties(indexTemps, TCheckIndex.class);
    }

    if (!indexList.isEmpty()) {
      StringBuilder indexDes = GeneratorDescriptionProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorDescriptions(indexList);
      if (indexDes != null) {
        res.append(indexDes);
      }
    }

  }

  //新建表和新建字段描述
  private void createTableAndFieldsDescription(TCheckDetail detail, GeneratorDescription model, StringBuilder res, boolean save) {
    //新增的表中字段
    List<TCheckField> fields = null;
    if (save) {
      fields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq("table_name", detail.getTableName()));
      fields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }


    //生成新增表的描述
    StringBuilder answer = generatorDescriptionCreateTableProcess.generatorAnswer(detail, fields);
    if (answer != null) {
      res.append(answer);
    }
  }

  //表可能被重命名，根据修改后表名查询表结构
  private String getTableName(TSceneDetail detail, TestRunModel model, boolean save) {
    String tableName = null;
    //查询场景表校验表
    List<TCheckDetail> tCheckDetails = null;
    if (save) {
      tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq("scene_detail_id", detail.getId()));
      tCheckDetails = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }


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

  /**
   * @description: 查询校验点
   * @author:
   * @date: 2023/6/28 11:16
   * @param: [id: sceneDetailId, exerciseId, tableName:新增表的表名]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.VerificationList
   **/
  private VerificationList getVerify(Long id, Long exerciseId, String tableName, boolean save) {
    VerificationList verificationList = new VerificationList();
    //查询表
    List<TCheckDetail> tCheckDetails = null;
    if (save) {
      tCheckDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckDetailTemp> detailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      tCheckDetails = CopyUtils.copyListProperties(detailTemps, TCheckDetail.class);
    }
    verificationList.setCheckDetails(tCheckDetails);
    //查询字段
    List<TCheckField> checkFields = null;
    if (save) {
      checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      checkFields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
    }
    verificationList.setCheckFields(checkFields);
    //新增索引
    List<TCheckIndex> indices = null;
    if (save) {
      indices = checkIndexService.list(new QueryWrapper<TCheckIndex>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckIndexTemp> indexTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      indices = CopyUtils.copyListProperties(indexTemps, TCheckIndex.class);
    }
    verificationList.setCheckIndexList(indices);
    //新增约束
    List<TCheckConstraint> constraints = null;
    if (save) {
      constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      constraints = CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }
    verificationList.setCheckConstraints(constraints);
    //新增外键
    List<TCheckFk> fks = null;
    if (save) {
      fks = checkFkService.list(new QueryWrapper<TCheckFk>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckFkTemp> fkTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      fks = CopyUtils.copyListProperties(fkTemps, TCheckFk.class);
    }
    verificationList.setCheckFks(fks);
    //新增序列
    List<TCheckSeq> seqs = null;
    if (save) {
      seqs = checkSeqService.list(new QueryWrapper<TCheckSeq>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
    } else {
      List<TCheckSeqTemp> seqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>()
        .eq("exercise_id", exerciseId)
        .eq(id == -1, "table_name", tableName)
        .eq(id != -1, "scene_detail_id", id));
      seqs = CopyUtils.copyListProperties(seqTemps, TCheckSeq.class);
    }
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
                                         Long exerciseId, StringBuilder builder) {
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
  private VerificationList informationExtraction(Statement statement, String tableName, String schemaName) throws SQLException {
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
  private <T> List<T> getInfo(Statement statement, String tableName, String schemaName, TableInfoEvent event, Class<T> clazz) throws SQLException {
    //获取sql
    EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
    String executeSql = eventProcess.execute(schemaName, tableName);
    //得到结果
    ResultSet resultSetDetail = statement.executeQuery(executeSql);
    return new ResultSetMapper<T>().mapRersultSetToObject(resultSetDetail, clazz);
  }

  private boolean removeSeqVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkSeqService.remove(new QueryWrapper<TCheckSeq>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    } else {
      return checkSeqTempService.remove(new QueryWrapper<TCheckSeqTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }
  }

  private boolean removeFkVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkFkService.remove(new QueryWrapper<TCheckFk>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    } else {
      return checkFkTempService.remove(new QueryWrapper<TCheckFkTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }
  }

  private boolean removeConstraintVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkConstraintService.remove(new QueryWrapper<TCheckConstraint>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    } else {
      return checkConstraintTempService.remove(new QueryWrapper<TCheckConstraintTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }
  }

  private boolean removeIndexVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkIndexService.remove(new QueryWrapper<TCheckIndex>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != null, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));

    } else {
      return checkIndexTempService.remove(new QueryWrapper<TCheckIndexTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }
  }

  private boolean removeFieldVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkFieldService.remove(new QueryWrapper<TCheckField>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    } else {
      return checkFieldTempService.remove(new QueryWrapper<TCheckFieldTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }
  }

  private boolean removeTableVerify(RecoveryModel model) {
    if (exerciseService.isSave(model.getExerciseId())) {
      return checkDetailService.remove(new QueryWrapper<TCheckDetail>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    } else {
      return checkDetailTempService.remove(new QueryWrapper<TCheckDetailTemp>()
        .eq("exercise_id", model.getExerciseId())
        .eq(model.getSceneDetailId() != -1, "scene_detail_id", model.getSceneDetailId())
        .eq(model.getSceneDetailId() == -1, "table_name", model.getTableName()));
    }

  }

  //数据拷贝
  private void copyData(Long exerciseId) {
    SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
    String[] sqlStatements = {
      "INSERT INTO t_check_detail SELECT * FROM t_check_detail_temp WHERE exerciseId=" + exerciseId,
      "INSERT INTO t_check_field SELECT * FROM t_check_field_temp WHERE exerciseId=" + exerciseId,
      "INSERT INTO t_check_constraint SELECT * FROM t_check_constraint_temp WHERE exerciseId=" + exerciseId,
      "INSERT INTO t_check_fk SELECT * FROM t_check_fk_temp WHERE exerciseId=" + exerciseId,
      "INSERT INTO t_check_index SELECT * FROM t_check_index_temp WHERE exerciseId=" + exerciseId,
      "INSERT INTO t_check_seq SELECT * FROM t_check_seq_temp WHERE exerciseId=" + exerciseId,
    };
    try {
      for (String sql : sqlStatements) {
        sqlSession.update(sql);
      }
      sqlSession.commit();
      logger.info("数据拷贝完成");
    } catch (Exception e) {
      sqlSession.rollback();
      e.printStackTrace();
      logger.error("数据拷贝失败", e);
      throw new APIException("保存失败");
    } finally {
      sqlSession.close();
    }
  }
}
