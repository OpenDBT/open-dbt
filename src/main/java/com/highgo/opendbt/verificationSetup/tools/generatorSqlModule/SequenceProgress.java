package com.highgo.opendbt.verificationSetup.tools.generatorSqlModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取序列查询sql
 * @Title: FieldProgress
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/16 13:33
 */

@Component("sequenceProgress")
@TableInfoEventAnnotation(value = TableInfoEvent.SEQUENCE)
public class SequenceProgress implements EventProcess {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String execute(String schemaName, String tableName) {
    return getSequenceSql(schemaName, tableName);
  }


  //查询约束信息
  private String getSequenceSql(String schemaName, String tableName) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT  ");
    sql.append(" s.relname   as seq_name, ");
    sql.append(" col.attname as field, ");
    sql.append(" seq.increment_by as step, ");
    sql.append(" seq.min_value as min_value,  ");
    sql.append(" seq.max_value as max_value, ");
    sql.append(" seq.cycle as cycle, ");
    sql.append(" seq.last_value as latest_value, ");
    sql.append(" seq.start_value as start_value, ");
    sql.append(" seq.cache_size, ");
    sql.append(" ty.typname as type_name ");
    sql.append(" from pg_class s ");
    sql.append(" join pg_namespace sn on sn.oid = s.relnamespace ");
    sql.append(" join pg_depend d on d.refobjid = s.oid and d.refclassid='pg_class'::regclass ");
    sql.append(" join pg_attrdef ad on ad.oid = d.objid and d.classid = 'pg_attrdef'::regclass ");
    sql.append(" join pg_attribute col on col.attrelid = ad.adrelid and col.attnum = ad.adnum ");
    sql.append(" join pg_class tbl on tbl.oid = ad.adrelid ");
    sql.append(" join pg_namespace ts on ts.oid = tbl.relnamespace   ");
    sql.append(" join pg_sequences seq on seq.sequencename=s.relname and seq.schemaname=sn.nspname  ");
    sql.append(" join pg_type ty on seq.data_type=ty.oid  ");
    sql.append(" where s.relkind = 'S' ");
    sql.append(" and d.deptype in ('a', 'n') ");
    sql.append(" and tbl.relname= ");
    sql.append("'");
    sql.append(tableName);
    sql.append("'");
    sql.append(" AND ts.nspname =  ");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    sql.append(" UNION ");
    sql.append(" SELECT ");
    sql.append(" s.relname   as seq_name, ");
    sql.append(" null  as field, ");
    sql.append(" seq.increment_by as step, ");
    sql.append(" seq.min_value as min_value, ");
    sql.append(" seq.max_value as max_value, ");
    sql.append(" seq.cycle as cycle, ");
    sql.append(" seq.last_value as latest_value, ");
    sql.append(" seq.start_value as start_value, ");
    sql.append(" seq.cache_size, ");
    sql.append(" ty.typname as type_name ");
    sql.append(" from   pg_class   s ");
    sql.append(" left   join pg_depend d ON d.refobjid = s.oid AND d.deptype <> 'i' ");
    sql.append(" join pg_namespace sn on sn.oid = s.relnamespace  ");
    sql.append(" join pg_sequences seq on seq.sequencename=s.relname and seq.schemaname=sn.nspname ");
    sql.append(" join pg_type ty on seq.data_type=ty.oid ");
    sql.append(" where  s.relkind = 'S' ");
    sql.append(" and  d.refobjid IS NULL  ");
    sql.append(" and sn.nspname= ");
    sql.append("'");
    sql.append(schemaName);
    sql.append("'");
    logger.info(sql.toString());
    return sql.toString();
  }
}
