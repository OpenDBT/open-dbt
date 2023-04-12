package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取约束查询sql
 * @Title: FieldProgress
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:33
 */

@Component("constraintProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.CONSTRAINT)
public class ConstraintProgress implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    return getConstraintSql(schemaName, tableName);
  }


  //查询约束信息
  private String getConstraintSql(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT  ");
    sql.append(" con.conname AS cr_name, ");
    sql.append(" (SELECT string_agg ( att.attname, ',' ) FROM  pg_attribute att  INNER JOIN UNNEST ( con.conkey ) UNNEST ( conkey ) ON UNNEST.conkey = att.attnum ");
    sql.append(" WHERE att.attrelid = con.conrelid) AS cr_fields, ");
    sql.append(" (CASE ");
    sql.append(" WHEN con.contype = 'c' THEN  ");
    sql.append(" regexp_replace( pg_get_constraintdef ( con.OID ), '([CHECK,(,)]{1})', '', 'g' ) ");
    sql.append(" WHEN con.contype = 'x' THEN ");
    sql.append(" SUBSTRING ( pg_get_constraintdef ( con.OID ) FROM '\\((.*?)\\)' ) ELSE NULL ");
    sql.append(" END ");
    sql.append(" ) AS cr_expression, ");
    sql.append(" con.contype AS cr_type, ");
    sql.append(" ( CASE WHEN con.contype = 'x' THEN split_part( pg_get_constraintdef ( con.OID ), ' ', 3 ) ELSE NULL END ) AS cr_index_type ");
    sql.append(" FROM ");
    sql.append(" pg_constraint con ");
    sql.append(" INNER JOIN pg_class rel ON rel.OID = con.conrelid ");
    sql.append(" INNER JOIN pg_namespace nsp ON nsp.OID = rel.relnamespace ");
    sql.append(" WHERE ");
    sql.append(" con.contype != 'f'  ");
    sql.append(" AND rel.relname =  ");
    sql.append("'");
    sql.append(tableName);
    sql.append("'");
    sql.append(" AND nsp.nspname =  ");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    logger.info(sql.toString());
    return sql.toString();
  }
}
