package com.highgo.opendbt.homework.manage;

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

/**
 * @Description: 触发器
 * @Title: TriggerDetermine
 * @Package com.highgo.opendbt.homework.manage
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/24 10:57
 */
@Component("triggerDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.TRIGGER)
@NoArgsConstructor
public class TriggerDetermine extends Determine {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    if (NUllJudgement(stuHomeworkInfo, exerciseResult)) {
      return;
    }
    String answer = exerciseResult == null ? "" : exerciseResult.replaceAll("<p>", "").replaceAll("</p>", "");
    Long exerciseId = stuHomeworkInfo.getExerciseId();
    //根据习题id查询场景id
    TNewExercise exercise = exerciseService.getById(exerciseId);

    Score score = new Score();
    score.setAnswer(answer);
    score.setExerciseId(exerciseId);
    score.setExerciseType(10);
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
    ResponseModel result = new ResponseModel();;

    try {
      result = FunctionUtil.veryTrigger(model, loginUser);
      result.setExecuteRs(true);
      result.setScoreRs(true);
    } catch (Exception e) {
      e.printStackTrace();
      result.setLog(e.getMessage());
      result.setScoreRs(false);
    }

    saveSubmitDate(loginUser, score, isSaveSubmitData, result);
    return result;
  }

  @Override
  public ResponseModel testRunAnswer(TestRunModel model, ResponseModel responseModel, UserInfo loginUser) {
    //判断是否为视图相关DDL语句
    determineIsTrggerSql(model);
    //若没有校验查询sql则返回答案中的查询sql结果集
    if (StringUtils.isBlank(model.getVerySql())) {
      model.setVerySql(getSelectSql(model.getStandardAnswer()));
    }
    //执行正确答案//返回查询结果
    FunctionUtil.executeSql(loginUser, model, responseModel);
    return responseModel;
  }

  @Override
  public SubmitResult testSubmitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    //返回结果
    ResponseModel responseModel = new ResponseModel();
    //转换参数实体类
    TestRunModel model = new TestRunModel();
    model.setStandardAnswer(score.getAnswer());
    model.setExerciseType(score.getExerciseType());
    model.setExerciseId(score.getExerciseId());
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(score.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, score.getExerciseId());
    model.setSceneId(exercise.getSceneId()==null?-1:exercise.getSceneId());
    model.setVerySql(exercise.getVerySql());
    //判断是否为视图相关DDL语句
    determineIsTrggerSql(model);
    //若没有校验查询sql则返回答案中的查询sql结果集
    if (StringUtils.isBlank(exercise.getVerySql())) {
      model.setVerySql(getSelectSql(model.getStandardAnswer()));
    }
    //执行正确答案//返回查询结果
    FunctionUtil.executeSql(loginUser, model, responseModel);
    return responseModel;
  }

  private void determineIsTrggerSql(TestRunModel model) {
    //判断是否为视图语句
    String lowerAnswer = model.getStandardAnswer().toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");
    boolean isView = lowerAnswer.contains("createtrigger")
      || lowerAnswer.contains("createorreplacetrigger")
      || lowerAnswer.contains("altertrigger")
      || lowerAnswer.contains("droptrigger");
    BusinessResponseEnum.ISTRIGGER.assertIsTrue(isView);
  }


  private ResponseModel veryTrigger(TestRunModel model, UserInfo loginUser) {
    ResponseModel teacherAnswerResultMap = new ResponseModel();
    ResponseModel studentAnswerResultMap = new ResponseModel();
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    model.setSceneId(exercise.getSceneId()==null?-1:exercise.getSceneId());
    model.setVerySql(exercise.getVerySql());
    //执行学生答案返回
    FunctionUtil.executeSql(loginUser, model, studentAnswerResultMap);
    model.setStandardAnswer(exercise.getStandardAnswser());
    //执行正确答案返回
    FunctionUtil.executeSql(loginUser, model, teacherAnswerResultMap);
    //比较教师和学生的结果集是否一样
    FunctionUtil.compareResultSet(teacherAnswerResultMap.getResultSetInfo(), studentAnswerResultMap.getResultSetInfo());
    return studentAnswerResultMap;
  }
}
