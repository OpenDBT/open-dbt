package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取字段信息查询sql
 * @Title: FieldProgress
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:33
 */
@Component("fieldProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.FIELD)
public class FieldProgress implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    return getFieldSql(schemaName, tableName);
  }


  //查询字段信息
  private String getFieldSql(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ");
    //sql.append("--c.relname as 表名,");
    sql.append("a.attnum as sort_num,");
    sql.append("a.attname as field_name,");
    sql.append("col.is_identity\tas auto_increment,");
    sql.append("col.column_default\tas field_default,");
    sql.append("coalesce(col.character_maximum_length,col.numeric_precision, -1) as field_length,");
    sql.append("col.numeric_scale as decimal_num,");
    //sql.append("--data_type as column_dataType,");
    sql.append("concat_ws('', t.typname) as field_type,");
    sql.append("(case ");
    sql.append("when a.attnotnull = true then true ");
    sql.append("else false end) as field_non_null,");
    sql.append("(case ");
    sql.append("when (");
    sql.append("select ");
    sql.append("count(pg_constraint.*) ");
    sql.append("from ");
    sql.append("pg_constraint ");
    sql.append("inner join pg_class on ");
    sql.append("pg_constraint.conrelid = pg_class.oid ");
    sql.append("inner join pg_attribute on ");
    sql.append("pg_attribute.attrelid = pg_class.oid ");
    sql.append("and pg_attribute.attnum = any(pg_constraint.conkey) ");
    sql.append("inner join pg_type on ");
    sql.append("pg_type.oid = pg_attribute.atttypid ");
    sql.append("where ");
    sql.append("pg_class.relname = c.relname ");
    sql.append("and pg_constraint.contype = 'p' ");
    sql.append("and pg_attribute.attname = a.attname) > 0 then true ");
    sql.append("else false end) as primary_key,");
    //sql.append("--(case when a.attlen > 0 then a.attlen");
    // sql.append("--when t.typname='bit' then a.atttypmod");
    // sql.append("--else a.atttypmod - 4 end) as 长度,");
    sql.append("(select description from pg_description where objoid = a.attrelid ");
    sql.append("and objsubid = a.attnum) as field_comment ");
    sql.append("from ");
    sql.append("pg_class c,");
    sql.append("pg_namespace nsp,");
    sql.append("pg_attribute a ,");
    sql.append("pg_type t,");
    sql.append("information_schema.columns as col ");
    sql.append("where ");
    sql.append("c.relname =");
    sql.append("'");
    sql.append(tableName);
    sql.append("'");
    sql.append(" and col.table_schema=");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    sql.append(" and nsp.nspname =");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    sql.append(" and a.attnum>0 ");
    sql.append(" and a.attrelid = c.oid ");
    sql.append(" and a.atttypid = t.oid ");
    sql.append(" and col.table_name=c.relname and col.column_name=a.attname ");
    sql.append(" and nsp.OID = c.relnamespace ");
    sql.append(" order by ");
    sql.append(" c.relname desc,");
    sql.append(" a.attnum asc ");
    logger.info(sql.toString());
    return sql.toString();
  }
}
