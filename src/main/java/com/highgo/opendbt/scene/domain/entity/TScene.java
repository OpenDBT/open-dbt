package com.highgo.opendbt.scene.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @TableName t_scene
 */
@TableName(value ="t_scene")
@Data
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TScene other = (TScene) that;
        return (this.getSceneId() == null ? other.getSceneId() == null : this.getSceneId().equals(other.getSceneId()))
            && (this.getCourseId() == null ? other.getCourseId() == null : this.getCourseId().equals(other.getCourseId()))
            && (this.getSceneName() == null ? other.getSceneName() == null : this.getSceneName().equals(other.getSceneName()))
            && (this.getSceneDesc() == null ? other.getSceneDesc() == null : this.getSceneDesc().equals(other.getSceneDesc()))
            && (this.getInitShell() == null ? other.getInitShell() == null : this.getInitShell().equals(other.getInitShell()))
            && (this.getDeleteTime() == null ? other.getDeleteTime() == null : this.getDeleteTime().equals(other.getDeleteTime()))
            && (this.getDeleteFlag() == null ? other.getDeleteFlag() == null : this.getDeleteFlag().equals(other.getDeleteFlag()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSceneId() == null) ? 0 : getSceneId().hashCode());
        result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
        result = prime * result + ((getSceneName() == null) ? 0 : getSceneName().hashCode());
        result = prime * result + ((getSceneDesc() == null) ? 0 : getSceneDesc().hashCode());
        result = prime * result + ((getInitShell() == null) ? 0 : getInitShell().hashCode());
        result = prime * result + ((getDeleteTime() == null) ? 0 : getDeleteTime().hashCode());
        result = prime * result + ((getDeleteFlag() == null) ? 0 : getDeleteFlag().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(sceneId);
        sb.append(", courseId=").append(courseId);
        sb.append(", sceneName=").append(sceneName);
        sb.append(", sceneDesc=").append(sceneDesc);
        sb.append(", initShell=").append(initShell);
        sb.append(", deleteTime=").append(deleteTime);
        sb.append(", deleteFlag=").append(deleteFlag);
        sb.append(", parentId=").append(parentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
