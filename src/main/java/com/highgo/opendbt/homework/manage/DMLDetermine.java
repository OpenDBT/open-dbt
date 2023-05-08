package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.course.domain.entity.Knowledge;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.tools.FunctionUtil;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: DML类型 sql题判定
 * @Title: SingleChoiceDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("DMLDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.DML)
@NoArgsConstructor
public class DMLDetermine extends Determine {

  @Override
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    if (NUllJudgement(stuHomeworkInfo, exerciseResult)) {
      return;
    }
    Score score = new Score();
    score.setAnswer(exerciseResult == null ? "" : exerciseResult.replaceAll("<p>", "").replaceAll("</p>", ""));
    score.setExerciseId(stuHomeworkInfo.getExerciseId());
    try {
      SubmitResult result = this.submitAnswer(loginUser, score, 0, false, 0);
      boolean scoreRs = result.isScoreRs();
      boolean executeRs = result.isExecuteRs();
      if (scoreRs && executeRs) {
        stuHomeworkInfo.setIsCorrect(1);
        stuHomeworkInfo.setExerciseScore(stuHomeworkInfo.getExerciseActualScore());
      } else {
        stuHomeworkInfo.setIsCorrect(2);
        //判定题目得分
        stuHomeworkInfo.setExerciseScore(0.0);
      }
    } catch (Exception e) {
      logger.error("sql提批阅报错" + e.getMessage(), e);
      stuHomeworkInfo.setIsCorrect(2);
      //判定题目得分
      stuHomeworkInfo.setExerciseScore(0.0);
    }

  }

  @Override
  public SubmitResult submitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {

    try {
      List<Knowledge> coverageKnowledgeList = new ArrayList<>();
      SubmitResult result = new SubmitResult(coverageKnowledgeList, score.getUsageTime(), 0);
      // 查询习题信息
      TNewExercise exercise;
      exercise = exerciseService.getById(score.getExerciseId());
      BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, score.getExerciseId());
      // 验证老师和学生的答案是否是同一类型
      String[] teacherAnswerArray = exercise.getStandardAnswser().trim().split(" ");
      String[] studentAnswerArray = score.getAnswer().trim().split(" ");
      if (!teacherAnswerArray[0].toLowerCase().equals(studentAnswerArray[0].toLowerCase())) {
        result.setExecuteRs(false);
        result.setScoreRs(false);
        result.setLog(Message.get("SQLNotMatchKnowledge"));
      } else {
        // 验证答案
        boolean answerRs = veryAnswerService.verifyAnswer(loginUser, exercise, score, result, exerciseSource, isSaveSubmitData);
        result.setScoreRs(answerRs);// 结果集是否正确
      }
      // 提交答案才会保存，测试运行不需要保存
      saveSubmitDate(loginUser, score, isSaveSubmitData, result);
      return result;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new APIException(e.getMessage());
    }
  }

  @Override
  public SubmitResult testSubmitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    return this.submitAnswer(loginUser, score, exerciseSource, isSaveSubmitData, entranceType);
  }


  @Override
  public ResponseModel testRunAnswer(TestRunModel model, ResponseModel responseModel, UserInfo loginUser) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    try {
      // 初始化脚本并获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, model.getSceneId(), 0, schemaConnection, 0);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        //普通测试运行
        if (model.getExerciseType() == 6) {
          FunctionUtil.testDML(model.getStandardAnswer(), responseModel, statement);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(resultSet);
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
    return responseModel;
  }
}
