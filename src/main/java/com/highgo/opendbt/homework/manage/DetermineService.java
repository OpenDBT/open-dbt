package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 不同题目类型的工具类
 * @Title: DetermineUtil
 * @Package com.highgo.opendbt.homework.manage
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/13 14:19
 */

@Component
public class DetermineService {
  Logger logger = LoggerFactory.getLogger(getClass());

  //判断题目答案对错
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String answer) {
    ExerciseFactory.createEventProcess(stuHomeworkInfo.getExerciseType()).determineExercise(loginUser, stuHomeworkInfo, answer);
  }

  //学生练习不同题目提交答案
  public SubmitResult submitAnswer(UserInfo loginUser, Score score, boolean isSaveSubmitData) {
    //学生答案处理
    score.setAnswer(score.getAnswer() == null ? "" : score.getAnswer()
      .replaceAll("<p>", "")
      .replaceAll("</p>", ""));
    return ExerciseFactory.createEventProcess(score.getExerciseType()).submitAnswer(loginUser, score, 0, isSaveSubmitData, 0);
  }

  //测试运行正确答案
  public ResponseModel testRunAnswer(UserInfo loginUser, TestRunModel model) {
    ResponseModel responseModel = new ResponseModel();
    return ExerciseFactory.createEventProcess(model.getExerciseType()).testRunAnswer(model, responseModel, loginUser);
  }

  public SubmitResult testSubmitAnswer(UserInfo loginUser, Score score) {
    //学生答案处理
    score.setAnswer(score.getAnswer() == null ? "" : score.getAnswer()
      .replaceAll("<p>", "")
      .replaceAll("</p>", ""));
    return ExerciseFactory.createEventProcess(score.getExerciseType()).testSubmitAnswer(loginUser, score, 0, false, 0);

  }
}
