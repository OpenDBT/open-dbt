package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.TimeUtil;
import com.highgo.opendbt.exercise.service.TExerciseInfoService;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.homework.tools.DMLVeryAnswerService;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.score.mapper.ScoreMapper;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorCreateTableProcess;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description: 试题判定对错抽象类
 * @author:
 * @date: 2022/9/22 15:34
 **/
@NoArgsConstructor
public abstract class Determine {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  protected TNewExerciseService exerciseService;
  @Autowired
  protected ScoreMapper scoreMapper;
  @Autowired
  protected DMLVeryAnswerService veryAnswerService;
  @Autowired
  protected TExerciseInfoService exerciseInfoService;
  @Autowired
  protected VerifyCommonService verifyCommonService;
  @Autowired
  protected RunAnswerService runAnswerService;
  @Autowired
  protected TSceneDetailService sceneDetailService;
  @Autowired
  protected TCheckDetailService checkDetailService;
  @Autowired
  protected TSceneFieldService sceneFieldService;
  @Autowired
  protected TCheckFieldService checkFieldService;
  @Autowired
  protected TSceneIndexService sceneIndexService;
  @Autowired
  protected TCheckIndexService checkIndexService;
  @Autowired
  protected TSceneConstraintService sceneConstraintService;
  @Autowired
  protected TCheckConstraintService checkConstraintService;
  @Autowired
  protected TSceneFkService sceneForeignKeyService;
  @Autowired
  protected TCheckFkService checkFkService;
  @Autowired
  protected TSceneSeqService sceneSeqService;
  @Autowired
  protected TCheckSeqService checkSeqService;
  @Autowired
  protected GeneratorCreateTableProcess generatorCreateTableProcess;

  //判定试题对错
  public abstract void determineExercise(UserInfo loginUser, TStuHomeworkInfo stuHomeworkInfo, String exerciseResult);

  //学生答案为空判断
  public boolean NUllJudgement(TStuHomeworkInfo stuHomeworkInfo, String exerciseResult) {
    boolean res = false;
    if (exerciseResult == null || exerciseResult == "") {
      //错误
      stuHomeworkInfo.setIsCorrect(2);
      stuHomeworkInfo.setExerciseScore(0.0);
      res = true;
    } else {
      String result = exerciseResult.replaceAll("@_@", "").trim();
      if (result == null || "".equals(result)) {
        //错误
        stuHomeworkInfo.setIsCorrect(2);
        stuHomeworkInfo.setExerciseScore(0.0);
        res = true;
      }
    }
    return res;
  }

  //学生答案为空判断
  public boolean NUllJudgement(SubmitResult submitResult, String exerciseResult) {
    boolean res = false;
    if (exerciseResult == null || exerciseResult == "") {
      //错误
      submitResult.setScoreRs(false);// 结果集是否正确
      res = true;
    } else {
      String result = exerciseResult.replaceAll("@_@", "").trim();
      if (result == null || "".equals(result)) {
        //错误
        submitResult.setScoreRs(false);// 结果集是否正确
        res = true;
      }
    }
    return res;
  }

  protected void saveSubmitDate(UserInfo loginUser, Score score, boolean isSaveSubmitData, SubmitResult result) {
    // 提交答案才会保存，测试运行不需要保存
    if (isSaveSubmitData) {
      // 保存提交数据
      //Date date = new Date(System.currentTimeMillis() - score.getUsageTime() * 1000);
      score.setCreateTime(TimeUtil.convertDateTime(new Date()));
      score.setScore(result.isScoreRs() ? 100 : 0);
      score.setUserId(loginUser.getUserId());
      scoreMapper.add(score);
    }
  }

  /**
   * @description:学生端学生练习测试运行和提交
   * @author:
   * @date: 2023/4/21 9:27
   * @param: [loginUser 登录人信息, score 习题、答案等信息, exerciseSource 习题来源exerciseSource为1是公题库0：私有题库 默认0, isSaveSubmitData 是否保存运行结果, entranceType历史作业中题目，固定0]
   * @return: com.highgo.opendbt.score.domain.model.SubmitResult
   **/
  public abstract SubmitResult submitAnswer(UserInfo loginUser, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType);

  /**
   * @description: 测试运行习题参考答案，只针对相关sql类型习题
   * @author:
   * @date: 2023/4/23 15:17
   * @param: [model 测试运行请求相关参数, responseModel返回参数, loginUser 当前登录人信息]
   * @return: com.highgo.opendbt.verificationSetup.domain.model.ResponseModel
   **/
  public ResponseModel testRunAnswer(TestRunModel model, ResponseModel responseModel, UserInfo loginUser) {
    throw new APIException("该类型题目无测试运行功能");
  }

  //筛选出答案中的查询语句
  protected String getSelectSql(String exerciseAnswer) {
   AtomicReference<String> selectSql=new AtomicReference<>();
    String[] answers = exerciseAnswer.trim().split(";");
    Arrays.stream(answers).forEach(answer -> {
      if (answer.trim().toLowerCase().startsWith("select")) {
        selectSql.set(answer);
      }
    });
    return selectSql.get();
  }
  //学生端测试运行
  public  SubmitResult testSubmitAnswer(UserInfo loginUser, Score score, int i, boolean isSaveSubmitData, int i1){
    throw new APIException("该类型题目无测试运行功能");
  };
}
