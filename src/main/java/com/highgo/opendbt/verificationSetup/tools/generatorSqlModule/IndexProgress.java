package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取索引信息查询sql
 * @Title: FieldProgress
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:33
 */

@Component("indexProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.INDEX)
public class IndexProgress implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    return getIndexSql(schemaName, tableName);
  }


  //查询索引信息
  private String getIndexSql(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT  ");
    //sql.append(" A.SCHEMANAME, ");
    sql.append(" A.INDEXNAME as index_name, ");
    sql.append(" substring(A.INDEXDEF from '\\((.*?)\\)')AS index_fields, ");
    //sql.append(" A.TABLESPACE, ");
    //sql.append(" A.INDEXDEF,  ");
    sql.append(" B.AMNAME as index_type, ");
    //sql.append(" C.INDEXRELID, ");
    //sql.append(" C.INDNATTS, ");
    sql.append(" C.INDISUNIQUE as index_unique, ");
    sql.append(" D.DESCRIPTION ");
    sql.append(" FROM ");
    sql.append(" PG_AM B ");
    sql.append(" LEFT JOIN PG_CLASS F ON B.OID = F.RELAM ");
    sql.append(" LEFT JOIN PG_STAT_ALL_INDEXES E ON F.OID = E.INDEXRELID ");
    sql.append(" LEFT JOIN PG_INDEX C ON E.INDEXRELID = C.INDEXRELID ");
    sql.append(" LEFT OUTER JOIN PG_DESCRIPTION D ON C.INDEXRELID = D.OBJOID, ");
    sql.append(" PG_INDEXES A ");
    sql.append(" WHERE ");
    sql.append(" A.SCHEMANAME = E.SCHEMANAME  ");
    sql.append(" AND A.TABLENAME = E.RELNAME ");
    sql.append(" AND A.INDEXNAME = E.INDEXRELNAME ");
    sql.append(" AND C.INDISPRIMARY =false ");
    sql.append(" AND E.SCHEMANAME = ");
    sql.append(" '");
    sql.append(schemaName);
    sql.append("' ");
    sql.append(" AND E.RELNAME = ");
    sql.append(" '");
    sql.append(tableName);
    sql.append("' ");
    logger.info(sql.toString());
    return sql.toString();
  }
}
