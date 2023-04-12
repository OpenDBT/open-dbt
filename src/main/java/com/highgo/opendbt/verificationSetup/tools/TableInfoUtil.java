package com.highgo.opendbt.verificationSetup.tools;

import com.alibaba.fastjson.JSONObject;
import com.highgo.opendbt.common.bean.DataTypeAndImg;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.DataTypeImgUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.EventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 提取表结构信息
 * @Title: TableInfoUtil
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/15 9:47
 */
@Component
public class TableInfoUtil {
  static Logger logger = LoggerFactory.getLogger(TableInfoUtil.class);
  @Autowired
  private RunAnswerService runAnswerService2;
  private static RunAnswerService runAnswerService;
  @Autowired
  private TSceneDetailService sceneDetailService2;
  private static TSceneDetailService sceneDetailService;

  @PostConstruct
  public void beforeInit() {
    runAnswerService = runAnswerService2;
    sceneDetailService = sceneDetailService2;
  }


  /**
   * @description: 生成相关结构查询sql，并查询表相关结构信息
   * @author:
   * @date: 2023/3/28 17:24
   * @param: [statement, connection, schemaConnection, tableName, event]
   * @return: java.sql.ResultSet
   **/
  public static ResultSet extractTableInfo(Statement statement, Connection connection, SchemaConnection schemaConnection, String tableName, TableInfoEvent event) {
    try {
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
        if (eventProcess != null) {
          String executeSql = eventProcess.execute(schemaConnection.getSchemaName(), tableName);
          //获取查询字段信息sql
          return statement.executeQuery(executeSql);
        }
      }
    } catch (Exception e) {
      logger.error("获取失败", e);
      throw new APIException(e.getMessage());
    }
    return null;
  }

  public static ResultSet extractTableInfo(Statement statement, Connection connection, SchemaConnection schemaConnection, String tableName, String shell, TableInfoEvent event) {
    try {
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        //新模式下执行答案
        statement.executeUpdate(shell);
        EventProcess eventProcess = EventProcessFactory.createEventProcess(event);
        if (eventProcess != null) {
          String executeSql = eventProcess.execute(schemaConnection.getSchemaName(), tableName);
          //获取查询字段信息sql
          return statement.executeQuery(executeSql);
        }
      }
    } catch (Exception e) {
      logger.error("获取失败", e);
      throw new APIException(e.getMessage());
    }
    return null;
  }

  /**
   * @description: 生成相关答案sql，并执行
   * @author:
   * @date: 2023/3/28 17:26
   * @param: [statement, connection, schemaConnection, tableName, event]
   * @return: java.sql.ResultSet
   **/
  public static void extractAnswer(UserInfo userInfo, int sceneId, int exerciseId, StringBuilder builder) {
    Statement statement = null;
    Connection connection = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    try {
      //初始化场景并开启新的模式
      runAnswerService.getSchemaConnection(userInfo, sceneId, exerciseId, schemaConnection, 0);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        //新模式下执行答案
        statement.executeUpdate(builder.toString());
      }
    } catch (Exception e) {
      logger.error("获取失败", e);
      throw new APIException(e.getMessage());
    } finally {
      CloseUtil.close(statement);
      CloseUtil.close(connection);
      //删除模式
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
    }
  }


  //提取表相关信息
  public static <T> List<T> getInfo(long sceneDetailId, UserInfo userInfo, int exerciseId, TableInfoEvent event, Class<T> clazz) {
    List<T> list = null;
    TSceneDetail sceneDetail = sceneDetailService.getById(sceneDetailId);
    SchemaConnection schemaConnection = new SchemaConnection();
    Statement statement = null;
    Connection connection = null;
    //新建模式，新模式下执行初始化场景
    try {
      runAnswerService.getSchemaConnection(userInfo, sceneDetail.getSceneId(), exerciseId, schemaConnection, 0);
      //新模式下提取字段信息
      ResultSet resultSetField = extractTableInfo(statement, connection, schemaConnection, sceneDetail.getTableName(), event);
      //转换为试题对象
      ResultSetMapper<T> constraintMapper = new ResultSetMapper<>();
      list = constraintMapper.mapRersultSetToObject(resultSetField, clazz);
      logger.info("结果" + JSONObject.toJSONString(list));
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
   * @description:
   * @author:
   * @date: 2023/4/10 16:02
   * @param: [sceneId 场景id, shell 答案sql, viewName 视图名称, userInfo 用户信息, exerciseId 习题id, event 查询哪些信息, clazz 返回类型]
   * @return: java.util.List<T>
   **/
  //提取相关信息
  public static <T> List<T> getInfo(UserInfo userInfo, int sceneId, int exerciseId, String shell, String viewName, TableInfoEvent event, Class<T> clazz) {
    List<T> list = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    Statement statement = null;
    Connection connection = null;
    //新建模式，新模式下执行初始化场景
    try {
      runAnswerService.getSchemaConnection(userInfo, sceneId, exerciseId, schemaConnection, 0);
      //新模式下执行答案并提取字段信息
      ResultSet resultSetField = extractTableInfo(statement, connection, schemaConnection, viewName, shell, event);
      //转换为试题对象
      ResultSetMapper<T> constraintMapper = new ResultSetMapper<>();
      list = constraintMapper.mapRersultSetToObject(resultSetField, clazz);
      logger.info("结果" + JSONObject.toJSONString(list));
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

  //转换结果集
  public static ResultSetInfo resultSetConvertList(ResultSet resultSet) {
    ResultSetInfo resultSetInfo = new ResultSetInfo();

    List<String> columnList = resultSetInfo.getColumnList();
    List<DataTypeAndImg> dataTypeAndImgList = resultSetInfo.getDataTypeAndImgList();
    List<Map<Object, Object>> dataList = resultSetInfo.getDataList();
    try {
      setColumnAndData(resultSet, columnList, dataTypeAndImgList, dataList);
    } catch (Exception e) {
      throw new APIException(e.getMessage());
    }

    resultSetInfo.setColumnNumber(columnList.size());
    resultSetInfo.setRowNumber(dataList.size());

    return resultSetInfo;
  }

  private static void setColumnAndData(ResultSet
                                         resultSet, List<String> columnList, List<DataTypeAndImg> dataTypeAndImgList, List<Map<Object, Object>> resultList) throws
    Exception {

    int columnCount = resultSet.getMetaData().getColumnCount();

    //添加字段名、数据类型以及数据类型图标
    columnList.add(Message.get("SerialNumber"));
    dataTypeAndImgList.add(new DataTypeAndImg(Message.get("SerialNumber"), null));
    for (int i = 1; i <= columnCount; i++) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      columnList.add(metaData.getColumnName(i));
      dataTypeAndImgList.add(new DataTypeAndImg(metaData.getColumnTypeName(i), DataTypeImgUtil.getDataTypeImgUrl(metaData.getColumnTypeName(i))));
    }

    // 前端需要的唯一标识列
    int rowKey = 0;
    while (resultSet.next()) {
      Map<Object, Object> resultSetMap = new LinkedHashMap<Object, Object>();
      rowKey++;
      resultSetMap.put(0, rowKey);
      for (int i = 1; i <= columnCount; i++) {
        resultSetMap.put(i, resultSet.getObject(i));
      }
      resultList.add(resultSetMap);
    }
  }


}
