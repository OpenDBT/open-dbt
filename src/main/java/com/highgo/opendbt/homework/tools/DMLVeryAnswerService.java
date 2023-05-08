package com.highgo.opendbt.homework.tools;

import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.constant.Constant;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.course.domain.model.SceneDetail;
import com.highgo.opendbt.course.mapper.SceneDetailMapper;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.domain.model.StudentAndTeacherResult;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.score.domain.model.UpdateRowAndResultSetTO;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: DML习题答案验证
 * @Title: VeryAnswerUtil
 * @Package com.highgo.opendbt.homework.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/20 18:09
 */
@Service
public class DMLVeryAnswerService {
  static Logger logger = LoggerFactory.getLogger(DMLVeryAnswerService.class);


  @Autowired
  private RunAnswerService runAnswerService;
  @Autowired
  private SceneDetailMapper sceneDetailMapper;


  public boolean verifyAnswer(UserInfo loginUser, TNewExercise exercise, Score score, SubmitResult result, int exerciseSource, boolean isSaveSubmitData) {
    if (exercise.getStandardAnswser().toLowerCase().startsWith("select")) {
      // 验证查询
      StudentAndTeacherResult studentAndTeacherResult = getStudentAndTeacherResult(loginUser,
        exercise.getSceneId(), exercise.getId(), exercise.getStandardAnswser(),
        score.getAnswer(), result, exerciseSource, isSaveSubmitData);
      if (studentAndTeacherResult.getIsCompare()) {
        return compareResultSet(studentAndTeacherResult.getTeacherResult(), studentAndTeacherResult.getStudentResult(), result);
      } else {
        return false;
      }
    } else {
      // 执行学生答案并返回影响行数和结果集
      UpdateRowAndResultSetTO studentData = getUpdateRowAndResultSet(loginUser, exercise.getSceneId(),
        exercise.getId(), score.getAnswer(), true, result, exerciseSource);
      if (studentData.getUpdateRow() == -1) {
        return false;
      }

      // 执行参考答案并返回影响行数和结果集
      UpdateRowAndResultSetTO teacherData = getUpdateRowAndResultSet(loginUser,
        exercise.getSceneId(), exercise.getId(), exercise.getStandardAnswser(),
        true, result, exerciseSource);

      // 对比影响行数是否一样，结果集map大小是否一样
      if (studentData.getUpdateRow() != teacherData.getUpdateRow()
        || studentData.getResultSetInfoMap().size() != teacherData.getResultSetInfoMap().size()) {
        return false;
      }

      // 结果集map大小一样，其中一个等于0，无需再对比
      if (teacherData.getResultSetInfoMap().size() == 0) {
        return true;
      }

      // 有多个结果集，通过表名，对比结果集
      for (Map.Entry<String, ResultSetInfo> entry : teacherData.getResultSetInfoMap().entrySet()) {
        Map<String, ResultSetInfo> studentResultSetListMap = studentData.getResultSetInfoMap();
        if (studentResultSetListMap.get(entry.getKey()) == null) {
          return false;
        } else {
          if (!compareResultSet(entry.getValue(), studentResultSetListMap.get(entry.getKey()), null)) {
            return false;
          }
        }
      }
      return true;
    }
  }


