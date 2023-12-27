package com.highgo.opendbt.exercise.domain.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 题目设置为共享练习题/非共享练习题
 * @Title: SharedExercise
 * @Package com.highgo.opendbt.exercise.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/2/17 15:56
 */
@Data
@ToString
@Accessors(chain = true)
public class SharedExercise {
    @NotEmpty(message = "题目id不能为空")
    private List<Integer> ids;
    //1:私有，2:共享
    @NotNull(message = "题目共享状态设置不能为空")
    private int  authType;
    @NotNull(message = "课程id不能为空")
    private int courseId;
}
