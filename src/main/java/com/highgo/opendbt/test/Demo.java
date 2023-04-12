package com.highgo.opendbt.test;

import com.alibaba.fastjson.JSONObject;
import com.highgo.opendbt.common.bean.DataTypeAndImg;

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

  private static DbUtil dbUtil = new DbUtil();

  /**
   * 调用存储过程，通过id查询bookName
   *
   * @param id
   * @return
   * @throws Exception
   */
  private static void getBookNameById(String id) throws Exception {
    java.util.List<String> columnList = new ArrayList<>();
    List<DataTypeAndImg> dataTypeAndImgList = new ArrayList<>();
    List<Map<String, Object>> resultList = new ArrayList<>();
    Connection con = dbUtil.getCon();
    con.setAutoCommit(false);
    String sql = "select  test_p2();";
    CallableStatement cstmt = con.prepareCall(sql);
     //cstmt.setObject(1, 1);//设置第一个参数
    // cstmt.setObject(2, 2);//设置第一个参数
    //cstmt.registerOutParameter(1, Types.INTEGER);//设置返回类型
    //cstmt.registerOutParameter(2, Types.INTEGER);//设置返回类型
    boolean execute = cstmt.execute();
    System.out.println("execute=" + execute);
    while (execute) {
      ResultSet resultSet = cstmt.getResultSet();//取得第一个结果集
      resultList = DataTableHelper.rs2MapList(resultSet);
      System.out.println("result=" + JSONObject.toJSONString(resultList));
      if (resultSet.next()) {
        System.out.println("打印结果集第一个字段");//打印出结果集的第一个字段
      }
      execute = cstmt.getMoreResults();//继续去取结果集，若还还能取到结果集，则bl=true了。然后回去循环。
    }


    dbUtil.close(cstmt, con);
  }

  public static void main(String[] args) throws Exception {
    getBookNameById("444444");
  }

}