  private StudentAndTeacherResult getStudentAndTeacherResult(UserInfo loginUser, int sceneId, int exerciseId, String referAnswer, String studentAnswer, SubmitResult result, int exerciseSource, boolean isSaveSubmitData) {
    StudentAndTeacherResult studentAndTeacherResult = new StudentAndTeacherResult();
    Connection connection = null;
    Statement statement = null;
    ResultSet teacherResultSet = null;
    ResultSet studentResultSet = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    try {
      // 初始化场景，获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, sceneId, exerciseId, schemaConnection, exerciseSource);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();

        boolean isGetTeacherResultSet = true;

        // 执行学生的答案并把结果集转换成list
        try {
          long startTime = System.currentTimeMillis();
          studentResultSet = statement.executeQuery(studentAnswer);
          long endTime = System.currentTimeMillis();
          result.setAnswerExecuteTime((int) (endTime - startTime));

          ResultSetInfo studentResult = runAnswerService.resultSetConvertList(studentResultSet);
          studentAndTeacherResult.setStudentResult(studentResult);

          result.setExecuteRs(true); // 执行SQL是否正确

          if (!isSaveSubmitData) {
            Map<String, Object> studentResultMap = result.getStudentResultMap();
            studentResultMap.put(Constant.TEST_RUN_DATATYPE, studentResult.getDataTypeAndImgList());
            studentResultMap.put(Constant.TEST_RUN_COLUMN, studentResult.getColumnList());
            studentResultMap.put(Constant.TEST_RUN_RESULT, studentResult.getDataList());
          }
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          // 如果学生的出现异常，就不需要对比结果集，并把jdbc日志返回
          isGetTeacherResultSet = false;
          studentAndTeacherResult.setIsCompare(false);
          result.setLog(e.getMessage());
        }

        // 学生的答案和结果集出错不需要再执行老师的答案
        if (isGetTeacherResultSet) {
          try {
            // 执行老师的答案并把结果集转换成list
            teacherResultSet = statement.executeQuery(referAnswer);
            ResultSetInfo teacherResult = runAnswerService.resultSetConvertList(teacherResultSet);
            studentAndTeacherResult.setTeacherResult(teacherResult);
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
            BusinessResponseEnum.ANSWERISPROBLEM.assertIsTrue(false, e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(teacherResultSet);
      CloseUtil.close(studentResultSet);
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
    return studentAndTeacherResult;
  }

  private UpdateRowAndResultSetTO getUpdateRowAndResultSet(UserInfo loginUser, int sceneId, int exerciseId, String answer, boolean isStudent, SubmitResult result, int exerciseSource) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    SchemaConnection schemaConnection = new SchemaConnection();

    UpdateRowAndResultSetTO updateRowAndResultSet = new UpdateRowAndResultSetTO();

    try {
      // 初始化场景，获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, sceneId, exerciseId, schemaConnection, exerciseSource);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();

        // 学生的答案报错需要记录，老师的答案保存直接抛异常所以需要区分
        if (isStudent) {
          try {
            int updateRow = statement.executeUpdate(answer);
            updateRowAndResultSet.setUpdateRow(updateRow);
            result.setExecuteRs(true); // 执行SQL是否正确
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setLog(e.getMessage());
            return updateRowAndResultSet;
          }
        } else {
          try {
            int updateRow = statement.executeUpdate(answer);
            updateRowAndResultSet.setUpdateRow(updateRow);
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
            BusinessResponseEnum.ANSWERISPROBLEM.assertIsTrue(false, e.getMessage());
          }

        }

        // 执行完答案后，获取场景所有结果集
        try {
          Map<String, ResultSetInfo> resultSetListMap = new HashMap<>();

          List<SceneDetail> sceneDetailList;
          if (exerciseSource == 1) {
            sceneDetailList = sceneDetailMapper.getPublicSceneDetailById(sceneId);
          } else {
            sceneDetailList = sceneDetailMapper.getSceneDetailById(sceneId);
          }

          for (SceneDetail sceneDetail : sceneDetailList) {
            resultSet = statement.executeQuery("select * from " + sceneDetail.getTableName());

            ResultSetInfo resultSetInfo = runAnswerService.resultSetConvertList(resultSet);

            resultSetListMap.put(sceneDetail.getTableName(), resultSetInfo);
          }
          updateRowAndResultSet.setResultSetInfoMap(resultSetListMap);
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          BusinessResponseEnum.EXECUTANSWERGETRESULTFILE.assertIsTrue(false, e.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(resultSet);
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
    return updateRowAndResultSet;
  }

  /**
   * 对比结果集
   *
   * @param source 老师的结果集
   * @param target 学生的结果集
   * @return boolean
   */
  private boolean compareResultSet(ResultSetInfo source, ResultSetInfo target, SubmitResult result) {

    if (null == target || null == source) {
      return false;
    }

    // 比较行数
    if (target.getRowNumber() != source.getRowNumber()) {
      if (null != result) {
        result.setErrorMessage(Message.get("RowNumberDiff", source.getRowNumber()));
      }
      return false;
    }

    // 比较列数
    if (target.getColumnNumber() != source.getColumnNumber()) {
      if (null != result) {
        result.setErrorMessage(Message.get("ColumnNumberDiff", source.getColumnNumber() - 1));
      }
      return false;
    }

    // 比较字段名
    List<String> sourceColumnList = source.getColumnList();
    List<String> targetColumnList = target.getColumnList();
    for (int i = 1; i < sourceColumnList.size(); i++) {
      if (null == sourceColumnList.get(i) || null == sourceColumnList.get(i)) {
        return false;
      }

      if (!sourceColumnList.get(i).equals(targetColumnList.get(i))) {
        if (null != result) {
          result.setErrorMessage(Message.get("ColumnNameDiff"));
        }
        return false;
      }
    }

    // 比较数据
    List<Map<Object, Object>> sourceDataList = source.getDataList();
    List<Map<Object, Object>> targetDataList = target.getDataList();
    for (int i = 0; i < sourceDataList.size(); i++) {
      Map<Object, Object> sourceData = sourceDataList.get(i);
      Map<Object, Object> targetData = targetDataList.get(i);
      if (!compareData(sourceData, targetData)) {
        if (null != result) {
          result.setErrorMessage(Message.get("DataDiff"));
        }
        return false;
      }
    }
    return true;
  }


  /**
   * 对比两条数据
   *
   * @param sourceData 老师的数据
   * @param targetData 学生的数据
   * @return boolean
   */
  private boolean compareData(Map<Object, Object> sourceData, Map<Object, Object> targetData) {
    for (Map.Entry<Object, Object> entry : sourceData.entrySet()) {
      // 如果该字段老师和学生的数据都等于null，说明该字段数据就是null，所以也是相同的
      if (null == entry.getValue() && null == targetData.get(entry.getKey())) {
        continue;
      }

      // 前面已经排除了都是null的情况，老师和学生有一个是null就是不相同的数据
      if (null == entry.getValue() || null == targetData.get(entry.getKey())) {
        return false;
      }

      // 如果都不等于null，对比数据是否一样
      if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
        return false;
      }
    }
    return true;
  }
}
