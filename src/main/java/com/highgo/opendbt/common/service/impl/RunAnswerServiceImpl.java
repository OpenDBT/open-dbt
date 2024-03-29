package com.highgo.opendbt.common.service.impl;

import com.highgo.opendbt.common.bean.DataSourceBean;
import com.highgo.opendbt.common.bean.DataTypeAndImg;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.DataTypeImgUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.common.utils.PostgresqlKeyWord;
import com.highgo.opendbt.course.domain.model.Scene;
import com.highgo.opendbt.course.mapper.SceneMapper;
import com.highgo.opendbt.publicLibrary.mapper.PublicSceneMapper;
import com.highgo.opendbt.publicLibrary.model.PublicScene;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RunAnswerServiceImpl implements RunAnswerService {

  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SceneMapper sceneMapper;

  @Autowired
  private PublicSceneMapper publicSceneMapper;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DataSourceBean dataSourceBean;

  /**
   * 初始化脚本，返回schema和指定schema名的连接
   */
  @Override
  public void getSchemaConnection(UserInfo loginUser, int sceneId, Long exerciseId, SchemaConnection schemaConnection, int exerciseSource) throws Exception {
    // 根据场景id获取场景信息
    String initShell = "";
    if (sceneId != -1 && sceneId != 0) {
      // 习题来源exerciseSource为1是公题库
      if (exerciseSource == 1) {
        PublicScene scene = publicSceneMapper.getSceneDetail(sceneId);
        if (null == scene) {
          throw new Exception(Message.get("GetSceneFail"));
        }
        initShell = scene.getInitShell();
      } else {
        Scene scene = sceneMapper.getSceneDetail(sceneId);
        if (null == scene) {
          throw new Exception(Message.get("GetSceneFail"));
        }
        initShell = scene.getInitShell();
      }
    }
    Statement statement = null;

    try {
      // 拼接具有唯一标识的schema名，并创建schema
      String schemaName = "schema_" + exerciseSource + "_" + loginUser.getCode() + "_" + exerciseId + "_" + Thread.currentThread().getId();
      logger.info("schema="+schemaName);
      schemaName = PostgresqlKeyWord.convertorToPostgresql(schemaName);
      schemaConnection.setSchemaName(schemaName);
      jdbcTemplate.execute("create schema " + schemaName + ";");
      logger.info("create schema success="+schemaName);

      // 连接串后拼接schema名，可连接指定schema
      String schemaUrl = dataSourceBean.url + "?currentSchema=" + schemaName;

      // 获取指定schema的连接
      Class.forName(dataSourceBean.driverClassName);
      Connection connection = DriverManager.getConnection(schemaUrl, dataSourceBean.username, dataSourceBean.password);

      schemaConnection.setConnection(connection);
      if (sceneId != -1 && sceneId != 0) {
        statement = connection.createStatement();
        // 执行初始化场景的sql脚本
        statement.executeUpdate(initShell);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(Message.get("InitSceneFail", e.getMessage()));
    } finally {
      CloseUtil.close(statement);
    }
  }
  /**
   * 初始化脚本,并删除插入的数据，返回schema和指定schema名的连接
   */
  @Override
  public void getSchemaConnection(UserInfo loginUser, int sceneId, Long exerciseId, SchemaConnection schemaConnection, int exerciseSource,boolean delInsert) throws Exception {
    // 根据场景id获取场景信息
    String initShell = "";
    if (sceneId != -1 && sceneId != 0) {
      // 习题来源exerciseSource为1是公题库
      if (exerciseSource == 1) {
        PublicScene scene = publicSceneMapper.getSceneDetail(sceneId);
        if (null == scene) {
          throw new Exception(Message.get("GetSceneFail"));
        }
        initShell = scene.getInitShell();
      } else {
        Scene scene = sceneMapper.getSceneDetail(sceneId);
        if (null == scene) {
          throw new Exception(Message.get("GetSceneFail"));
        }
        initShell = scene.getInitShell();
        if(delInsert){
          initShell= delInsert(initShell);
        }
      }
    }
    Statement statement = null;

    try {
      // 拼接具有唯一标识的schema名，并创建schema
      String schemaName = "schema_" + exerciseSource + "_" + loginUser.getCode() + "_" + exerciseId + "_" + Thread.currentThread().getId();
      schemaName = PostgresqlKeyWord.convertorToPostgresql(schemaName);

      schemaConnection.setSchemaName(schemaName);
      jdbcTemplate.execute("create schema " + schemaName + ";");
      logger.info("create schema success");

      // 连接串后拼接schema名，可连接指定schema
      String schemaUrl = dataSourceBean.url + "?currentSchema=" + schemaName;

      // 获取指定schema的连接
      Class.forName(dataSourceBean.driverClassName);
      Connection connection = DriverManager.getConnection(schemaUrl, dataSourceBean.username, dataSourceBean.password);

      schemaConnection.setConnection(connection);
      if (sceneId != -1 && sceneId != 0) {
        statement = connection.createStatement();
        // 执行初始化场景的sql脚本
        statement.executeUpdate(initShell);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(Message.get("InitSceneFail", e.getMessage()));
    } finally {
      CloseUtil.close(statement);
    }
  }
private String delInsert(String sql){
  String[] sqlStatements = sql.split(";");
  StringBuilder output = new StringBuilder();
  for (String statement : sqlStatements) {
    String trimmedStatement = statement.trim();
    if (!trimmedStatement.toUpperCase().replaceAll(" ","").startsWith("INSERTINTO")) {
      output.append(trimmedStatement).append(";");
      if(trimmedStatement.toUpperCase().replaceAll(" ","").startsWith("--")){
        output.append("\n\n");
      }
    }
  }

  return output.toString();
}
  private void setColumnAndData(ResultSet resultSet, List<String> columnList, List<DataTypeAndImg> dataTypeAndImgList, List<Map<Object, Object>> resultList) throws Exception {

    int columnCount = resultSet.getMetaData().getColumnCount();

    //添加字段名、数据类型以及数据类型图标
    columnList.add(Message.get("SerialNumber"));
    dataTypeAndImgList.add(new DataTypeAndImg(Message.get("SerialNumber"), null));
    for (int i = 1; i <= columnCount; i++) {
      columnList.add(resultSet.getMetaData().getColumnName(i));
      dataTypeAndImgList.add(new DataTypeAndImg(resultSet.getMetaData().getColumnTypeName(i), DataTypeImgUtil.getDataTypeImgUrl(resultSet.getMetaData().getColumnTypeName(i))));
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

  /**
   * 获取ResultSet结果集的字段名和数据
   */
  @Override
  public void getResultSetColumnAndData(ResultSet resultSet, List<String> columnList, List<DataTypeAndImg> dataTypeAndImgList, List<Map<Object, Object>> resultList) throws Exception {
    setColumnAndData(resultSet, columnList, dataTypeAndImgList, resultList);
  }

  /**
   * 把ResultSet结果集数据转成list
   */
  @Override
  public ResultSetInfo resultSetConvertList(ResultSet resultSet) throws Exception {
    ResultSetInfo resultSetInfo = new ResultSetInfo();

    List<String> columnList = resultSetInfo.getColumnList();
    List<DataTypeAndImg> dataTypeAndImgList = resultSetInfo.getDataTypeAndImgList();
    List<Map<Object, Object>> dataList = resultSetInfo.getDataList();
    setColumnAndData(resultSet, columnList, dataTypeAndImgList, dataList);

    resultSetInfo.setColumnNumber(columnList.size());
    resultSetInfo.setRowNumber(dataList.size());

    return resultSetInfo;
  }

  /**
   * 删除schema
   */
  @Override
  public void dropSchema(String schemaName) {
    logger.info("到达此处schemaName="+"drop schema IF EXISTS " + schemaName + " cascade;");
    try {
      if (null != schemaName) {
        jdbcTemplate.execute("drop schema IF EXISTS " + schemaName + " cascade;");
        logger.info("drop schema success");
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("drop schema error",e);
    }
  }

  /**
   * 返回schema和指定schema名的连接，不执行脚本
   */
  @Override
  public void getSchemaConnection(UserInfo loginUser, SchemaConnection schemaConnection) throws Exception {
    // 拼接具有唯一标识的schema名，并创建schema
    String schemaName = "schema_" + loginUser.getCode();
    schemaName = PostgresqlKeyWord.convertorToPostgresql(schemaName);

    schemaConnection.setSchemaName(schemaName);
    jdbcTemplate.execute("create schema " + schemaName + ";");
    logger.info("create schema success");

    // 连接串后拼接schema名，可连接指定schema
    String schemaUrl = dataSourceBean.url + "?currentSchema=" + schemaName;

    // 获取指定schema的连接
    Class.forName(dataSourceBean.driverClassName);
    Connection connection = DriverManager.getConnection(schemaUrl, dataSourceBean.username, dataSourceBean.password);

    schemaConnection.setConnection(connection);
  }

}
