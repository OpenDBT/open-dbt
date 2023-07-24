package com.highgo.opendbt.exam.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_exam_score
 */
@TableName(value ="t_exam_score")
@Data
public class TExamScore implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 班级id
     */
    @TableField(value = "class_id")
    private Integer classId;

    /**
     * 作业 id
     */
    @TableField(value = "exam_id")
    private Integer examId;

    /**
     * 习题id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "exercise_id")
    private Long exerciseId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 使用时间
     */
    @TableField(value = "usage_time")
    private String usageTime;

    /**
     * 答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 分数
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 答案执行时间
     */
    @TableField(value = "answer_execute_time")
    private Integer answerExecuteTime;

    /**
     * 作业课程关联id
     */
    @TableField(value = "exam_class_id")
    private Integer examClassId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
