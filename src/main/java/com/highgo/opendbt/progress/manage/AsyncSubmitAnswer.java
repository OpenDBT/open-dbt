package com.highgo.opendbt.progress.manage;

import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.TimeUtil;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.homework.manage.DetermineService;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.mapper.ScoreMapper;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 异步提交
 * @Title: AsyncSubmitAnswer
 * @Package com.highgo.opendbt.progress.manage
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/13 17:34
 */
@Service
public class AsyncSubmitAnswer {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private DetermineService determineService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private ScoreMapper scoreMapper;

  @Async("threadPoolTaskExecutor")
  public CompletableFuture<TStuHomeworkInfo> syncSubmitAnswer(HttpServletRequest request, Score score) {
    logger.info("task  start.");
    long start = System.currentTimeMillis();
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //根据习题id查询习题信息
    TNewExercise exercise = exerciseService.getExerciseInfo(request, score.getExerciseId());
    TStuHomeworkInfo homeworkInfo = new TStuHomeworkInfo();
    homeworkInfo.setExerciseInfoList(exercise.getExerciseInfos());
    homeworkInfo.setExerciseActualScore(100.0);//不为0的分数即可
    homeworkInfo.setStandardAnswser(exercise.getStandardAnswser());
    //借用作业模块判断题目对错和分数的判定方法
    determineService.determineExercise(loginUser, homeworkInfo, score.getAnswer());
    // 保存提交数据
    score.setCreateTime(TimeUtil.convertDateTime(new Date()));
    score.setScore(homeworkInfo.getIsCorrect() == 1 ? 100 : 0);
    score.setUserId(loginUser.getUserId());
    scoreMapper.add(score);
    long end = System.currentTimeMillis();
    logger.info("task  done,cost " + (end - start) + "ms.");
    return CompletableFuture.completedFuture(homeworkInfo);
  }
}
