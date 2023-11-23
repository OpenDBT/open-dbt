package com.highgo.opendbt.experiment.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName t_backup
 */
@TableName(value ="t_backup")
@Accessors(chain = true)
@ToString
@Data
public class TBackup implements Serializable {
    /**
     * 主键
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 容器名称
     */
    @NotBlank(message = "备份容器名称不能为空")
    private String containerName;

    /**
     * 备份路径
     */
    @NotBlank(message = "备份路径不能为空")
    private String backupPath;

    /**
     * 备份时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "backup_time")
    private Date backupTime;

    /**
     * 镜像名称
     */
    @NotBlank(message = "镜像名称不能为空")
    private String imageName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



}
