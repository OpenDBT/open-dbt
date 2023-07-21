ALTER TABLE t_new_exercise ADD exercise_status int4 NULL DEFAULT 0;
COMMENT ON COLUMN t_new_exercise.exercise_status IS '练习题状态 0：是练习题 1：非练习题' ;
ALTER TABLE t_new_exercise ADD show_answer int4 NULL DEFAULT 0;
COMMENT ON COLUMN t_new_exercise.show_answer IS '是否显示答案 0：显示答案 1：不显示答案' ;

ALTER TABLE t_new_exercise ADD execute_sql varchar;
COMMENT ON COLUMN t_new_exercise.execute_sql IS '函数执行语句' ;

ALTER TABLE t_new_exercise ADD very_sql varchar;
COMMENT ON COLUMN t_new_exercise.very_sql IS '函数验证语句' ;




-- ----------------------------
-- Table structure for t_check_constraint
-- ----------------------------
DROP TABLE IF EXISTS "t_check_constraint";
CREATE TABLE "t_check_constraint" (
  "id" bigserial primary key,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "scene_constraint_id" int8,
  "cr_name" varchar(255) COLLATE "pg_catalog"."default",
  "cr_type" varchar(255) COLLATE "pg_catalog"."default",
  "cr_fields" varchar(255) COLLATE "pg_catalog"."default",
  "cr_expression" varchar(255) COLLATE "pg_catalog"."default",
  "cr_index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_constraint"."id" IS '主键';
COMMENT ON COLUMN "t_check_constraint"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_constraint"."scene_constraint_id" IS '约束id';
COMMENT ON COLUMN "t_check_constraint"."cr_name" IS '约束名称';
COMMENT ON COLUMN "t_check_constraint"."cr_type" IS '约束类型';
COMMENT ON COLUMN "t_check_constraint"."cr_fields" IS '约束字段，分割';
COMMENT ON COLUMN "t_check_constraint"."cr_expression" IS '表达式';
COMMENT ON COLUMN "t_check_constraint"."cr_index_type" IS '排他约束索引类型';
COMMENT ON COLUMN "t_check_constraint"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_constraint"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_constraint"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_constraint"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_constraint_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_constraint_temp";
CREATE TABLE "t_check_constraint_temp" (
  "id" bigserial primary key,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "scene_constraint_id" int8,
  "cr_name" varchar(255) COLLATE "pg_catalog"."default",
  "cr_type" varchar(255) COLLATE "pg_catalog"."default",
  "cr_fields" varchar(255) COLLATE "pg_catalog"."default",
  "cr_expression" varchar(255) COLLATE "pg_catalog"."default",
  "cr_index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_constraint_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_constraint_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_constraint_temp"."scene_constraint_id" IS '约束id';
COMMENT ON COLUMN "t_check_constraint_temp"."cr_name" IS '约束名称';
COMMENT ON COLUMN "t_check_constraint_temp"."cr_type" IS '约束类型';
COMMENT ON COLUMN "t_check_constraint_temp"."cr_fields" IS '约束字段，分割';
COMMENT ON COLUMN "t_check_constraint_temp"."cr_expression" IS '表达式';
COMMENT ON COLUMN "t_check_constraint_temp"."cr_index_type" IS '排他约束索引类型';
COMMENT ON COLUMN "t_check_constraint_temp"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_constraint_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_constraint_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_constraint_temp"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_detail
-- ----------------------------
DROP TABLE IF EXISTS "t_check_detail";
CREATE TABLE "t_check_detail" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "exercise_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "describe" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_detail"."id" IS '主键';
COMMENT ON COLUMN "t_check_detail"."scene_detail_id" IS '场景详情id';
COMMENT ON COLUMN "t_check_detail"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_detail"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_detail"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_detail"."describe" IS '表描述';

-- ----------------------------
-- Table structure for t_check_detail_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_detail_temp";
CREATE TABLE "t_check_detail_temp" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "exercise_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "describe" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "t_check_detail_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_detail_temp"."scene_detail_id" IS '场景详情id';
COMMENT ON COLUMN "t_check_detail_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_detail_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_detail_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_detail_temp"."describe" IS '表描述';

-- ----------------------------
-- Table structure for t_check_field
-- ----------------------------
DROP TABLE IF EXISTS "t_check_field";
CREATE TABLE "t_check_field" (
  "id" bigserial primary key,
  "scene_field_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "sort_num" int4,
  "field_type" varchar(255) COLLATE "pg_catalog"."default",
  "field_length" int4,
  "field_default" varchar(255) COLLATE "pg_catalog"."default",
  "field_non_null" bool,
  "field_comment" varchar(255) COLLATE "pg_catalog"."default",
  "field_name" varchar(255) COLLATE "pg_catalog"."default",
  "auto_increment" bool,
  "decimal_num" int4,
  "primary_key" bool,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_field"."id" IS '主键';
COMMENT ON COLUMN "t_check_field"."scene_field_id" IS '场景字段表id';
COMMENT ON COLUMN "t_check_field"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_field"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_field"."field_type" IS '字段类型';
COMMENT ON COLUMN "t_check_field"."field_length" IS '字段长度';
COMMENT ON COLUMN "t_check_field"."field_default" IS '字段默认值';
COMMENT ON COLUMN "t_check_field"."field_non_null" IS '非空t：是f:否';
COMMENT ON COLUMN "t_check_field"."field_comment" IS '字段描述';
COMMENT ON COLUMN "t_check_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "t_check_field"."auto_increment" IS 't:自增字段f:非自增字段';
COMMENT ON COLUMN "t_check_field"."decimal_num" IS '小数点位数';
COMMENT ON COLUMN "t_check_field"."primary_key" IS 't:主键f:非主键';
COMMENT ON COLUMN "t_check_field"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_field"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_field"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_field_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_field_temp";
CREATE TABLE "t_check_field_temp" (
  "id" bigserial primary key,
  "scene_field_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "sort_num" int4,
  "field_type" varchar(255) COLLATE "pg_catalog"."default",
  "field_length" int4,
  "field_default" varchar(255) COLLATE "pg_catalog"."default",
  "field_non_null" bool,
  "field_comment" varchar(255) COLLATE "pg_catalog"."default",
  "field_name" varchar(255) COLLATE "pg_catalog"."default",
  "auto_increment" bool,
  "decimal_num" int4,
  "primary_key" bool,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_field_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_field_temp"."scene_field_id" IS '场景字段表id';
COMMENT ON COLUMN "t_check_field_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_field_temp"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_field_temp"."field_type" IS '字段类型';
COMMENT ON COLUMN "t_check_field_temp"."field_length" IS '字段长度';
COMMENT ON COLUMN "t_check_field_temp"."field_default" IS '字段默认值';
COMMENT ON COLUMN "t_check_field_temp"."field_non_null" IS '非空t：是f:否';
COMMENT ON COLUMN "t_check_field_temp"."field_comment" IS '字段描述';
COMMENT ON COLUMN "t_check_field_temp"."field_name" IS '字段名称';
COMMENT ON COLUMN "t_check_field_temp"."auto_increment" IS 't:自增字段f:非自增字段';
COMMENT ON COLUMN "t_check_field_temp"."decimal_num" IS '小数点位数';
COMMENT ON COLUMN "t_check_field_temp"."primary_key" IS 't:主键f:非主键';
COMMENT ON COLUMN "t_check_field_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_field_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_field_temp"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_fk
-- ----------------------------
DROP TABLE IF EXISTS "t_check_fk";
CREATE TABLE "t_check_fk" (
  "id" bigserial primary key,
  "scene_fk_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "fk_name" varchar(255) COLLATE "pg_catalog"."default",
  "fk_fields" varchar(255) COLLATE "pg_catalog"."default",
  "reference" varchar(255) COLLATE "pg_catalog"."default",
  "reference_fields" varchar(255) COLLATE "pg_catalog"."default",
  "update_rule" varchar(100) COLLATE "pg_catalog"."default",
  "delete_rule" varchar(100) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_fk"."id" IS '主键';
COMMENT ON COLUMN "t_check_fk"."scene_fk_id" IS '索引id';
COMMENT ON COLUMN "t_check_fk"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_fk"."fk_name" IS '外键约束名称';
COMMENT ON COLUMN "t_check_fk"."fk_fields" IS '外键约束字段';
COMMENT ON COLUMN "t_check_fk"."reference" IS '参照表';
COMMENT ON COLUMN "t_check_fk"."reference_fields" IS '参照表字段';
COMMENT ON COLUMN "t_check_fk"."update_rule" IS '更新规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_check_fk"."delete_rule" IS '删除规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_check_fk"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_fk"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_fk"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_fk"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_fk_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_fk_temp";
CREATE TABLE "t_check_fk_temp" (
  "id" bigserial primary key,
  "scene_fk_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "fk_name" varchar(255) COLLATE "pg_catalog"."default",
  "fk_fields" varchar(255) COLLATE "pg_catalog"."default",
  "reference" varchar(255) COLLATE "pg_catalog"."default",
  "reference_fields" varchar(255) COLLATE "pg_catalog"."default",
  "update_rule" varchar(100) COLLATE "pg_catalog"."default",
  "delete_rule" varchar(100) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_fk_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_fk_temp"."scene_fk_id" IS '索引id';
COMMENT ON COLUMN "t_check_fk_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_fk_temp"."fk_name" IS '外键约束名称';
COMMENT ON COLUMN "t_check_fk_temp"."fk_fields" IS '外键约束字段';
COMMENT ON COLUMN "t_check_fk_temp"."reference" IS '参照表';
COMMENT ON COLUMN "t_check_fk_temp"."reference_fields" IS '参照表字段';
COMMENT ON COLUMN "t_check_fk_temp"."update_rule" IS '更新规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_check_fk_temp"."delete_rule" IS '删除规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_check_fk_temp"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_fk_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_fk_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_fk_temp"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_index
-- ----------------------------
DROP TABLE IF EXISTS "t_check_index";
CREATE TABLE "t_check_index" (
  "id" bigserial primary key,
  "scene_index_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "index_name" varchar(255) COLLATE "pg_catalog"."default",
  "index_fields" varchar(255) COLLATE "pg_catalog"."default",
  "index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "index_unique" bool,
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_index"."id" IS '主键';
COMMENT ON COLUMN "t_check_index"."scene_index_id" IS '索引id';
COMMENT ON COLUMN "t_check_index"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_index"."index_name" IS '索引名称';
COMMENT ON COLUMN "t_check_index"."index_fields" IS '索引字段,分割';
COMMENT ON COLUMN "t_check_index"."index_type" IS '索引类型';
COMMENT ON COLUMN "t_check_index"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_index"."index_unique" IS '是否唯一索引 true ,false';
COMMENT ON COLUMN "t_check_index"."description" IS '描述';
COMMENT ON COLUMN "t_check_index"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_index"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_index"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_index_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_index_temp";
CREATE TABLE "t_check_index_temp" (
  "id" bigserial primary key,
  "scene_index_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "index_name" varchar(255) COLLATE "pg_catalog"."default",
  "index_fields" varchar(255) COLLATE "pg_catalog"."default",
  "index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "index_unique" bool,
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_index_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_index_temp"."scene_index_id" IS '索引id';
COMMENT ON COLUMN "t_check_index_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_index_temp"."index_name" IS '索引名称';
COMMENT ON COLUMN "t_check_index_temp"."index_fields" IS '索引字段,分割';
COMMENT ON COLUMN "t_check_index_temp"."index_type" IS '索引类型';
COMMENT ON COLUMN "t_check_index_temp"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_index_temp"."index_unique" IS '是否唯一索引 true ,false';
COMMENT ON COLUMN "t_check_index_temp"."description" IS '描述';
COMMENT ON COLUMN "t_check_index_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_index_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_index_temp"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_seq
-- ----------------------------
DROP TABLE IF EXISTS "t_check_seq";
CREATE TABLE "t_check_seq" (
  "id" bigserial primary key,
  "scene_seq_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "sort_num" int4,
  "seq_name" varchar(255) COLLATE "pg_catalog"."default",
  "step" int8,
  "min_value" int8,
  "max_value" int8,
  "latest_value" int8,
  "cycle" bool,
  "field" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "type_name" varchar(255) COLLATE "pg_catalog"."default",
  "start_value" int8,
  "cache_size" int8,
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_seq"."id" IS '主键';
COMMENT ON COLUMN "t_check_seq"."scene_seq_id" IS '初始化表序列id';
COMMENT ON COLUMN "t_check_seq"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_seq"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_seq"."seq_name" IS '序列名称';
COMMENT ON COLUMN "t_check_seq"."step" IS '步长';
COMMENT ON COLUMN "t_check_seq"."min_value" IS '最小值';
COMMENT ON COLUMN "t_check_seq"."max_value" IS '最大值';
COMMENT ON COLUMN "t_check_seq"."latest_value" IS '最新值';
COMMENT ON COLUMN "t_check_seq"."cycle" IS '是否循环f：否t：是';
COMMENT ON COLUMN "t_check_seq"."field" IS '列拥有';
COMMENT ON COLUMN "t_check_seq"."remark" IS '描述';
COMMENT ON COLUMN "t_check_seq"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_seq"."type_name" IS '数据类型';
COMMENT ON COLUMN "t_check_seq"."start_value" IS '开始值';
COMMENT ON COLUMN "t_check_seq"."cache_size" IS '缓冲尺寸';
COMMENT ON COLUMN "t_check_seq"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_seq"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_check_seq_temp
-- ----------------------------
DROP TABLE IF EXISTS "t_check_seq_temp";
CREATE TABLE "t_check_seq_temp" (
  "id" bigserial primary key,
  "scene_seq_id" int8,
  "check_status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 0,
  "sort_num" int4,
  "seq_name" varchar(255) COLLATE "pg_catalog"."default",
  "step" int8,
  "min_value" int8,
  "max_value" int8,
  "latest_value" int8,
  "cycle" bool,
  "field" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "table_name" varchar(255) COLLATE "pg_catalog"."default",
  "type_name" varchar(255) COLLATE "pg_catalog"."default",
  "start_value" int8,
  "cache_size" int8,
  "exercise_id" int8,
  "scene_detail_id" int8
)
;
COMMENT ON COLUMN "t_check_seq_temp"."id" IS '主键';
COMMENT ON COLUMN "t_check_seq_temp"."scene_seq_id" IS '初始化表序列id';
COMMENT ON COLUMN "t_check_seq_temp"."check_status" IS '校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除';
COMMENT ON COLUMN "t_check_seq_temp"."sort_num" IS '序号';
COMMENT ON COLUMN "t_check_seq_temp"."seq_name" IS '序列名称';
COMMENT ON COLUMN "t_check_seq_temp"."step" IS '步长';
COMMENT ON COLUMN "t_check_seq_temp"."min_value" IS '最小值';
COMMENT ON COLUMN "t_check_seq_temp"."max_value" IS '最大值';
COMMENT ON COLUMN "t_check_seq_temp"."latest_value" IS '最新值';
COMMENT ON COLUMN "t_check_seq_temp"."cycle" IS '是否循环f：否t：是';
COMMENT ON COLUMN "t_check_seq_temp"."field" IS '列拥有';
COMMENT ON COLUMN "t_check_seq_temp"."remark" IS '描述';
COMMENT ON COLUMN "t_check_seq_temp"."table_name" IS '表名';
COMMENT ON COLUMN "t_check_seq_temp"."type_name" IS '数据类型';
COMMENT ON COLUMN "t_check_seq_temp"."start_value" IS '开始值';
COMMENT ON COLUMN "t_check_seq_temp"."cache_size" IS '缓冲尺寸';
COMMENT ON COLUMN "t_check_seq_temp"."exercise_id" IS '题目id';
COMMENT ON COLUMN "t_check_seq_temp"."scene_detail_id" IS '场景详情id';

-- ----------------------------
-- Table structure for t_scene_constraint
-- ----------------------------
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

-- ----------------------------
-- Table structure for t_scene_detail
-- ----------------------------
DROP TABLE IF EXISTS "t_scene_detail";
CREATE TABLE "t_scene_detail" (
  "id" bigserial primary key,
  "scene_id" int4 NOT NULL,
  "table_name" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "table_detail" text COLLATE "pg_catalog"."default" NOT NULL,
  "table_desc" varchar COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for t_scene_field
-- ----------------------------
DROP TABLE IF EXISTS "t_scene_field";
CREATE TABLE "t_scene_field" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "sort_num" int4,
  "field_type" varchar(255) COLLATE "pg_catalog"."default",
  "field_length" int4,
  "field_default" varchar(255) COLLATE "pg_catalog"."default",
  "field_non_null" bool,
  "field_comment" varchar(255) COLLATE "pg_catalog"."default",
  "field_name" varchar(255) COLLATE "pg_catalog"."default",
  "auto_increment" bool,
  "decimal_num" int4,
  "primary_key" bool
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

-- ----------------------------
-- Table structure for t_scene_fk
-- ----------------------------
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
COMMENT ON COLUMN "t_scene_fk"."update_rule" IS '更新规则c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_scene_fk"."delete_rule" IS '删除规则c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,';
COMMENT ON COLUMN "t_scene_fk"."sort_num" IS '序号';

-- ----------------------------
-- Table structure for t_scene_index
-- ----------------------------
DROP TABLE IF EXISTS "t_scene_index";
CREATE TABLE "t_scene_index" (
  "id" bigserial primary key,
  "scene_detail_id" int8,
  "index_name" varchar(255) COLLATE "pg_catalog"."default",
  "index_fields" varchar(255) COLLATE "pg_catalog"."default",
  "index_type" varchar(255) COLLATE "pg_catalog"."default",
  "sort_num" int4,
  "index_unique" bool,
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

-- ----------------------------
-- Table structure for t_scene_seq
-- ----------------------------
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
  "cycle" bool,
  "field" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "type_name" varchar(255) COLLATE "pg_catalog"."default",
  "start_value" int8,
  "cache_size" int8
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
COMMENT ON COLUMN "t_scene_seq"."cycle" IS '是否循环f：否t：是';
COMMENT ON COLUMN "t_scene_seq"."field" IS '列拥有';
COMMENT ON COLUMN "t_scene_seq"."remark" IS '描述';
COMMENT ON COLUMN "t_scene_seq"."type_name" IS '数据类型';
COMMENT ON COLUMN "t_scene_seq"."start_value" IS '开始值';
COMMENT ON COLUMN "t_scene_seq"."cache_size" IS '缓冲尺寸';



--修改exercise_id为long类型
ALTER TABLE t_exam_exercise ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_exercise ALTER COLUMN id TYPE int8 USING id::int8;
ALTER TABLE t_exercise_knowledge ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_score ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;

ALTER TABLE t_public_exercise ALTER COLUMN id TYPE int8 USING id::int8;
ALTER TABLE t_exam_score ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_exercise_info ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_model_exercise ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_new_exercise ALTER COLUMN id TYPE int8 USING id::int8;
ALTER TABLE t_stu_homework_info ALTER COLUMN exercise_id TYPE int8 USING exercise_id::int8;
ALTER TABLE t_stu_homework_info ALTER COLUMN is_correct SET DEFAULT 2;

insert into t_exercise_type("type_code","type_name","type_style")values (7,'DDLSQL编程题',2);
insert into t_exercise_type("type_code","type_name","type_style")values (8,'DDL视图编程题',2);
insert into t_exercise_type("type_code","type_name","type_style")values (9,'DDL函数编程题',2);
insert into t_exercise_type("type_code","type_name","type_style")values (10,'DDL触发器编程题',2);

ALTER TABLE t_stu_homework ADD end_time timestamp NULL;
ALTER TABLE t_stu_homework ADD approval_user_id int4 NULL ;





