package com.highgo.opendbt.test;

import com.alibaba.fastjson.JSONObject;
import com.highgo.opendbt.common.bean.DataTypeAndImg;
import com.highgo.opendbt.common.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 函数、存储过程 结果获取
 * @Title: Demo
 * @Package com.highgo.opendbt.test
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/9 16:42
 */
public class Demo {
  static Logger logger = LoggerFactory.getLogger(Demo.class);
  private static DbUtil dbUtil = new DbUtil();

  /**
   * 调用存储过程，通过id查询bookName
   *
   * @param id
   * @return
   * @throws Exception
   */
  public static void getBookNameById(String id) throws Exception {

    Connection con = dbUtil.getCon();
    try {
      con.setAutoCommit(false);
      String sql = "select  my_function3(5);";
      List<List<Map<String, Object>>> lists = executeSql(sql, con);
      logger.info("ALL_RESULT=" + JSONObject.toJSONString(lists));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (con != null) {
        con.close();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    getBookNameById("444444");
  }


  public static List<List<Map<String, Object>>> executeSql(String sql, Connection con) throws Exception {
    List<List<Map<String, Object>>> lists = new ArrayList<>();
    con.setAutoCommit(false);
    CallableStatement statement = con.prepareCall(sql);
    try {
      boolean execute = statement.execute();
      while (execute) {
        ResultSet resultSet = statement.getResultSet();//取得第一个结果集
        List<Map<String, Object>> resultList = DataTableHelper.rs2MapList(resultSet);
        logger.info("result=" + JSONObject.toJSONString(resultList));
        lists.add(resultList);
        execute = statement.getMoreResults();//继续去取结果集，若还还能取到结果集，则bl=true了。然后回去循环。
      }
    } catch (Exception e) {
      throw new APIException("执行sql获取结果异常");
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
    return lists;

  }
}
