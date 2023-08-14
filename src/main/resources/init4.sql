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
