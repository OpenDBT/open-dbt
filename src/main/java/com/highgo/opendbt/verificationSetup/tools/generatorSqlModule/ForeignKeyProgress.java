package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取外键查询sql
 * @Title: FieldProgress
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:33
 */

@Component("foreignKeyProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.FOREIGN_KEY)
public class ForeignKeyProgress implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    return getConstraintSql(schemaName, tableName);
  }


  //查询约束信息
  private String getConstraintSql(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT  ");
    sql.append(" con.conname AS fk_name, ");
    sql.append(" (SELECT string_agg ( att.attname, ',' ) FROM pg_attribute att INNER JOIN UNNEST ( con.conkey ) UNNEST ( conkey ) ON UNNEST.conkey = att.attnum ");
    sql.append(" WHERE att.attrelid = con.conrelid ) AS fk_fields, ");
    sql.append(" fkrel.relname as reference, ");
    sql.append(" (SELECT string_agg ( attf.attname, ',' ) FROM pg_attribute attf INNER JOIN UNNEST ( con.confkey ) UNNEST ( confkey ) ON UNNEST.confkey = attf.attnum  ");
    sql.append(" WHERE attf.attrelid = con.confrelid ) AS reference_fields, ");
    //--外键更新动作编码 a = 没动作, r = 限制, c = 级联, n =设置为null, d =设置为缺省
    sql.append(" con.confupdtype as update_rule, ");
    //--	外键删除动作编码 a = 没动作, r = 限制, c = 级联, n =设置为null, d =设置为缺省
    sql.append(" con.confdeltype as delete_rule");
    sql.append(" FROM ");
    sql.append(" pg_constraint con ");
    sql.append(" INNER JOIN pg_class rel ON rel.OID = con.conrelid ");
    sql.append(" INNER JOIN pg_class fkrel ON fkrel.OID = con.confrelid ");
    sql.append(" INNER JOIN pg_namespace nsp ON nsp.OID = rel.relnamespace ");
    sql.append(" WHERE ");
    sql.append(" con.contype = 'f'  ");
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
