ALTER TABLE t_new_exercise ALTER COLUMN auth_type SET DEFAULT 1;
ALTER TABLE t_scene ADD auth_type int4 NULL DEFAULT 1;
UPDATE t_new_exercise SET auth_type = 1
COMMENT ON COLUMN "t_new_exercise"."auth_type" IS '1:私有，2:共享';

ALTER TABLE t_resources ADD md5 varchar;
ALTER TABLE t_resources ADD auth_type int4 NULL DEFAULT 1;
COMMENT ON COLUMN "t_resources"."auth_type" IS '1:私有，2:共享';

ALTER TABLE t_resources ADD course_id int4;
COMMENT ON COLUMN "t_resources"."course_id" IS '课程id';

-- t_resources表auth_type默认值都改为1私有,并且每个每个资源都增加课程id



-- ----------------------------
-- 镜像列表
-- ----------------------------
DROP TABLE IF EXISTS "t_images";
CREATE TABLE "t_images" (
  "id" serial primary key,
  "image_name" varchar(255),
  "image_port" varchar(20),
  "image_path" varchar(255)
)
;
COMMENT ON COLUMN "t_images"."image_name" IS '镜像名称';
COMMENT ON COLUMN "t_images"."image_port" IS '镜像端口号';
COMMENT ON COLUMN "t_images"."image_path" IS '镜像存放地址';

-- ----------------------------
-- 实验列表
-- ----------------------------
DROP TABLE IF EXISTS "t_experiment";
CREATE TABLE "t_experiment" (
  "id" bigserial primary key,
  "experiment_name" varchar(255),
  "image_id" int4,
  "release_status"  bool default false,
  "course_id" int4,
  create_user int4,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "update_user" int4,
  "delete_flag" int4 DEFAULT 0,
  "delete_time" timestamp(6),
  "delete_user" int4
)
;
COMMENT ON COLUMN "t_experiment"."experiment_name" IS '实验名称';
COMMENT ON COLUMN "t_experiment"."image_id" IS '镜像id';
COMMENT ON COLUMN "t_experiment"."release_status" IS '发布状态true:发布，false:未发布';
COMMENT ON COLUMN "t_experiment"."course_id" IS '课程id';

COMMENT ON COLUMN "t_experiment"."update_time" IS '修改时间';
COMMENT ON COLUMN "t_experiment"."update_user" IS '修改人员';
COMMENT ON COLUMN "t_experiment"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_experiment"."create_user" IS '创建人员';
COMMENT ON COLUMN "t_experiment"."delete_flag" IS '删除标志0：未删除1：已删除';
COMMENT ON COLUMN "t_experiment"."delete_time" IS '删除时间';
COMMENT ON COLUMN "t_experiment"."delete_user" IS '删除人员';

-- ----------------------------
-- 实验文档
-- ----------------------------
DROP TABLE IF EXISTS "t_experiment_documents";
CREATE TABLE "t_experiment_documents" (
  "id" bigserial primary key,
  "experiment_content" TEXT,
  "experiment_id" int8
  );
COMMENT ON COLUMN "t_experiment_documents"."experiment_content" IS '实验文档内容';
COMMENT ON COLUMN "t_experiment_documents"."experiment_id" IS '实验id';
-- ----------------------------
-- 容器列表
-- ----------------------------
DROP TABLE IF EXISTS "t_containers";
CREATE TABLE "t_containers" (
  "id" bigserial primary key,
  "container_id" varchar(100),
  "container_port" varchar(50),
  "code" varchar(50),
  "image_id" int4
)
;
COMMENT ON COLUMN "t_containers"."container_id" IS '容器id';
COMMENT ON COLUMN "t_containers"."container_port" IS '容器端口号';
COMMENT ON COLUMN "t_containers"."code" IS '学号';
COMMENT ON COLUMN "t_containers"."image_id" IS '镜像id';
