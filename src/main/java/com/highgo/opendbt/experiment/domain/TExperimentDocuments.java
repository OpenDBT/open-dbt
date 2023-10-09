package com.highgo.opendbt.experiment.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 *
 * @TableName t_experiment_documents
 */
public class TExperimentDocuments implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 实验文档内容
     */
    private String experimentContent;

    /**
     * 实验id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long experimentId;

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public Long getId() {
        return id;
    }

    /**
     *
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 实验文档内容
     */
    public String getExperimentContent() {
        return experimentContent;
    }

    /**
     * 实验文档内容
     */
    public void setExperimentContent(String experimentContent) {
        this.experimentContent = experimentContent;
    }

    /**
     * 实验id
     */
    public Long getExperimentId() {
        return experimentId;
    }

    /**
     * 实验id
     */
    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

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
        TExperimentDocuments other = (TExperimentDocuments) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExperimentContent() == null ? other.getExperimentContent() == null : this.getExperimentContent().equals(other.getExperimentContent()))
            && (this.getExperimentId() == null ? other.getExperimentId() == null : this.getExperimentId().equals(other.getExperimentId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExperimentContent() == null) ? 0 : getExperimentContent().hashCode());
        result = prime * result + ((getExperimentId() == null) ? 0 : getExperimentId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", experimentContent=").append(experimentContent);
        sb.append(", experimentId=").append(experimentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
