package com.highgo.opendbt.verificationSetup.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName t_scene_field
 */
@TableName(value ="t_scene_field")
@Data
public class TSceneField implements Serializable {
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
     * 序号
     */
    @TableField(value = "sort_num")
    private Integer sortNum;

    /**
     * 字段类型
     */
    @TableField(value = "field_type")
    private String fieldType;

    /**
     * 字段长度
     */
    @TableField(value = "field_length")
    private Integer fieldLength;

    /**
     * 字段默认值
     */
    @TableField(value = "field_default")
    private String fieldDefault;

    /**
     * t：是f:否
     */
    @TableField(value = "field_non_null")
    private Boolean fieldNonNull;

    /**
     * 字段描述
     */
    @TableField(value = "field_comment")
    private String fieldComment;

    /**
     * 字段名称
     */
    @TableField(value = "field_name")
    private String fieldName;

    /**
     * t:自增字段f:非自增字段
     */
    @TableField(value = "auto_increment")
    private Boolean autoIncrement;

    /**
     * 小数点位数
     */
    @TableField(value = "decimal_num")
    private Integer decimalNum;

    /**
     * t:主键f:非主键
     */
    @TableField(value = "primary_key")
    private Boolean primaryKey;

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
        TSceneField other = (TSceneField) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSceneDetailId() == null ? other.getSceneDetailId() == null : this.getSceneDetailId().equals(other.getSceneDetailId()))
            && (this.getSortNum() == null ? other.getSortNum() == null : this.getSortNum().equals(other.getSortNum()))
            && (this.getFieldType() == null ? other.getFieldType() == null : this.getFieldType().equals(other.getFieldType()))
            && (this.getFieldLength() == null ? other.getFieldLength() == null : this.getFieldLength().equals(other.getFieldLength()))
            && (this.getFieldDefault() == null ? other.getFieldDefault() == null : this.getFieldDefault().equals(other.getFieldDefault()))
            && (this.getFieldNonNull() == null ? other.getFieldNonNull() == null : this.getFieldNonNull().equals(other.getFieldNonNull()))
            && (this.getFieldComment() == null ? other.getFieldComment() == null : this.getFieldComment().equals(other.getFieldComment()))
            && (this.getFieldName() == null ? other.getFieldName() == null : this.getFieldName().equals(other.getFieldName()))
            && (this.getAutoIncrement() == null ? other.getAutoIncrement() == null : this.getAutoIncrement().equals(other.getAutoIncrement()))
            && (this.getDecimalNum() == null ? other.getDecimalNum() == null : this.getDecimalNum().equals(other.getDecimalNum()))
            && (this.getPrimaryKey() == null ? other.getPrimaryKey() == null : this.getPrimaryKey().equals(other.getPrimaryKey()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSceneDetailId() == null) ? 0 : getSceneDetailId().hashCode());
        result = prime * result + ((getSortNum() == null) ? 0 : getSortNum().hashCode());
        result = prime * result + ((getFieldType() == null) ? 0 : getFieldType().hashCode());
        result = prime * result + ((getFieldLength() == null) ? 0 : getFieldLength().hashCode());
        result = prime * result + ((getFieldDefault() == null) ? 0 : getFieldDefault().hashCode());
        result = prime * result + ((getFieldNonNull() == null) ? 0 : getFieldNonNull().hashCode());
        result = prime * result + ((getFieldComment() == null) ? 0 : getFieldComment().hashCode());
        result = prime * result + ((getFieldName() == null) ? 0 : getFieldName().hashCode());
        result = prime * result + ((getAutoIncrement() == null) ? 0 : getAutoIncrement().hashCode());
        result = prime * result + ((getDecimalNum() == null) ? 0 : getDecimalNum().hashCode());
        result = prime * result + ((getPrimaryKey() == null) ? 0 : getPrimaryKey().hashCode());
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
        sb.append(", fieldType=").append(fieldType);
        sb.append(", fieldLength=").append(fieldLength);
        sb.append(", fieldDefault=").append(fieldDefault);
        sb.append(", fieldNonNull=").append(fieldNonNull);
        sb.append(", fieldComment=").append(fieldComment);
        sb.append(", fieldName=").append(fieldName);
        sb.append(", autoIncrement=").append(autoIncrement);
        sb.append(", decimalNum=").append(decimalNum);
        sb.append(", primaryKey=").append(primaryKey);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
