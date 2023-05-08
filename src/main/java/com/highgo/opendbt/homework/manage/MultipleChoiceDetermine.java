package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.common.utils.ArraysUtils;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: 多选题判定
 * @Title: SingleChoiceDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("MultipleChoiceDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.MULTIPLE_CHOICE)
@NoArgsConstructor
public class MultipleChoiceDetermine extends Determine {

  @Override
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    if (NUllJudgement(stuHomeworkInfo, exerciseResult)) {
      return;
    }
    //全对
    if (ArraysUtils.equals(stuHomeworkInfo.getStandardAnswser().trim().split(","), exerciseResult.trim().split(","))) {
      stuHomeworkInfo.setIsCorrect(1);
      stuHomeworkInfo.setExerciseScore(stuHomeworkInfo.getExerciseActualScore());
    } else {
      //半对
      if (stuHomeworkInfo.getUnselectedGiven() == 1 && ArraysUtils.contains(stuHomeworkInfo.getStandardAnswser().trim().split(","), exerciseResult.trim().split(","))) {
        stuHomeworkInfo.setIsCorrect(3);
        stuHomeworkInfo.setExerciseScore(BigDecimal.valueOf(stuHomeworkInfo.getExerciseActualScore()).divide(BigDecimal.valueOf(2), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
      } else {
        //错误
        stuHomeworkInfo.setIsCorrect(2);
        stuHomeworkInfo.setExerciseScore(0.0);
      }
    }
  }


  @Override
  public SubmitResult submitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    SubmitResult result = new SubmitResult();
    //答案为空跳过后面判断
    if (!NUllJudgement(result, score.getAnswer())) {
      TNewExercise exercise = exerciseService.getById(score.getExerciseId());
      if (ArraysUtils.equals(exercise.getStandardAnswser().trim().split(","), score.getAnswer().trim().split(","))) {
        result.setScoreRs(true);
      } else {
        result.setScoreRs(false);
      }
    }
    // 提交答案才会保存，测试运行不需要保存
    saveSubmitDate(loginUser, score, isSaveSubmitData, result);
    return result;
  }
}
