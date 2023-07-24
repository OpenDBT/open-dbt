package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 *
 * @TableName t_scene_fk
 */
@TableName(value ="t_scene_fk")
@Data
public class TSceneFk implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 场景表id
     */
    @TableField(value = "scene_detail_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneDetailId;

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
     * 更新规则
     */
    @TableField(value = "update_rule")
    private String updateRule;

    /**
     * 删除规则
     */
    @TableField(value = "delete_rule")
    private String deleteRule;

    /**
     * 序号
     */
    @TableField(value = "sort_num")
    private Integer sortNum;

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
        TSceneFk other = (TSceneFk) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()))
            && (this.getFkName() == null ? other.getFkName() == null : this.getFkName().equals(other.getFkName()))
            && (this.getFkFields() == null ? other.getFkFields() == null : this.getFkFields().equals(other.getFkFields()))
            && (this.getReference() == null ? other.getReference() == null : this.getReference().equals(other.getReference()))
            && (this.getReferenceFields() == null ? other.getReferenceFields() == null : this.getReferenceFields().equals(other.getReferenceFields()))
            && (this.getUpdateRule() == null ? other.getUpdateRule() == null : this.getUpdateRule().equals(other.getUpdateRule()))
            && (this.getDeleteRule() == null ? other.getDeleteRule() == null : this.getDeleteRule().equals(other.getDeleteRule()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        result = prime * result + ((getFkName() == null) ? 0 : getFkName().hashCode());
        result = prime * result + ((getFkFields() == null) ? 0 : getFkFields().hashCode());
        result = prime * result + ((getReference() == null) ? 0 : getReference().hashCode());
        result = prime * result + ((getReferenceFields() == null) ? 0 : getReferenceFields().hashCode());
        result = prime * result + ((getUpdateRule() == null) ? 0 : getUpdateRule().hashCode());
        result = prime * result + ((getDeleteRule() == null) ? 0 : getDeleteRule().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
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
        sb.append(", fkName=").append(fkName);
        sb.append(", fkFields=").append(fkFields);
        sb.append(", reference=").append(reference);
        sb.append(", referenceFields=").append(referenceFields);
        sb.append(", updateRule=").append(updateRule);
        sb.append(", deleteRule=").append(deleteRule);
        sb.append(", sortNum=").append(sortNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
