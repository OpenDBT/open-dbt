DROP TABLE IF EXISTS "t_scene_field";
CREATE TABLE "t_scene_field" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "sort_num" int4,
  "field_type" varchar(255) COLLATE "pg_catalog"."default",
  "field_length" int4,
  "field_default" varchar(255) COLLATE "pg_catalog"."default",
  "field_non_null" varchar(32) COLLATE "pg_catalog"."default",
  "field_comment" varchar(255) COLLATE "pg_catalog"."default",
  "field_name" varchar(255) COLLATE "pg_catalog"."default",
  "auto_increment" varchar(255) COLLATE "pg_catalog"."default",
  "decimal_num" int4
)
;
COMMENT ON COLUMN "t_scene_field"."id" IS '主键';
COMMENT ON COLUMN "t_scene_field"."scene_detail_id" IS '场景表id';
COMMENT ON COLUMN "t_scene_field"."sort_num" IS '序号';
COMMENT ON COLUMN "t_scene_field"."field_type" IS '字段类型';
COMMENT ON COLUMN "t_scene_field"."field_length" IS '字段长度';
COMMENT ON COLUMN "t_scene_field"."field_default" IS '字段默认值';
COMMENT ON COLUMN "t_scene_field"."field_non_null" IS 't：是f:否';
COMMENT ON COLUMN "t_scene_field"."field_comment" IS '字段描述';
COMMENT ON COLUMN "t_scene_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "t_scene_field"."auto_increment" IS 't:自增字段f:非自增字段';
COMMENT ON COLUMN "t_scene_field"."decimal_num" IS '小数点位数';
COMMENT ON COLUMN "t_scene_field"."primary_key" IS 't:主键f:非主键';

DROP TABLE IF EXISTS "t_scene_constraint";
CREATE TABLE "t_scene_constraint" (
	"id" bigserial primary key,
  "scene_detail_id" int8,
  "cr_name" varchar(255) COLLATE "pg_catalog"."default",
  "cr_type" varchar(255) COLLATE "pg_catalog"."default",
  "cr_fields" varchar(255) COLLATE "pg_catalog"."default",
  "cr_expression" varchar(255) COLLATE "pg_catalog"."default",
  "cr_index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4
)
;
COMMENT ON COLUMN "t_scene_constraint"."id" IS '主键';
COMMENT ON COLUMN "t_scene_constraint"."scene_detail_id" IS '场景表id';
COMMENT ON COLUMN "t_scene_constraint"."cr_name" IS '约束名称';
COMMENT ON COLUMN "t_scene_constraint"."cr_type" IS '约束类型';
COMMENT ON COLUMN "t_scene_constraint"."cr_fields" IS '约束字段，分割';
COMMENT ON COLUMN "t_scene_constraint"."cr_expression" IS '表达式';
COMMENT ON COLUMN "t_scene_constraint"."cr_index_type" IS '排他约束索引类型';
COMMENT ON COLUMN "t_scene_constraint"."sort_num" IS '序号';



DROP TABLE IF EXISTS "t_scene_index";
CREATE TABLE "t_scene_index" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "index_name" varchar(255) COLLATE "pg_catalog"."default",
  "index_fields" varchar(255) COLLATE "pg_catalog"."default",
  "index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "index_unique" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_scene_index"."id" IS '主键';
COMMENT ON COLUMN "t_scene_index"."scene_detail_id" IS '场景表id';
COMMENT ON COLUMN "t_scene_index"."index_name" IS '索引名称';
COMMENT ON COLUMN "t_scene_index"."index_fields" IS '索引字段,分割';
COMMENT ON COLUMN "t_scene_index"."index_type" IS '索引类型';
COMMENT ON COLUMN "t_scene_index"."sort_num" IS '序号';
COMMENT ON COLUMN "t_scene_index"."index_unique" IS '是否唯一索引 true ,false';
COMMENT ON COLUMN "t_scene_index"."description" IS '描述';



DROP TABLE IF EXISTS "t_scene_fk";
CREATE TABLE "t_scene_fk" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "fk_name" varchar(255) COLLATE "pg_catalog"."default",
  "fk_fields" varchar(255) COLLATE "pg_catalog"."default",
  "reference" varchar(255) COLLATE "pg_catalog"."default",
  "reference_fields" varchar(255) COLLATE "pg_catalog"."default",
  "update_rule" varchar(100) COLLATE "pg_catalog"."default",
  "delete_rule" varchar(100) COLLATE "pg_catalog"."default",
  "sort_num" int4
)
;
COMMENT ON COLUMN "t_scene_fk"."id" IS '主键';
COMMENT ON COLUMN "t_scene_fk"."scene_detail_id" IS '场景表id';
COMMENT ON COLUMN "t_scene_fk"."fk_name" IS '外键约束名称';
COMMENT ON COLUMN "t_scene_fk"."fk_fields" IS '外键约束字段';
COMMENT ON COLUMN "t_scene_fk"."reference" IS '参照表';
COMMENT ON COLUMN "t_scene_fk"."reference_fields" IS '参照表字段';
COMMENT ON COLUMN "t_scene_fk"."update_rule" IS '更新规则';
COMMENT ON COLUMN "t_scene_fk"."delete_rule" IS '删除规则';
COMMENT ON COLUMN "t_scene_fk"."sort_num" IS '序号';


