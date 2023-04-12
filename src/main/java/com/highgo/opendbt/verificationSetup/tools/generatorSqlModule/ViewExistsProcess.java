package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 视图是否存在sql
 * @Title: ViewExists
 * @Package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/10 16:51
 */
@Component("viewExistsProcess")
@TableInfoEventAnnotation(value = TableInfoEvent.TABLE_EXISTS)
public class ViewExistsProcess implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT EXISTS (  ");
    sql.append(" SELECT FROM pg_catalog.pg_views   ");
    sql.append(" WHERE schemaname =  ");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    sql.append("  AND viewname=  ");
    sql.append("'");
    sql.append(tableName);
    sql.append("'");
    logger.info(sql.toString());
    return sql.toString();
  }
}
