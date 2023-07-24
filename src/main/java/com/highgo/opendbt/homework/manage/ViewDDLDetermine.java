package com.highgo.opendbt.homework.manage;

import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.domain.model.ViewModel;
import com.highgo.opendbt.verificationSetup.tools.CheckStatus;
import com.highgo.opendbt.verificationSetup.tools.FunctionUtil;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: VIEW DDL类型sql题判定
 * @Title: SingleChoiceDetermine
 * @Package com.highgo.opendbt.homework.manage.determine
 * @Author:
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2022/9/22 15:35
 */
@Component("viewDDLDetermine")
@DetermineEventAnnotation(ExerciseTypeEvent.VIEW_DDL)
@NoArgsConstructor
public class ViewDDLDetermine extends Determine {
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
    score.setExerciseType(8);
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
    //model.setVerySql(score.getVerySql());
    model.setExerciseType(score.getExerciseType());
    model.setExerciseId(score.getExerciseId());
    //查询习题标准答案
    TNewExercise exercise = exerciseService.getById(score.getExerciseId());
    //习题不能为空
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, score.getExerciseId());
    model.setSceneId(exercise.getSceneId()==null?-1:exercise.getSceneId());
    model.setVerySql(exercise.getVerySql());
    //返回结果
    ResponseModel result = new ResponseModel();
    try {
      veryVIEWDDLTypeExercise(model, loginUser, result);
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

    //判断是否为视图相关DDL语句
    determineIsViewSql(model);
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
    determineIsViewSql(model);
    //若没有校验查询sql则返回答案中的查询sql结果集
    if (StringUtils.isBlank(exercise.getVerySql())) {
      model.setVerySql(getSelectSql(model.getStandardAnswer()));
    }
    //执行正确答案//返回查询结果
    FunctionUtil.executeSql(loginUser, model, responseModel);
    return responseModel;
  }

  private void determineIsViewSql(TestRunModel model) {
    //判断是否为视图语句
    String lowerAnswer = model.getStandardAnswer().toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");
    boolean isView = lowerAnswer.contains("createview")
      || lowerAnswer.contains("createorreplaceview")
      || lowerAnswer.contains("alterview")
      || lowerAnswer.contains("dropview");
    BusinessResponseEnum.ISVIEW.assertIsTrue(isView);
  }

  //视图类型题目
  private void veryVIEWDDLTypeExercise(TestRunModel model, UserInfo userInfo, ResponseModel responseModel) {
    //查询到的视图结果
    HashMap<String, List<TSceneField>> map = new HashMap<>();
    //根据习题id查询标准答案
    TNewExercise exercise = exerciseService.getById(model.getExerciseId());
    //习题不存在抛出异常
    BusinessResponseEnum.UNEXERCISE.assertNotNull(exercise, model.getExerciseId());
    model.setSceneId(exercise.getSceneId()==null?-1:exercise.getSceneId());
    model.setVerySql(exercise.getVerySql());

    //解析答案获取key：视图名称和value:操作类型
    Map<String, String> answer = generatorViewNameAndType(exercise.getStandardAnswser());
    //循环map
    answer.entrySet().forEach(item -> {
      //视图名称
      String viewName = item.getKey();
      //视图类型
      String viewType = item.getValue();
      //删除类型
      if (CheckStatus.DEL.toString().equalsIgnoreCase(viewType)) {
        List<ViewModel> info = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), model.getStandardAnswer(), viewName, TableInfoEvent.TABLE_EXISTS, ViewModel.class);
        //删除失败提示
        BusinessResponseEnum.FAILDELETEVIEW.assertIsFalse(info.get(0).getExists(), viewName);
      }

      //新增和修改类型
      if (CheckStatus.INSERT.toString().equalsIgnoreCase(viewType) || CheckStatus.UPDATE.toString().equalsIgnoreCase(viewType)) {
        //执行学生答案的到视图结构
        List<TSceneField> studentFields = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), model.getStandardAnswer(), viewName, TableInfoEvent.FIELD, TSceneField.class);
        //执行正确答案得到视图结构
        List<TSceneField> teacherFields = TableInfoUtil.getInfo(userInfo, model.getSceneId(), model.getExerciseId(), exercise.getStandardAnswser(), viewName, TableInfoEvent.FIELD, TSceneField.class);
        //比较视图结构，不同抛出异常
        compareView(teacherFields, studentFields);
        map.put(viewName, studentFields);
      }
    });
    responseModel.setViewInfo(map);
  }


  //解析答案获取视图名称和操作类型
  private Map<String, String> generatorViewNameAndType(String standardAnswer) {
    Map<String, String> map = new HashMap<>();
    String[] answers = standardAnswer.trim().split(";");
    //判断是否为视图语句
    String lowerAnswer = standardAnswer.toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");
    boolean isView = lowerAnswer.contains("createview")
      || lowerAnswer.contains("createorreplaceview")
      || lowerAnswer.contains("alterview")
      || lowerAnswer.contains("dropview");
    BusinessResponseEnum.ISVIEW.assertIsTrue(isView);
    for (String answer : answers) {
      String answerTrim = answer.toLowerCase().replaceAll(" ", "").replaceAll("[\r\n]+", "");

      //新增的视图
      if (answerTrim.startsWith("createview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("createview") + 10, answerTrim.indexOf("asselect")), CheckStatus.INSERT.toString());
      }
      if (answerTrim.startsWith("createorreplaceview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("createorreplaceview") + 19, answerTrim.indexOf("asselect")), CheckStatus.INSERT.toString());
      }
      //修改的视图
      if (answerTrim.startsWith("alterview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("alterview") + 9, answerTrim.indexOf("asselect")), CheckStatus.UPDATE.toString());
      }
      //删除的视图
      if (answerTrim.startsWith("dropview")) {
        map.put(answerTrim.substring(answerTrim.indexOf("dropview") + 8, answerTrim.indexOf(";")), CheckStatus.DEL.toString());
      }
    }
    return map;

  }

  /**
   * @description: 比较标准答案和学生答案判断作答是否正确
   * @author:
   * @date: 2023/4/10 16:18
   * @param: [teacherFields 标准答案返回的视图结构, studentFields学生答案返回的视图结构]
   * @return: void
   **/
  private void compareView(List<TSceneField> teacherFields, List<TSceneField> studentFields) {
    //学生答案查询到的字段为空
    BusinessResponseEnum.NOTFOUNDVIEW.assertIsNotEmpty(studentFields);
    teacherFields.forEach(field -> {
      List<TSceneField> fieldList = studentFields.stream().filter(item -> item.getFieldName().equalsIgnoreCase(field.getFieldName())).collect(Collectors.toList());
      //校验字段不存在抛出异常
      BusinessResponseEnum.NOFIELDSFOUND.assertIsNotEmpty(fieldList, field.getFieldName());
      //答案中的对应字段
      TSceneField sceneField = fieldList.get(0);
      //比对答案中的字段
      fieldComparison(field, sceneField);
    });
  }

  private void fieldComparison(TSceneField field, TSceneField sceneField) {
    //判断字段类型是否相同
    BusinessResponseEnum.DIFFERENTFIELDTYPES.assertIsTrue(field.getFieldType().equalsIgnoreCase(sceneField.getFieldType())
      , field.getFieldName(), field.getFieldType(), sceneField.getFieldType());
    //判断字段长度是否相同
    if ("varchar".equalsIgnoreCase(field.getFieldType())) {
      BusinessResponseEnum.FIELDLENGTHISDIFFERENT.assertIsEquals(field.getFieldLength(), sceneField.getFieldLength(), field.getFieldName(), field.getFieldLength(), sceneField.getFieldLength());
    }
    //判断默认值是否相同
    BusinessResponseEnum.FIELDDEFAULTISDIFFERENT.assertIsEquals(field.getFieldDefault(), sceneField.getFieldDefault(), field.getFieldName(), field.getFieldDefault(), sceneField.getFieldDefault());
    //判断是否非空
    BusinessResponseEnum.WHETHERITISNOTEMPTYISDIFFERENT.assertIsEquals(field.getFieldNonNull(), sceneField.getFieldNonNull(), field.getFieldName(), field.getFieldNonNull(), sceneField.getFieldNonNull());
    //判断字段描述
    BusinessResponseEnum.FIELDDESCRIPTIONSDIFFER.assertIsEquals(field.getFieldComment(), sceneField.getFieldComment(), field.getFieldName(), field.getFieldComment(), sceneField.getFieldComment());
    //判断是否自增
    //BusinessResponseEnum.WHETHERSELFINCREASINGISDIFFERENT.assertIsEquals(field.getAutoIncrement(), tCheckField.getAutoIncrement(), field.getFieldName(), field.getAutoIncrement(), tCheckField.getAutoIncrement());
    //判断小数点位数
    if ("numeric".equalsIgnoreCase(sceneField.getFieldType())) {
      BusinessResponseEnum.DIFFERENTDECIMALPLACES.assertIsEquals(field.getDecimalNum(), sceneField.getDecimalNum(), field.getFieldName(), field.getDecimalNum(), sceneField.getDecimalNum());
    }
  }
}
