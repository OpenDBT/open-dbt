package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_check_index
 */
@TableName(value ="t_check_index")
@Data
public class TCheckIndex implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 索引id
     */
    @TableField(value = "scene_index_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneIndexId;

    /**
     * 校验点状态UNCHANGED：未改变UPDATE：更新INSERT：新增DEL：删除
     */
    @TableField(value = "check_status")
    private String checkStatus;

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
        TCheckIndex other = (TCheckIndex) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneIndexId() == null ? other.getSceneIndexId() == null : this.getSceneIndexId().equals(other.getSceneIndexId()))
            && (this.getCheckStatus() == null ? other.getCheckStatus() == null : this.getCheckStatus().equals(other.getCheckStatus()))
            && (this.getIndexName() == null ? other.getIndexName() == null : this.getIndexName().equals(other.getIndexName()))
            && (this.getIndexFields() == null ? other.getIndexFields() == null : this.getIndexFields().equals(other.getIndexFields()))
            && (this.getIndexType() == null ? other.getIndexType() == null : this.getIndexType().equals(other.getIndexType()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()))
            && (this.getIndexUnique() == null ? other.getIndexUnique() == null : this.getIndexUnique().equals(other.getIndexUnique()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getTableName() == null ? other.getTableName() == null : this.getTableName().equals(other.getTableName()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneIndexId() == null) ? 0 : getSceneIndexId().hashCode());
        result = prime * result + ((getCheckStatus() == null) ? 0 : getCheckStatus().hashCode());
        result = prime * result + ((getIndexName() == null) ? 0 : getIndexName().hashCode());
        result = prime * result + ((getIndexFields() == null) ? 0 : getIndexFields().hashCode());
        result = prime * result + ((getIndexType() == null) ? 0 : getIndexType().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
        result = prime * result + ((getIndexUnique() == null) ? 0 : getIndexUnique().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
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
        sb.append(", sceneIndexId=").append(sceneIndexId);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", indexName=").append(indexName);
        sb.append(", indexFields=").append(indexFields);
        sb.append(", indexType=").append(indexType);
        sb.append(", sortNum=").append(sortNum);
        sb.append(", indexUnique=").append(indexUnique);
        sb.append(", description=").append(description);
        sb.append(", tableName=").append(tableName);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", sceneDetailId=").append(sceneDetailId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
