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
 * @TableName t_scene_seq
 */
@TableName(value ="t_scene_seq")
@Data
public class TSceneSeq implements Serializable {
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
     * 序号
     */
    @TableField(value = "sort_num")
    private Integer sortNum;

    /**
     * 序列名称
     */
    @TableField(value = "seq_name")
    private String seqName;

    /**
     * 步长
     */
    @TableField(value = "step")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long step;

    /**
     * 最小值
     */
    @TableField(value = "min_value")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long minValue;

    /**
     * 最大值
     */
    @TableField(value = "max_value")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maxValue;

    /**
     * 最新值
     */
    @TableField(value = "latest_value")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long latestValue;

    /**
     * 是否循环f：否t：是
     */
    @TableField(value = "cycle")
    private Boolean cycle;

    /**
     * 列拥有
     */
    @TableField(value = "field")
    private String field;

    /**
     * 描述
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 数据类型
     */
    @TableField(value = "type_name")
    private String typeName;

    /**
     * 开始值
     */
    @TableField(value = "start_value")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long startValue;

    /**
     * 缓冲尺寸
     */
    @TableField(value = "cache_size")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cacheSize;

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
        TSceneSeq other = (TSceneSeq) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()))
            && (this.getSeqName() == null ? other.getSeqName() == null : this.getSeqName().equals(other.getSeqName()))
            && (this.getStep() == null ? other.getStep() == null : this.getStep().equals(other.getStep()))
            && (this.getMinValue() == null ? other.getMinValue() == null : this.getMinValue().equals(other.getMinValue()))
            && (this.getMaxValue() == null ? other.getMaxValue() == null : this.getMaxValue().equals(other.getMaxValue()))
            && (this.getLatestValue() == null ? other.getLatestValue() == null : this.getLatestValue().equals(other.getLatestValue()))
            && (this.getCycle() == null ? other.getCycle() == null : this.getCycle().equals(other.getCycle()))
            && (this.getField() == null ? other.getField() == null : this.getField().equals(other.getField()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getTypeName() == null ? other.getTypeName() == null : this.getTypeName().equals(other.getTypeName()))
            && (this.getStartValue() == null ? other.getStartValue() == null : this.getStartValue().equals(other.getStartValue()))
            && (this.getCacheSize() == null ? other.getCacheSize() == null : this.getCacheSize().equals(other.getCacheSize()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
        result = prime * result + ((getSeqName() == null) ? 0 : getSeqName().hashCode());
        result = prime * result + ((getStep() == null) ? 0 : getStep().hashCode());
        result = prime * result + ((getMinValue() == null) ? 0 : getMinValue().hashCode());
        result = prime * result + ((getMaxValue() == null) ? 0 : getMaxValue().hashCode());
        result = prime * result + ((getLatestValue() == null) ? 0 : getLatestValue().hashCode());
        result = prime * result + ((getCycle() == null) ? 0 : getCycle().hashCode());
        result = prime * result + ((getField() == null) ? 0 : getField().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getTypeName() == null) ? 0 : getTypeName().hashCode());
        result = prime * result + ((getStartValue() == null) ? 0 : getStartValue().hashCode());
        result = prime * result + ((getCacheSize() == null) ? 0 : getCacheSize().hashCode());
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
        sb.append(", sortNum=").append(sortNum);
        sb.append(", seqName=").append(seqName);
        sb.append(", step=").append(step);
        sb.append(", minValue=").append(minValue);
        sb.append(", maxValue=").append(maxValue);
        sb.append(", latestValue=").append(latestValue);
        sb.append(", cycle=").append(cycle);
        sb.append(", field=").append(field);
        sb.append(", remark=").append(remark);
        sb.append(", typeName=").append(typeName);
        sb.append(", startValue=").append(startValue);
        sb.append(", cacheSize=").append(cacheSize);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
