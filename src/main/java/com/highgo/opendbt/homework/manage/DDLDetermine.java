package com.highgo.opendbt.homework.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.StoreAnswer;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.tools.FunctionUtil;
import com.highgo.opendbt.verificationSetup.tools.ResultSetMapper;
import com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule.CheckEventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: DDL类型sql题判定
 * @Title: SingleChoiceDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("DDLDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.DDL)
@NoArgsConstructor
public class DDLDetermine extends Determine {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    if (NUllJudgement(stuHomeworkInfo, exerciseResult)) {
      return;
    }
    String answer = exerciseResult == null ? "" : exerciseResult.replaceAll("<p>", "").replaceAll("</p>", "");
    Integer exerciseId = stuHomeworkInfo.getExerciseId();
    //根据习题id查询场景id
    TNewExercise exercise = exerciseService.getById(exerciseId);

    Score score = new Score();
    score.setAnswer(answer);
    score.setExerciseId(exerciseId);
    score.setExerciseType(7);
    score.setVerySql(exercise.getVerySql());
    score.setSceneId(exercise.getSceneId());
    try {
      SubmitResult result = this.submitAnswer(loginUser, score, 0, false, 0);
      if (result.isScoreRs()) {
        stuHomeworkInfo.setIsCorrect(1);
        stuHomeworkInfo.setExerciseScore(stuHomeworkInfo.getExerciseActualScore());
      } else {
        stuHomeworkInfo.setIsCorrect(2);
        //判定题目得分
        stuHomeworkInfo.setExerciseScore(0.0);
      }
    } catch (Exception e) {
      logger.error("sql题批阅报错" + e.getMessage(), e);
      stuHomeworkInfo.setIsCorrect(2);
      //判定题目得分
      stuHomeworkInfo.setExerciseScore(0.0);
    }

  }

  @Override
  public SubmitResult submitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    TestRunModel model = new TestRunModel();
    model.setStandardAnswer(score.getAnswer());
    model.setVerySql(score.getVerySql());
    model.setExerciseType(score.getExerciseType());
    model.setExerciseId(score.getExerciseId());
    model.setSceneId(score.getSceneId());
    //返回结果
    SubmitResult result = new SubmitResult();
    try {
      veryDDLTypeExercise(model, loginUser);
      result.setExecuteRs(true);
      result.setScoreRs(true);
    } catch (Exception e) {
      e.printStackTrace();
      result.setLog(e.getMessage());
      result.setScoreRs(false);
    }
    // 提交答案才会保存，测试运行不需要保存
    saveSubmitDate(loginUser, score, isSaveSubmitData, result);
    return result;
  }

  @Override
  public SubmitResult testSubmitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    //返回结果
    ResponseModel responseModel = new ResponseModel();
    //转换参数实体类
    TestRunModel model = new TestRunModel();
    model.setStandardAnswer(score.getAnswer());
    model.setVerySql(score.getVerySql());
    model.setExerciseType(score.getExerciseType());
    model.setExerciseId(score.getExerciseId());
    model.setSceneId(score.getSceneId());
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(score.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, score.getExerciseId());
    model.setSceneId(exercise.getSceneId());
    //若没有校验查询sql则返回答案中的查询sql结果集
    if (StringUtils.isBlank(exercise.getVerySql())) {
      model.setVerySql(getSelectSql(model.getStandardAnswer()));
    }
    //执行正确答案//返回查询结果
    FunctionUtil.executeSql(loginUser, model, responseModel);
    return responseModel;
  }

  @Override
  public ResponseModel testRunAnswer(TestRunModel model, ResponseModel responseModel, UserInfo loginUser) {
    verifyCommonService.veryDDLTypeExercise(model, loginUser);
    responseModel.setSelect(false);
    return responseModel;
  }


  //DML类型题目
  private void veryDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    //解析答案
    StoreAnswer storeAnswer = gengratorAnswer(model.getStandardAnswer());
    //非新增表答案
    Map<String, String> commonAnswers = storeAnswer.getCommonAnswers();
    //新增表答案
    Map<String, String> addAnswers = storeAnswer.getAddAnswers();
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    model.setSceneId(exercise.getSceneId());
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
  private <T> List<T> getInfo(Statement statement, String tableName, String schemaName, TableInfoEvent event, Class<T> clazz) throws
    SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    //获取sql
    EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
    String executeSql = eventProcess.execute(schemaName, tableName);
    //得到结果
    ResultSet resultSetDetail = statement.executeQuery(executeSql);
    return new ResultSetMapper<T>().mapRersultSetToObject(resultSetDetail, clazz);
  }

}
