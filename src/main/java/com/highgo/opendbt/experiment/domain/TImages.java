package com.highgo.opendbt.experiment.domain;

import java.io.Serializable;

/**
 * 
 * @TableName t_images
 */
public class TImages implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 镜像名称
     */
    private String imageName;

    /**
     * 镜像端口号
     */
    private String imagePort;

    /**
     * 镜像存放地址
     */
    private String imagePath;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 镜像名称
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * 镜像名称
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * 镜像端口号
     */
    public String getImagePort() {
        return imagePort;
    }

    /**
     * 镜像端口号
     */
    public void setImagePort(String imagePort) {
        this.imagePort = imagePort;
    }

    /**
     * 镜像存放地址
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * 镜像存放地址
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
        TImages other = (TImages) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getImageName() == null ? other.getImageName() == null : this.getImageName().equals(other.getImageName()))
            && (this.getImagePort() == null ? other.getImagePort() == null : this.getImagePort().equals(other.getImagePort()))
            && (this.getImagePath() == null ? other.getImagePath() == null : this.getImagePath().equals(other.getImagePath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getImageName() == null) ? 0 : getImageName().hashCode());
        result = prime * result + ((getImagePort() == null) ? 0 : getImagePort().hashCode());
        result = prime * result + ((getImagePath() == null) ? 0 : getImagePath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", imageName=").append(imageName);
        sb.append(", imagePort=").append(imagePort);
        sb.append(", imagePath=").append(imagePath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}