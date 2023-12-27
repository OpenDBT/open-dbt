package com.highgo.opendbt.scene.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName t_scene
 */
@TableName(value = "t_scene")
@Data
@ToString
public class TScene implements Serializable {
  /**
   *
   */
  @TableId(value = "id")
  private Integer sceneId;

  /**
   *
   */
  @TableField(value = "course_id")
  private Integer courseId;

  /**
   *
   */
  @TableField(value = "scene_name")
  private String sceneName;

  /**
   *
   */
  @TableField(value = "scene_desc")
  private String sceneDesc;

  /**
   *
   */
  @TableField(value = "init_shell")
  private String initShell;

  /**
   *
   */
  @TableField(value = "delete_time")
  private LocalDateTime deleteTime;

  /**
   *
   */
  @TableField(value = "delete_flag")
  private Integer deleteFlag;

  /**
   *
   */
  @TableField(value = "parent_id")
  private Integer parentId;
  //1:私有，2:共享
  @TableField(value = "auth_type")
  private Integer authType;


  @TableField(exist = false)
  private static final long serialVersionUID = 1L;


}
