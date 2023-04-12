package com.highgo.opendbt.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @Description: test
 * @Title: DbUtil
 * @Package com.highgo.opendbt.test
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/9 16:40
 */
public class DbUtil {

  //数据库地址
  private static String dbUrl="jdbc:postgresql://118.190.151.85:5431/test";
  //用户名
  private static String dbUserName="sdnu";
  //密码
  private static String dbPassword="sdnu";
  //驱动名称
  private static String jdbcName="org.postgresql.Driver";

  /**
   * 获取数据库连接
   * 	1.加载数据库驱动
   *  2.获取数据库连接
   */
  public Connection getCon() throws Exception {

    Class.forName(jdbcName);//加载数据库驱动

    Connection con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

    return con;
  }

  /**
   * 关闭连接
   */
  public static void close(Statement stmt, Connection con) throws Exception {
    if(stmt!=null) {
      stmt.close();
    }
    if(con!=null) {
      con.close();
    }
  }
}
