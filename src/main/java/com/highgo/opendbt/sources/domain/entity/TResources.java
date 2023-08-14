package com.highgo.opendbt.sources.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.highgo.opendbt.common.entity.MyBaseEntity;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @TableName t_resources 资源表
 */
@TableName(value = "t_resources")
@Data
@ToString
public class TResources extends MyBaseEntity {
  /**
   * 资源id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 资源名称
   */
  @TableField(value = "resources_name")
  private String resourcesName;
  /**
   * 资源存储名称
   */
  @TableField(value = "resources_rename")
  private String resourcesRename;

  /**
   * 资源类型
   */
  @TableField(value = "resources_type")
  private String resourcesType;

  /**
   * 资源路径
   */
  @TableField(value = "resources_url")
  private String resourcesUrl;

  /**
   * 资源时长
   */
  @TableField(value = "resources_time")
  private Integer resourcesTime;

  /**
   * 资源后缀
   */
  @TableField(value = "resources_suffix")
  private String resourcesSuffix;

  /**
   * 视频缩略图路径
   */
  @TableField(value = "screenshot")
  private String screenshot;

  /**
   * ppt页数
   */
  @TableField(value = "page_num")
  private Integer pageNum;
  /**
   * 上层目录id
   */
  @TableField(value = "parent_id")
  private Integer parentId;
  /**
   * 排序
   */
  @TableField(value = "sort_num")
  private Integer sortNum;


  /**
   * 资源类型名称
   */
  @TableField(value = "resources_type_name")
  private String resourcesTypeName;

  /**
   * 文档类型资源转pdf后的路径
   */
  @TableField(value = "resources_pdf_url")
  private String resourcesPdfUrl;

  /**
   * 资源大小
   */
  @TableField(value = "resources_size")
  private Integer resourcesSize;

  /**
   * 转换后资源类型
   */
  @TableField(value = "resources_retype")
  private Integer resourcesRetype;

  /**
   * 是否为其他资源 1：是 2：否
   */
  @TableField(value = "resources_additional")
  private Integer resourcesAdditional;

  @TableField(value = "md5")
  private String md5;

  /**1:私有，2:共享*/
  @TableField(value = "auth_type")
  private Integer authType;

  /**课程id*/
  @TableField(value = "course_id")
  private Integer courseId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;

  /**
   * 子目录
   */
  @TableField(exist = false)
  private List<TResources> childrens;
  /**
   * 是否为叶子节点
   */
  @TableField(exist = false)
  private boolean isleaf;


}
