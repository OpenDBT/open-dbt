package com.highgo.opendbt.verificationSetup.tools;

import com.alibaba.fastjson.JSONObject;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.test.DataTableHelper;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 函数相关工具
 * @Title: FunctionUtil
 * @Package com.highgo.opendbt.verificationSetup.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/18 10:24
 */
@Component
public class FunctionUtil {
  static Logger logger = LoggerFactory.getLogger(FunctionUtil.class);

  @Autowired
  private RunAnswerService runAnswerService2;
  private static RunAnswerService runAnswerService;


  @PostConstruct
  public void beforeInit() {
    runAnswerService = runAnswerService2;
  }


  public static void executeFunctionSql(UserInfo loginUser, TestRunModel model, ResponseModel responseModel) {
    Connection connection = null;
    CallableStatement statement = null;
    Statement functionStatement = null;
    SchemaConnection schemaConnection = new SchemaConnection();

    List<List<Map<String, Object>>> lists = new ArrayList<>();

    try {
      // 初始化脚本并获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, model.getSceneId(), model.getExerciseId(), schemaConnection, 0);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        connection.setAutoCommit(false);
        functionStatement = connection.createStatement();
        long startTime = System.currentTimeMillis();
        functionStatement.executeUpdate(model.getStandardAnswer());

        if(StringUtils.isNotBlank(model.getVerySql())){
          statement = connection.prepareCall(model.getVerySql());
          //新模式下执行答案
          boolean execute = statement.execute();
          while (execute) {
            ResultSet resultSet = statement.getResultSet();//取得第一个结果集
            List<Map<String, Object>> resultList = DataTableHelper.rs2MapList(resultSet);
            logger.info("result=" + JSONObject.toJSONString(resultList));
            lists.add(resultList);
            execute = statement.getMoreResults();//继续去取结果集，若还还能取到结果集，则bl=true了。然后回去循环。
            resultSet.close();
          }
        }
        long endTime = System.currentTimeMillis();
        responseModel.setAnswerExecuteTime((int) (endTime - startTime));
        responseModel.setFunctionResult(lists);
        connection.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(functionStatement);
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
  }
  public static void executeSql(UserInfo loginUser, TestRunModel model, ResponseModel responseModel) {
    Connection connection = null;
    CallableStatement statement = null;
    Statement functionStatement = null;
    SchemaConnection schemaConnection = new SchemaConnection();

    try {
      // 初始化脚本并获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, model.getSceneId(), model.getExerciseId(), schemaConnection, 0);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        functionStatement = connection.createStatement();
        long startTime = System.currentTimeMillis();
        functionStatement.execute(model.getStandardAnswer());
        if(StringUtils.isNotBlank(model.getVerySql())){
          FunctionUtil.testDML(model.getVerySql(), responseModel, functionStatement);
        }
        long endTime = System.currentTimeMillis();
        responseModel.setAnswerExecuteTime((int) (endTime - startTime));
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(functionStatement);
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
  }

  public static void testDML(String sql, ResponseModel model, Statement statement) throws Exception {
    // 区分是查询还是其他，查询返回结果集，其他返回影响行数
    if (sql.trim().toLowerCase().startsWith("select")) {
      model.setSelect(true);
      ResultSet resultSet = statement.executeQuery(sql);
      //解析结果集
      ResultSetInfo resultSetInfo = runAnswerService.resultSetConvertList(resultSet);
      model.setDatatype(resultSetInfo.getDataTypeAndImgList());
      model.setColumn(resultSetInfo.getColumnList());
      model.setResult(resultSetInfo.getDataList());
      model.setResultSetInfo(resultSetInfo);
    } else {
      int updateRow = statement.executeUpdate(sql);
      model.setUpdateRow(updateRow);
    }
  }

  public static <T> List<T> getViewInfo(UserInfo userInfo, TestRunModel model, String viewName, TableInfoEvent event, Class<TSceneField> clazz, ResponseModel resultMap) {
    //视图结构
    List<T> list = null;
    //查询结果
    ResultSetInfo studentResult = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    Statement statement = null;
    Connection connection = null;

    try {
      //新建模式，新模式下执行初始化场景
      runAnswerService.getSchemaConnection(userInfo, model.getSceneId(), model.getExerciseId(), schemaConnection, 0);

      //新模式下执行答案并提取字段信息
      connection = schemaConnection.getConnection();
      statement = connection.createStatement();
      //新模式下执行答案
      statement.execute(model.getStandardAnswer());

      //获取视图结构信息
      EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
      if (eventProcess != null) {
        String executeSql = eventProcess.execute(schemaConnection.getSchemaName(), viewName);
        //获取查询字段信息sql
        ResultSet resultSet = statement.executeQuery(executeSql);
        //转换为试题对象
        ResultSetMapper<T> constraintMapper = new ResultSetMapper<>();
        list = constraintMapper.mapRersultSetToObject(resultSet, clazz);
        logger.info("结果" + JSONObject.toJSONString(list));

      }
      //根据校验sql查询
      if (StringUtils.isNotBlank(model.getVerySql())) {
        ResultSet resultSet = statement.executeQuery(model.getVerySql());
        studentResult = runAnswerService.resultSetConvertList(resultSet);
        resultMap.setSelect(true);
        resultMap.setColumn(studentResult.getColumnList());
        resultMap.setDatatype(studentResult.getDataTypeAndImgList());
        resultMap.setResult(studentResult.getDataList());
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.error("提取失败:", e);
      throw new APIException("提取失败");
    } finally {
      CloseUtil.close(statement);
      CloseUtil.close(connection);
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
    }
    return list;
  }

  /**
   * 对比结果集
   *
   * @param source 老师的结果集
   * @param target 学生的结果集
   * @return boolean
   */
  public static void compareResultSet(ResultSetInfo source, ResultSetInfo target) {
    //结果集为空抛出异常
    BusinessResponseEnum.RESULTSETISEMPTY.assertIsFalse(null == target || null == source);

    // 比较行数
    BusinessResponseEnum.ROWNUMBERDIFF.assertIsTrue(target.getRowNumber() == source.getRowNumber(), source.getRowNumber());

    // 比较列数
    BusinessResponseEnum.COLUMNNUMBERDIFF.assertIsTrue(target.getColumnNumber() == source.getColumnNumber(), source.getColumnNumber() - 1);

    // 比较字段名
    List<String> sourceColumnList = source.getColumnList();
    List<String> targetColumnList = target.getColumnList();
    for (int i = 1; i < sourceColumnList.size(); i++) {
      //字段名称为空
      BusinessResponseEnum.COLUMNNAMENULL.assertIsFalse(null == sourceColumnList.get(i) || null == sourceColumnList.get(i));
      //字段名称不一致
      BusinessResponseEnum.COLUMNNAMEDIFF.assertIsTrue(sourceColumnList.get(i).equals(targetColumnList.get(i)));
    }

    // 比较数据
    List<Map<Object, Object>> sourceDataList = source.getDataList();
    List<Map<Object, Object>> targetDataList = target.getDataList();
    for (int i = 0; i < sourceDataList.size(); i++) {
      Map<Object, Object> sourceData = sourceDataList.get(i);
      Map<Object, Object> targetData = targetDataList.get(i);
      BusinessResponseEnum.DATADIFF.assertIsTrue(compareData(sourceData, targetData));
    }
  }


  /**
   * 对比两条数据
   *
   * @param sourceData 老师的数据
   * @param targetData 学生的数据
   * @return boolean
   */
  private static boolean compareData(Map<Object, Object> sourceData, Map<Object, Object> targetData) {
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
