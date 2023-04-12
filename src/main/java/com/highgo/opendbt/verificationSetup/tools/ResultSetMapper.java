package com.highgo.opendbt.verificationSetup.tools;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: resultSet转换为对象
 * @Title: ResultSetMapper
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/20 14:44
 */
public class ResultSetMapper<T> {
  Logger logger = LoggerFactory.getLogger(getClass());

  public List<T> mapRersultSetToObject(ResultSet rs, Class outputClass) {
    List<T> outputList = null;
    try {
      // 结果集不为空
      if (rs != null) {
        // 确认被TableName注解
        if (outputClass.isAnnotationPresent(TableName.class)) {
          // 获取 metadata
          ResultSetMetaData rsmd = rs.getMetaData();
          // 获取对象字段信息
          Field[] fields = outputClass.getDeclaredFields();
          while (rs.next()) {
            T bean = (T) outputClass.newInstance();
            for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {
              // 获取colnum name
              String columnName = rsmd.getColumnName(_iterator + 1);
              // 获取colnum value
              Object columnValue = rs.getObject(_iterator + 1);
              // 遍历对象字段 匹配结果集
              for (Field field : fields) {
                //主键
                boolean isIdField = field.isAnnotationPresent(TableId.class);
                if (isIdField) {
                  TableId tableId = field.getAnnotation(TableId.class);
                  String idValue = tableId.value();
                  //名称匹配
                  if (idValue.equals(columnName) && columnValue != null) {
                    //logger.info(idValue);
                    BeanUtils.setProperty(bean, field.getName(), columnValue);
                    break;
                  }
                }
                //非主键字段
                boolean isTableField = field.isAnnotationPresent(TableField.class);
                if (isTableField) {
                  TableField tableField = field.getAnnotation(TableField.class);
                  String fieldValue = tableField.value();
                  //logger.info(fieldValue);
                  if (fieldValue.equals(columnName) && columnValue != null) {
                    BeanUtils.setProperty(bean, field.getName(), columnValue);
                    break;
                  }
                }

              }
            }
            if (outputList == null) {
              outputList = new ArrayList<T>();
            }
            outputList.add(bean);
          }

        } else {
          // throw some error
        }
      } else {
        return null;
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return outputList;
  }
}

