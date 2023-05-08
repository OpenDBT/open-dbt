package com.highgo.opendbt.student.manage;

import com.highgo.opendbt.homework.manage.DetermineService;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @Description: 学生端练习题保存和提交
 * @Title: SubmitAnswer
 * @Package com.highgo.opendbt.student.manage
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/21 10:44
 */
@Service
public class AsyncSubmitExerciseAnswer {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private DetermineService determineService;

  @Async("threadPoolTaskExecutor")
  //学生练习不同题目提交答案
  public CompletableFuture<SubmitResult> submitAnswer(UserInfo loginUser, Score score, boolean isSaveSubmitData) {
    logger.info("thread start.");
    SubmitResult submitResult = determineService.submitAnswer(loginUser, score, isSaveSubmitData);
    CompletableFuture<SubmitResult> submitResultCompletableFuture = CompletableFuture.completedFuture(submitResult);
    logger.info("thread end.");
    return submitResultCompletableFuture;
  }

  @Async("threadPoolTaskExecutor")
  //教师端正确答案测试运行
  public CompletableFuture<ResponseModel> testRunAnswer(UserInfo loginUser, TestRunModel model) {
    logger.info("thread start.");
    ResponseModel responseModel = determineService.testRunAnswer(loginUser, model);
    CompletableFuture<ResponseModel> submitResultCompletableFuture = CompletableFuture.completedFuture(responseModel);
    logger.info("thread end.");
    return submitResultCompletableFuture;
  }

  @Async("threadPoolTaskExecutor")
  //学生练习不同题目提交答案
  public CompletableFuture<SubmitResult> testSubmitAnswer(UserInfo loginUser, Score score) {
    logger.info("thread start.");
    SubmitResult submitResult = determineService.testSubmitAnswer(loginUser, score);
    CompletableFuture<SubmitResult> submitResultCompletableFuture = CompletableFuture.completedFuture(submitResult);
    logger.info("thread end.");
    return submitResultCompletableFuture;
  }

}
