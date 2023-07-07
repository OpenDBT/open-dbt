package com.highgo.opendbt.temp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 *
 * @TableName t_check_constraint_temp
 */
@TableName(value ="t_check_constraint_temp")
@Data
public class TCheckConstraintTemp implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除
     */
    @TableField(value = "check_status")
    private String checkStatus;

    /**
     * 约束id
     */
    @TableField(value = "scene_constraint_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneConstraintId;

    /**
     * 约束名称
     */
    @TableField(value = "cr_name")
    private String crName;

    /**
     * 约束类型
     */
    @TableField(value = "cr_type")
    private String crType;

    /**
     * 约束字段，分割
     */
    @TableField(value = "cr_fields")
    private String crFields;

    /**
     * 表达式
     */
    @TableField(value = "cr_expression")
    private String crExpression;

    /**
     * 排他约束索引类型
     */
    @TableField(value = "cr_index_type")
    private String crIndexType;

    /**
     * 序号
     */
    @TableField(value = "sort_num")
    private Integer sortNum;

    /**
     * 表名
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 题目id
     */
    @TableField(value = "exercise_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long exerciseId;

    /**
     * 场景详情id
     */
    @TableField(value = "scene_detail_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneDetailId;

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
        TCheckConstraintTemp other = (TCheckConstraintTemp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCheckStatus() == null ? other.getCheckStatus() == null : this.getCheckStatus().equals(other.getCheckStatus()))
            && (this.getSceneConstraintId() == null ? other.getSceneConstraintId() == null : this.getSceneConstraintId().equals(other.getSceneConstraintId()))
            && (this.getCrName() == null ? other.getCrName() == null : this.getCrName().equals(other.getCrName()))
            && (this.getCrType() == null ? other.getCrType() == null : this.getCrType().equals(other.getCrType()))
            && (this.getCrFields() == null ? other.getCrFields() == null : this.getCrFields().equals(other.getCrFields()))
            && (this.getCrExpression() == null ? other.getCrExpression() == null : this.getCrExpression().equals(other.getCrExpression()))
            && (this.getCrIndexType() == null ? other.getCrIndexType() == null : this.getCrIndexType().equals(other.getCrIndexType()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()))
            && (this.getTableName() == null ? other.getTableName() == null : this.getTableName().equals(other.getTableName()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCheckStatus() == null) ? 0 : getCheckStatus().hashCode());
        result = prime * result + ((getSceneConstraintId() == null) ? 0 : getSceneConstraintId().hashCode());
        result = prime * result + ((getCrName() == null) ? 0 : getCrName().hashCode());
        result = prime * result + ((getCrType() == null) ? 0 : getCrType().hashCode());
        result = prime * result + ((getCrFields() == null) ? 0 : getCrFields().hashCode());
        result = prime * result + ((getCrExpression() == null) ? 0 : getCrExpression().hashCode());
        result = prime * result + ((getCrIndexType() == null) ? 0 : getCrIndexType().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
        result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
        result = prime * result + ((getExerciseId() == null) ? 0 : getExerciseId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", sceneConstraintId=").append(sceneConstraintId);
        sb.append(", crName=").append(crName);
        sb.append(", crType=").append(crType);
        sb.append(", crFields=").append(crFields);
        sb.append(", crExpression=").append(crExpression);
        sb.append(", crIndexType=").append(crIndexType);
        sb.append(", sortNum=").append(sortNum);
        sb.append(", tableName=").append(tableName);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", sceneDetailId=").append(sceneDetailId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
