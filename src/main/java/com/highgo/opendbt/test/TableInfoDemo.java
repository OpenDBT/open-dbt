package com.highgo.opendbt.test;

/**
 * @Description: 表结果相关测试
 * @Title: TableInfoDemo
 * @Package com.highgo.opendbt.test
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/15 14:29
 */

import com.alibaba.fastjson.JSONObject;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.course.domain.model.SceneDetail;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.tools.ResultSetMapper;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static com.highgo.opendbt.verificationSetup.tools.TableInfoUtil.extractTableInfo;

@CrossOrigin
@RestController
@RequestMapping("/tableInfoDemo")
@Api(tags = "测试字段查询关接口")
public class TableInfoDemo {
  Logger logger = LoggerFactory.getLogger(getClass());
  private static DbUtil dbUtil = new DbUtil();
  @Autowired
  private RunAnswerService runAnswerService;

  @GetMapping("/testTableField")
  @ApiOperation(value = "testTableField")
  public void testTableField() throws Exception {
    SchemaConnection schemaConnection = new SchemaConnection();
    Statement statement = null;
    Connection connection = null;

    try {
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //新建模式，新模式下执行初始化场景
      runAnswerService.getSchemaConnection(userInfo, 92, 0L, schemaConnection, 0);
      //场景参数设置
      SceneDetail sceneDetail = new SceneDetail();
      sceneDetail.setTableName("t_scene_constraint");

      //新模式下提表信息
      ResultSet resultSetDetail = extractTableInfo(statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("TABLE"));
      //转换为试题对象
      ResultSetMapper<TCheckDetail> resultSetMapperDetail = new ResultSetMapper<>();
      List<TCheckDetail> detailObject = resultSetMapperDetail.mapRersultSetToObject(resultSetDetail, TCheckDetail.class);
      logger.info("表信息"+JSONObject.toJSONString(detailObject));


      //新模式下提取约束信息
      ResultSet resultSet = extractTableInfo( statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("CONSTRAINT"));
      //转换为试题对象
      ResultSetMapper<TSceneConstraint> resultSetMapper = new ResultSetMapper<>();
      List<TSceneConstraint> rersultSetToObject = resultSetMapper.mapRersultSetToObject(resultSet, TSceneConstraint.class);
      logger.info("约束"+JSONObject.toJSONString(rersultSetToObject));

      //新模式下提取字段信息
      ResultSet resultSetField = extractTableInfo(statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("FIELD"));
      //转换为试题对象
      ResultSetMapper<TSceneField> resultSetMapperField = new ResultSetMapper<>();
      List<TSceneField> fieldObject = resultSetMapperField.mapRersultSetToObject(resultSetField, TSceneField.class);
      logger.info("字段"+JSONObject.toJSONString(fieldObject));

      //新模式下提取索引信息
      ResultSet resultSetIndex = extractTableInfo(statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("INDEX"));
      //转换为试题对象
      ResultSetMapper<TSceneIndex> resultSetMapperIndex = new ResultSetMapper<>();
      List<TSceneIndex> indexObject = resultSetMapperIndex.mapRersultSetToObject(resultSetIndex, TSceneIndex.class);
      logger.info("索引"+JSONObject.toJSONString(indexObject));


      //新模式下提取外键信息
      ResultSet resultSetFK = extractTableInfo(statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("FOREIGN_KEY"));
      //转换为试题对象
      ResultSetMapper<TSceneFk> resultSetMapperFK = new ResultSetMapper<>();
      List<TSceneFk> fkObject = resultSetMapperFK.mapRersultSetToObject(resultSetFK, TSceneFk.class);
      logger.info("外键"+JSONObject.toJSONString(fkObject));


      //新模式下提取序列信息
      ResultSet resultSetSeq = extractTableInfo(statement, connection,schemaConnection, sceneDetail.getTableName(), TableInfoEvent.valueOf("SEQUENCE"));
      //转换为试题对象
      ResultSetMapper<TSceneSeq> resultSetMapperSeq = new ResultSetMapper<>();
      List<TSceneSeq> seqObject = resultSetMapperSeq.mapRersultSetToObject(resultSetSeq, TSceneSeq.class);
      logger.info("序列"+JSONObject.toJSONString(seqObject));

    } catch (Exception e) {
      logger.error("查询字段信息错误", e);
      throw new APIException(e.getMessage());
    } finally {
      String name=schemaConnection.getSchemaName();
      dbUtil.close(statement, connection);
      runAnswerService.dropSchema(name);
    }

  }
}
