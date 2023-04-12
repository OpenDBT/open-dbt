package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName t_scene_index
 */
@TableName(value ="t_scene_index")
@Data
public class TSceneIndex implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 场景表id
     */
    @TableField(value = "scene_detail_id")
    private Long sceneDetailId;

    /**
     * 索引名称
     */
    @TableField(value = "index_name")
    private String indexName;

    /**
     * 索引字段,分割
     */
    @TableField(value = "index_fields")
    private String indexFields;

    /**
     * 索引类型
     */
    @TableField(value = "index_type")
    private String indexType;

    /**
     * 序号
     */
    @TableField(value = "sort_num")
    private Integer sortNum;

    /**
     * 是否唯一索引 true ,false
     */
    @TableField(value = "index_unique")
    private Boolean indexUnique;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

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
        TSceneIndex other = (TSceneIndex) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()))
            && (this.getIndexName() == null ? other.getIndexName() == null : this.getIndexName().equals(other.getIndexName()))
            && (this.getIndexFields() == null ? other.getIndexFields() == null : this.getIndexFields().equals(other.getIndexFields()))
            && (this.getIndexType() == null ? other.getIndexType() == null : this.getIndexType().equals(other.getIndexType()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()))
            && (this.getIndexUnique() == null ? other.getIndexUnique() == null : this.getIndexUnique().equals(other.getIndexUnique()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        result = prime * result + ((getIndexName() == null) ? 0 : getIndexName().hashCode());
        result = prime * result + ((getIndexFields() == null) ? 0 : getIndexFields().hashCode());
        result = prime * result + ((getIndexType() == null) ? 0 : getIndexType().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
        result = prime * result + ((getIndexUnique() == null) ? 0 : getIndexUnique().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
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
        sb.append(", indexName=").append(indexName);
        sb.append(", indexFields=").append(indexFields);
        sb.append(", indexType=").append(indexType);
        sb.append(", sortNum=").append(sortNum);
        sb.append(", indexUnique=").append(indexUnique);
        sb.append(", description=").append(description);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
