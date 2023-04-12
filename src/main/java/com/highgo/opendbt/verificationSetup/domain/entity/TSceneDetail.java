package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName t_scene_detail
 */
@TableName(value ="t_scene_detail")
@Data
public class TSceneDetail implements Serializable {
    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     *
     */
    @TableField(value = "scene_id")
    private Integer sceneId;

    /**
     *
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     *
     */
    @TableField(value = "table_detail")
    private String tableDetail;

    /**
     *
     */
    @TableField(value = "table_desc")
    private String tableDesc;

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
        TSceneDetail other = (TSceneDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneId() == null ? other.getSceneId() == null : this.getSceneId().equals(other.getSceneId()))
            && (this.getTableName() == null ? other.getTableName() == null : this.getTableName().equals(other.getTableName()))
            && (this.getTableDetail() == null ? other.getTableDetail() == null : this.getTableDetail().equals(other.getTableDetail()))
            && (this.getTableDesc() == null ? other.getTableDesc() == null : this.getTableDesc().equals(other.getTableDesc()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneId() == null) ? 0 : getSceneId().hashCode());
        result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
        result = prime * result + ((getTableDetail() == null) ? 0 : getTableDetail().hashCode());
        result = prime * result + ((getTableDesc() == null) ? 0 : getTableDesc().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sceneId=").append(sceneId);
        sb.append(", tableName=").append(tableName);
        sb.append(", tableDetail=").append(tableDetail);
        sb.append(", tableDesc=").append(tableDesc);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