DROP TABLE IF EXISTS "t_scene_seq";
CREATE TABLE "t_scene_seq" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "sort_num" int4,
  "seq_name" varchar(255) COLLATE "pg_catalog"."default",
  "step" int8,
  "min_value" int8,
  "max_value" int8,
  "latest_value" int8,
  "cycle" int4,
  "field" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "type_name" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_scene_seq"."id" IS '主键';
COMMENT ON COLUMN "t_scene_seq"."scene_detail_id" IS '场景表id';
COMMENT ON COLUMN "t_scene_seq"."sort_num" IS '序号';
COMMENT ON COLUMN "t_scene_seq"."seq_name" IS '序列名称';
COMMENT ON COLUMN "t_scene_seq"."step" IS '步长';
COMMENT ON COLUMN "t_scene_seq"."min_value" IS '最小值';
COMMENT ON COLUMN "t_scene_seq"."max_value" IS '最大值';
COMMENT ON COLUMN "t_scene_seq"."latest_value" IS '最新值';
COMMENT ON COLUMN "t_scene_seq"."cycle" IS '是否循环0：否1：是';
COMMENT ON COLUMN "t_scene_seq"."field" IS '列拥有';
COMMENT ON COLUMN "t_scene_seq"."remark" IS '描述';
COMMENT ON COLUMN "t_scene_seq"."type_name" IS '数据类型';



-------------------------------------------
DROP TABLE IF EXISTS "t_check_constraint";
CREATE TABLE "t_check_constraint" (
  "id" bigserial primary key,
  "check_detail_id" int8,
  "check_status" int4 DEFAULT 0,
  "scene_constraint_id" int8,
  "cr_name" varchar(255) COLLATE "pg_catalog"."default",
  "cr_type" varchar(255) COLLATE "pg_catalog"."default",
  "cr_fields" varchar(255) COLLATE "pg_catalog"."default",
  "cr_expression" varchar(255) COLLATE "pg_catalog"."default",
  "cr_index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4
)
;
COMMENT ON COLUMN "t_check_constraint"."id" IS '主键';
COMMENT ON COLUMN "t_check_constraint"."check_detail_id" IS '校验表id';
COMMENT ON COLUMN "t_check_constraint"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_constraint"."scene_constraint_id" IS '约束id';
COMMENT ON COLUMN "t_check_constraint"."cr_name" IS '约束名称';
COMMENT ON COLUMN "t_check_constraint"."cr_type" IS '约束类型';
COMMENT ON COLUMN "t_check_constraint"."cr_fields" IS '约束字段，分割';
COMMENT ON COLUMN "t_check_constraint"."cr_expression" IS '表达式';
COMMENT ON COLUMN "t_check_constraint"."cr_index_type" IS '排他约束索引类型';
COMMENT ON COLUMN "t_check_constraint"."sort_num" IS '序号';


DROP TABLE IF EXISTS "t_check_detail";
CREATE TABLE "t_check_detail" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "exercise_id" int4,
  "check_status" int4 DEFAULT 0,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "describe" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_detail"."id" IS '主键';
COMMENT ON COLUMN "t_check_detail"."scene_detail_id" IS '场景详情id';
COMMENT ON COLUMN "t_check_detail"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_detail"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_detail"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_detail"."describe" IS '表描述';



