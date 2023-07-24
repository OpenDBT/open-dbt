package com.highgo.opendbt.score.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@ToString
@Accessors(chain = true)
public class Score {

  private int scoreId = -1;
  private int userId; // 用户id
  @JsonSerialize(using = ToStringSerializer.class)
  @NotNull(message = "习题id不能为空")
  private Long exerciseId; // 习题id
  private String exerciseName; // 习题名称
  private String createTime; // 答题时间
  private int usageTime; // 答题用时
  private String answer; // 答题内容
  private int score;// 结果，0正确，-1错误
  private int sclassId;// 班级
  private int answerExecuteTime = 0;// 答案执行时间

  private int answerLength = 0;// 答案长度

  private int examId;// 作业id
  private int examClassId;// 作业关联班级id
  @NotNull(message = "习题类型不能为空")
  private Integer exerciseType; // 习题类型
  //校验sql
  private String verySql;
  //场景id
  private Integer SceneId;



}
