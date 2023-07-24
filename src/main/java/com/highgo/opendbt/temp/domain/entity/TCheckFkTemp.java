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
 * @TableName t_check_fk_temp
 */
@TableName(value ="t_check_fk_temp")
@Data
public class TCheckFkTemp implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 索引id
     */
    @TableField(value = "scene_fk_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneFkId;

    /**
     * 校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除
     */
    @TableField(value = "check_status")
    private String checkStatus;

    /**
     * 外键约束名称
     */
    @TableField(value = "fk_name")
    private String fkName;

    /**
     * 外键约束字段
     */
    @TableField(value = "fk_fields")
    private String fkFields;

    /**
     * 参照表
     */
    @TableField(value = "reference")
    private String reference;

    /**
     * 参照表字段
     */
    @TableField(value = "reference_fields")
    private String referenceFields;

    /**
     * 更新规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,
     */
    @TableField(value = "update_rule")
    private String updateRule;

    /**
     * 删除规则 c CASCADE, r RESTRICT, a NO ACTION, n SET NULL,d SET DEFAULT,
     */
    @TableField(value = "delete_rule")
    private String deleteRule;

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
        TCheckFkTemp other = (TCheckFkTemp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneFkId() == null ? other.getSceneFkId() == null : this.getSceneFkId().equals(other.getSceneFkId()))
            && (this.getCheckStatus() == null ? other.getCheckStatus() == null : this.getCheckStatus().equals(other.getCheckStatus()))
            && (this.getFkName() == null ? other.getFkName() == null : this.getFkName().equals(other.getFkName()))
            && (this.getFkFields() == null ? other.getFkFields() == null : this.getFkFields().equals(other.getFkFields()))
            && (this.getReference() == null ? other.getReference() == null : this.getReference().equals(other.getReference()))
            && (this.getReferenceFields() == null ? other.getReferenceFields() == null : this.getReferenceFields().equals(other.getReferenceFields()))
            && (this.getUpdateRule() == null ? other.getUpdateRule() == null : this.getUpdateRule().equals(other.getUpdateRule()))
            && (this.getDeleteRule() == null ? other.getDeleteRule() == null : this.getDeleteRule().equals(other.getDeleteRule()))
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
        result = prime * result + ((getSceneFkId() == null) ? 0 : getSceneFkId().hashCode());
        result = prime * result + ((getCheckStatus() == null) ? 0 : getCheckStatus().hashCode());
        result = prime * result + ((getFkName() == null) ? 0 : getFkName().hashCode());
        result = prime * result + ((getFkFields() == null) ? 0 : getFkFields().hashCode());
        result = prime * result + ((getReference() == null) ? 0 : getReference().hashCode());
        result = prime * result + ((getReferenceFields() == null) ? 0 : getReferenceFields().hashCode());
        result = prime * result + ((getUpdateRule() == null) ? 0 : getUpdateRule().hashCode());
        result = prime * result + ((getDeleteRule() == null) ? 0 : getDeleteRule().hashCode());
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
        sb.append(", sceneFkId=").append(sceneFkId);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", fkName=").append(fkName);
        sb.append(", fkFields=").append(fkFields);
        sb.append(", reference=").append(reference);
        sb.append(", referenceFields=").append(referenceFields);
        sb.append(", updateRule=").append(updateRule);
        sb.append(", deleteRule=").append(deleteRule);
        sb.append(", sortNum=").append(sortNum);
        sb.append(", tableName=").append(tableName);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", sceneDetailId=").append(sceneDetailId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