DROP TABLE IF EXISTS "t_check_field";
CREATE TABLE "t_check_field" (
  "id" bigserial primary key,
  "check_detail_id" int8,
  "scene_field_id" int8,
  "check_status" int4 DEFAULT 0,
  "sort_num" int4,
  "field_type" varchar(255) COLLATE "pg_catalog"."default",
  "field_length" int4,
  "field_default" varchar(255) COLLATE "pg_catalog"."default",
  "field_non_null" varchar(32) COLLATE "pg_catalog"."default",
  "field_comment" varchar(255) COLLATE "pg_catalog"."default",
  "field_name" varchar(255) COLLATE "pg_catalog"."default",
  "auto_increment" varchar(255) COLLATE "pg_catalog"."default",
  "decimal_num" int4,
  "primary_key" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_field"."id" IS '主键';
COMMENT ON COLUMN "t_check_field"."check_detail_id" IS '校验表id';
COMMENT ON COLUMN "t_check_field"."scene_field_id" IS '场景字段表id';
COMMENT ON COLUMN "t_check_field"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_field"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_field"."field_type" IS '字段类型';
COMMENT ON COLUMN "t_check_field"."field_length" IS '字段长度';
COMMENT ON COLUMN "t_check_field"."field_default" IS '字段默认值';
COMMENT ON COLUMN "t_check_field"."field_non_null" IS 't：是f:否';
COMMENT ON COLUMN "t_check_field"."field_comment" IS '字段描述';
COMMENT ON COLUMN "t_check_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "t_check_field"."auto_increment" IS 't:自增字段f:非自增字段';
COMMENT ON COLUMN "t_check_field"."decimal_num" IS '小数点位数';
COMMENT ON COLUMN "t_check_field"."primary_key" IS 't:主键f:非主键';


DROP TABLE IF EXISTS "t_check_fk";
CREATE TABLE "t_check_fk" (
  "id" bigserial primary key,
  "check_detail_id" int8,
  "scene_fk_id" int8,
  "check_status" int4 DEFAULT 0,
  "fk_name" varchar(255) COLLATE "pg_catalog"."default",
  "fk_fields" varchar(255) COLLATE "pg_catalog"."default",
  "reference" varchar(255) COLLATE "pg_catalog"."default",
  "reference_fields" varchar(255) COLLATE "pg_catalog"."default",
  "update_rule" varchar(100) COLLATE "pg_catalog"."default",
  "delete_rule" varchar(100) COLLATE "pg_catalog"."default",
  "sort_num" int4
)
;
COMMENT ON COLUMN "t_check_fk"."id" IS '主键';
COMMENT ON COLUMN "t_check_fk"."check_detail_id" IS '校验表id';
COMMENT ON COLUMN "t_check_fk"."scene_fk_id" IS '索引id';
COMMENT ON COLUMN "t_check_fk"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_fk"."fk_name" IS '外键约束名称';
COMMENT ON COLUMN "t_check_fk"."fk_fields" IS '外键约束字段';
COMMENT ON COLUMN "t_check_fk"."reference" IS '参照表';
COMMENT ON COLUMN "t_check_fk"."reference_fields" IS '参照表字段';
COMMENT ON COLUMN "t_check_fk"."update_rule" IS '更新规则';
COMMENT ON COLUMN "t_check_fk"."delete_rule" IS '删除规则';
COMMENT ON COLUMN "t_check_fk"."sort_num" IS '序号';

DROP TABLE IF EXISTS "t_check_index";
CREATE TABLE "t_check_index" (
  "id" bigserial primary key,
  "check_detail_id" int8,
  "scene_index_id" int8,
  "check_status" int4 DEFAULT 0,
  "index_name" varchar(255) COLLATE "pg_catalog"."default",
  "index_fields" varchar(255) COLLATE "pg_catalog"."default",
  "index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "index_unique" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_index"."id" IS '主键';
COMMENT ON COLUMN "t_check_index"."check_detail_id" IS '校验表id';
COMMENT ON COLUMN "t_check_index"."scene_index_id" IS '索引id';
COMMENT ON COLUMN "t_check_index"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_index"."index_name" IS '索引名称';
COMMENT ON COLUMN "t_check_index"."index_fields" IS '索引字段,分割';
COMMENT ON COLUMN "t_check_index"."index_type" IS '索引类型';
COMMENT ON COLUMN "t_check_index"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_index"."index_unique" IS '是否唯一索引 true ,false';
COMMENT ON COLUMN "t_check_index"."description" IS '描述';


DROP TABLE IF EXISTS "t_check_seq";
CREATE TABLE "t_check_seq" (
  "id" bigserial primary key,
  "check_detail_id" int8,
  "scene_seq_id" int8,
  "check_status" int4 DEFAULT 0,
  "sort_num" int4,
  "seq_name" varchar(255) COLLATE "pg_catalog"."default",
  "step" int8,
  "min_value" int8,
  "max_value" int8,
  "latest_value" int8,
  "cycle" int4,
  "field" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_seq"."id" IS '主键';
COMMENT ON COLUMN "t_check_seq"."check_detail_id" IS '校验表id';
COMMENT ON COLUMN "t_check_seq"."scene_seq_id" IS '索引id';
COMMENT ON COLUMN "t_check_seq"."check_status" IS '校验点状态0：未改变1：更新2：新增3：删除';
COMMENT ON COLUMN "t_check_seq"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_seq"."seq_name" IS '序列名称';
COMMENT ON COLUMN "t_check_seq"."step" IS '步长';
COMMENT ON COLUMN "t_check_seq"."min_value" IS '最小值';
COMMENT ON COLUMN "t_check_seq"."max_value" IS '最大值';
COMMENT ON COLUMN "t_check_seq"."latest_value" IS '最新值';
COMMENT ON COLUMN "t_check_seq"."cycle" IS '是否循环0：否1：是';
COMMENT ON COLUMN "t_check_seq"."field" IS '列拥有';
COMMENT ON COLUMN "t_check_seq"."remark" IS '描述';
