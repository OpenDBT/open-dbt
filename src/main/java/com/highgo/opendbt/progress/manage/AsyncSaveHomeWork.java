package com.highgo.opendbt.progress.manage;

import com.google.common.util.concurrent.AtomicDouble;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.homework.domain.model.SaveHomework;
import com.highgo.opendbt.homework.domain.model.SaveHomeworkInfo;
import com.highgo.opendbt.homework.manage.DetermineService;
import com.highgo.opendbt.homework.mapper.TStuHomeworkInfoMapper;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 异步保存作业
 * @Title: AsyncSaveHomeWork
 * @Package com.highgo.opendbt.progress.manage
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/13 17:38
 */
@Service
public class AsyncSaveHomeWork {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private TStuHomeworkInfoMapper stuHomeworkInfoMapper;
  @Autowired
  private DetermineService determineService;

  @Async("threadPoolTaskExecutor")
  public CompletableFuture<String> syncSaveHomeWork(SaveHomeworkInfo item, SaveHomework homeWork, UserInfo loginUser, HttpServletRequest request, AtomicDouble score, List<TStuHomeworkInfo> tStuHomeworkInfos) {
    logger.info("task  start.");
    long start = System.currentTimeMillis();
    TStuHomeworkInfo stuHomeworkInfo = stuHomeworkInfoMapper
      .getHomeworkInfoAndExercise(homeWork.getHomeworkId(), item.getExerciseId(), loginUser.getUserId());
    if (stuHomeworkInfo != null) {
      //设置学生答案
      stuHomeworkInfo.setExerciseResult(item.getExerciseResult());
      //判断客观题对错和获得分数
      //if (stuHomeworkInfo.getExerciseStyle() == 2) {
        determineService.determineExercise(loginUser, stuHomeworkInfo, item.getExerciseResult());
        score.addAndGet(stuHomeworkInfo.getExerciseScore());
     // }
      stuHomeworkInfo.setUpdateUser(loginUser.getUserId()).setUpdateTime(new Date());
      tStuHomeworkInfos.add(stuHomeworkInfo);
    }
    long end = System.currentTimeMillis();
    logger.info("task  done,cost " + (end - start) + "ms.");
    return CompletableFuture.completedFuture("over");
  }
}
