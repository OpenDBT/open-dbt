package com.highgo.opendbt.homework.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.exercise.domain.entity.TExerciseInfo;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 填空题题判定
 * @Title: SingleChoiceDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("FillInBlanksDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.FILL_IN_BLANKS)
@NoArgsConstructor
public class FillInBlanksDetermine extends Determine {


  @Override
  public void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    if (NUllJudgement(stuHomeworkInfo, exerciseResult)) {
      return;
    }
    //添加空题空格数
    int cellNum = 0;
    //填空题正确空格数
    int rightNum = 0;
    //学生答案
    String[] studentAnswer = exerciseResult.split("@_@");
    //标准答案
    List<TExerciseInfo> actualAnswer = stuHomeworkInfo.getExerciseInfoList().stream().sorted(Comparator.comparing(TExerciseInfo::getPrefix)).collect(Collectors.toList());
    cellNum = actualAnswer.size();
    for (int i = 0; i < actualAnswer.size(); i++) {
      //一个空格可能有多个答案
      String[] answers = actualAnswer.get(i).getContent().split(";");
      for (int n = 0; n < answers.length; n++) {
        //有一个答案正确即为正确
        if (answers[n].equals(studentAnswer[i])) {
          rightNum += 1;
          break;
        }

      }
    }
    if (cellNum == rightNum) {
      //全对
      stuHomeworkInfo.setIsCorrect(1);
      stuHomeworkInfo.setExerciseScore(stuHomeworkInfo.getExerciseActualScore());
    } else if (rightNum == 0) {
      //错误
      stuHomeworkInfo.setIsCorrect(2);
      stuHomeworkInfo.setExerciseScore(0.0);
    } else {
      //半对
      stuHomeworkInfo.setIsCorrect(3);
      stuHomeworkInfo.setExerciseScore(BigDecimal.valueOf(stuHomeworkInfo.getExerciseActualScore()).multiply(BigDecimal.valueOf(rightNum / (cellNum * 1.0))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
  }

  @Override
  public SubmitResult submitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {

    SubmitResult result = new SubmitResult();
    //答案为空跳过后面判断
    if (!NUllJudgement(result, score.getAnswer())) {
      judgeFillInBlank(score, result);
    }
    // 提交答案才会保存，测试运行不需要保存
    saveSubmitDate(loginUser, score, isSaveSubmitData, result);

    return result;
  }

  private void judgeFillInBlank(Score score, SubmitResult result) {
    //添加空题空格数
    int cellNum = 0;
    //填空题正确空格数
    int rightNum = 0;
    //学生答案
    String[] studentAnswer = score.getAnswer().split("@_@");
    //标准答案
    List<TExerciseInfo> exerciseInfos = exerciseInfoService.list(new QueryWrapper<TExerciseInfo>().eq("exercise_id", score.getExerciseId()));
    //填空题习题详情中的答案不能为空
    BusinessResponseEnum.UNEXERCISEiNFO.assertIsNotEmpty(exerciseInfos);
    List<TExerciseInfo> actualAnswer = exerciseInfos.stream().sorted(Comparator.comparing(TExerciseInfo::getPrefix)).collect(Collectors.toList());
    cellNum = actualAnswer.size();
    for (int i = 0; i < actualAnswer.size(); i++) {
      //一个空格可能有多个答案
      String[] answers = actualAnswer.get(i).getContent().split("@_@");
      for (int n = 0; n < answers.length; n++) {
        //有一个答案正确即为正确
        if (answers[n].equals(studentAnswer[i])) {
          rightNum += 1;
        }
        break;
      }
    }
    if (cellNum == rightNum) {
      result.setScoreRs(true);// 结果集是否正确
    } else {
      result.setScoreRs(false);
    }
  }


}

