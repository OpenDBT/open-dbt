package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_check_sql
 */
@TableName(value ="t_check_sql")
@Data
public class TCheckSql implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 校验sql
     */
    @TableField(value = "check_shell")
    private String checkShell;

    /**
     * 校验类型TABLE：表FIELD：字段INDEX：索引CONSTRAINT：约束FOREIGN_KEY：外键SEQUENCE：序列
     */
    @TableField(value = "check_type")
    private String checkType;

    /**
     * 习题id
     */
    @TableField(value = "exercise_id")
    private Integer exerciseId;

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
        TCheckSql other = (TCheckSql) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCheckShell() == null ? other.getCheckShell() == null : this.getCheckShell().equals(other.getCheckShell()))
            && (this.getCheckType() == null ? other.getCheckType() == null : this.getCheckType().equals(other.getCheckType()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCheckShell() == null) ? 0 : getCheckShell().hashCode());
        result = prime * result + ((getCheckType() == null) ? 0 : getCheckType().hashCode());
        result = prime * result + ((getExerciseId() == null) ? 0 : getExerciseId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", checkShell=").append(checkShell);
        sb.append(", checkType=").append(checkType);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
