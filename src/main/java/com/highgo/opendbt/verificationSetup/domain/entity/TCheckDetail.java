package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @TableName t_check_detail
 */
@TableName(value ="t_check_detail")
@Data
public class TCheckDetail implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 场景详情id
     */
    @TableField(value = "scene_detail_id")
    private Long sceneDetailId;

    /**
     * 题目id
     */
    @NotNull(message = "题目id不能为空")
    @TableField(value = "exercise_id")
    private Integer exerciseId;

    /**
     * 校验点状态UNCHANGED：未改变,UPDATE：更新,INSERT：新增,DEL：删除
     */
    @NotBlank(message = "校验点状态不能为空(UNCHANGED：未改变,UPDATE：更新,INSERT：新增,DEL：删除)")
    @TableField(value = "check_status")
    private String checkStatus;

    /**
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 表描述
     */
    @TableField(value = "describe")
    private String describe;

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
        TCheckDetail other = (TCheckDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()))
            && (this.getCheckStatus() == null ? other.getCheckStatus() == null : this.getCheckStatus().equals(other.getCheckStatus()))
            && (this.getTableName() == null ? other.getTableName() == null : this.getTableName().equals(other.getTableName()))
            && (this.getDescribe() == null ? other.getDescribe() == null : this.getDescribe().equals(other.getDescribe()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        result = prime * result + ((getExerciseId() == null) ? 0 : getExerciseId().hashCode());
        result = prime * result + ((getCheckStatus() == null) ? 0 : getCheckStatus().hashCode());
        result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
        result = prime * result + ((getDescribe() == null) ? 0 : getDescribe().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sceneDetailId=").append(sceneDetailId);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", tableName=").append(tableName);
        sb.append(", describe=").append(describe);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
