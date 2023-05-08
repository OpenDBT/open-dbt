package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.tools.FunctionUtil;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 函数类型sql题判定
 * @Title: FunctionDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("functionDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.FUNCTION_DDL)
@NoArgsConstructor
public class FunctionDetermine extends Determine {
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
    score.setExerciseType(9);
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
    ResponseModel result = new ResponseModel();
    try {
      result = veryFUNCTIONDDLTypeExercise(model, loginUser);
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
  public ResponseModel testRunAnswer(TestRunModel model, ResponseModel responseModel, UserInfo loginUser) {
    //校验查询sql不能为空
    BusinessResponseEnum.VERYSQLNULL.assertIsTrue(StringUtils.isNotBlank(model.getVerySql()));
    //判断是否为函数相关DDL语句
    determineIsFunctionSql(model);
    FunctionUtil.executeFunctionSql(loginUser, model, responseModel);
    return responseModel;
  }

  private void determineIsFunctionSql(TestRunModel model) {
    //判断是否为视图语句
    String lowerAnswer = model.getStandardAnswer().toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");
    boolean isFunction = lowerAnswer.contains("createfunction")
      || lowerAnswer.contains("createorreplacefunction")
      || lowerAnswer.contains("alterfunction")
      || lowerAnswer.contains("dropfunction");
    BusinessResponseEnum.ISFUNCTION.assertIsTrue(isFunction);
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
    //判断是否为函数相关DDL语句
    determineIsFunctionSql(model);
    FunctionUtil.executeFunctionSql(loginUser, model, responseModel);
    return responseModel;
  }
  //函数类型题目
  private ResponseModel veryFUNCTIONDDLTypeExercise(TestRunModel model, UserInfo userInfo) {
    ResponseModel teacherAnswerResultMap = new ResponseModel();
    ResponseModel studentAnswerResultMap = new ResponseModel();
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    model.setSceneId(exercise.getSceneId());
    model.setVerySql(exercise.getVerySql());
    //执行学生答案返回
    FunctionUtil.executeFunctionSql(userInfo, model, studentAnswerResultMap);
    model.setStandardAnswer(exercise.getStandardAnswser());
    //执行标准答案返回
    FunctionUtil.executeFunctionSql(userInfo, model, teacherAnswerResultMap);
    //比较返回结果
    compareFunction(teacherAnswerResultMap, studentAnswerResultMap);
    return studentAnswerResultMap;
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
      List<List<Map<String, Object>>> teacherLists = teacherAnswerResultMap.getFunctionResult();
      List<List<Map<String, Object>>> studentLists = studentAnswerResultMap.getFunctionResult();
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
}
