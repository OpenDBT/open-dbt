package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 表信息查询
 * @Title: TableProcess
 * @Package com.highgo.opendbt.tools.tableInfoSql
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/22 18:03
 */
@Component("tableProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.TABLE)
public class TableProcess implements EventProcess{
  Logger logger = LoggerFactory.getLogger(getClass());
  @Override
  public String execute(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" select obj_description(b.oid) as describe,a.tablename as table_name from pg_tables  a, pg_class b  ");
    sql.append(" where a.tablename = b.relname  ");
    sql.append(" and a.tablename =  ");
    sql.append("'");
    sql.append(tableName);
    sql.append("'");
    sql.append("  and a.schemaname=  ");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    logger.info(sql.toString());
    return sql.toString();
  }
}
