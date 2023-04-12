package com.highgo.opendbt.test;

import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import org.postgresql.jdbc.PgResultSet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 工具类
 * @Title: DataTableHelper
 * @Package com.highgo.opendbt.test
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/13 15:12
 */

public class DataTableHelper {

  public static List<Map<String, Object>> rs2MapList(ResultSet rsList) {

    List<Map<String, Object>> mytable = new ArrayList<>();
    if (rsList == null) {
      return mytable;
    }
    try {

      ResultSetMetaData mdata = rsList.getMetaData();
      int columnCount = mdata.getColumnCount();
      while (rsList.next()) {
        HashMap<String, Object> item = new HashMap<>();
        for (int j = 0; j < columnCount; j++) {
          Object resultSet = rsList.getObject(j + 1);
          if (resultSet instanceof PgResultSet) {
            item.put(mdata.getColumnName(j + 1), TableInfoUtil.resultSetConvertList((PgResultSet) resultSet));
          } else {
            item.put(mdata.getColumnName(j + 1), rsList.getObject(j + 1));
          }
        }
        mytable.add(item);
      }
      return mytable;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mytable;
  }


}

