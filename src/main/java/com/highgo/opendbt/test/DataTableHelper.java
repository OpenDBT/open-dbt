package com.highgo.opendbt.test;

import com.highgo.opendbt.common.bean.DataTypeAndImg;
import com.highgo.opendbt.common.bean.ResultSetInfo;
import com.highgo.opendbt.common.utils.DataTypeImgUtil;
import com.highgo.opendbt.common.utils.Message;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import org.postgresql.jdbc.PgResultSet;
import org.postgresql.util.PGobject;

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

      ResultSetMetaData metaData = rsList.getMetaData();
      int columnCount = metaData.getColumnCount();
      while (rsList.next()) {
        HashMap<String, Object> item = new HashMap<>();
        for (int j = 0; j < columnCount; j++) {
          Object resultSet = rsList.getObject(j + 1);
          if (resultSet instanceof PGobject) {
            PGobject pgObject = (PGobject) rsList.getObject(j + 1); // 1 表示第一列，即整个record类型
            item.put(metaData.getColumnName(j + 1), pgObject.getValue());
          } else if (resultSet instanceof PgResultSet) {
            item.put(metaData.getColumnName(j + 1), TableInfoUtil.resultSetConvertList((PgResultSet) resultSet));
          } else {
            item.put(metaData.getColumnName(j + 1), rsList.getObject(j + 1));
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

  public static List<ResultSetInfo> resultSetToResultSetInfo(ResultSet resultSet) {
    List<ResultSetInfo> resultSetList = new ArrayList<>();

    if (resultSet == null) {
      return resultSetList;
    }

    try {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();

      while (resultSet.next()) {
        ResultSetInfo resultSetInfo = new ResultSetInfo();

        List<String> columnList = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          columnList.add(metaData.getColumnName(i));
        }
        resultSetInfo.setColumnList(columnList);

        List<DataTypeAndImg> dataTypeAndImgList = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          dataTypeAndImgList.add(new DataTypeAndImg(resultSet.getMetaData().getColumnTypeName(i), DataTypeImgUtil.getDataTypeImgUrl(resultSet.getMetaData().getColumnTypeName(i))));

        }
        resultSetInfo.setDataTypeAndImgList(dataTypeAndImgList);

        List<Map<Object, Object>> dataList = new ArrayList<>();
        do {
          Map<Object, Object> rowData = new HashMap<>();
          for (int i = 1; i <= columnCount; i++) {
            Object columnValue = resultSet.getObject(i);
            if (columnValue instanceof PgResultSet) {
              List<ResultSetInfo> subResultSetList = resultSetToResultSetInfo((PgResultSet) columnValue);
              rowData.put(metaData.getColumnName(i), subResultSetList);
            } else {
              rowData.put(metaData.getColumnName(i), columnValue);
            }
          }
          dataList.add(rowData);
        } while (resultSet.next());

        resultSetInfo.setDataList(dataList);
        resultSetInfo.setColumnNumber(columnCount);
        resultSetInfo.setRowNumber(dataList.size());

        resultSetList.add(resultSetInfo);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultSetList;
  }

public static void resultSetToResultSetInfo(ResultSet resultSet, List<ResultSetInfo> resultSetList) {
  if (resultSet == null || resultSetList == null) {
    return;
  }

  try {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    Map<List<String>, ResultSetInfo> resultSetInfoMap = new HashMap<>();
    int rowKey = 0;
    while (resultSet.next()) {
      List<String> columnList = new ArrayList<>();
      //添加字段名、数据类型以及数据类型图标
      columnList.add(Message.get("SerialNumber"));
      for (int i = 1; i <= columnCount; i++) {
        columnList.add(metaData.getColumnName(i));
      }

      ResultSetInfo resultSetInfo = resultSetInfoMap.get(columnList);
      if (resultSetInfo == null) {
        resultSetInfo = new ResultSetInfo();
        resultSetInfo.setColumnList(columnList);

        List<DataTypeAndImg> dataTypeAndImgList = new ArrayList<>();
        dataTypeAndImgList.add(new DataTypeAndImg(Message.get("SerialNumber"), null));
        for (int i = 1; i <= columnCount; i++) {
          String columnTypeName = metaData.getColumnTypeName(i);
          dataTypeAndImgList.add(new DataTypeAndImg(columnTypeName, DataTypeImgUtil.getDataTypeImgUrl(columnTypeName)));
        }
        resultSetInfo.setDataTypeAndImgList(dataTypeAndImgList);

        resultSetInfoMap.put(columnList, resultSetInfo);
      }

      List<Map<Object, Object>> dataList = resultSetInfo.getDataList();

      // 判断数据行是否为空
      boolean hasData = false;
      Map<Object, Object> rowData = new HashMap<>();
      rowKey++;
      rowData.put(0, rowKey);
      for (int i = 1; i <= columnCount; i++) {
        Object columnValue = resultSet.getObject(i);
        if (columnValue instanceof PgResultSet) {
          PgResultSet pgResultSet = (PgResultSet) columnValue;
          resultSetToResultSetInfo(pgResultSet, resultSetList);
        } else {

          rowData.put(i, columnValue instanceof PGobject ? ((PGobject)columnValue).getValue():columnValue);
          // 判断数据行是否有非空值
          if (columnValue != null) {
            hasData = true;
          }
        }
      }

      // 仅当数据行非空时才添加到 dataList 中
      if (hasData) {
        dataList.add(rowData);
      }

      resultSetInfo.setColumnNumber(columnCount);
      resultSetInfo.setRowNumber(dataList.size());
    }

    // 仅将 dataList 不为空的 ResultSetInfo 添加到 resultSetList 中
    for (ResultSetInfo resultSetInfo : resultSetInfoMap.values()) {
      if (!resultSetInfo.getDataList().isEmpty()) {
        resultSetList.add(resultSetInfo);
      }
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
}

}

