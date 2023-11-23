package com.highgo.opendbt.experiment.mapper;

import com.highgo.opendbt.experiment.domain.TContainers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.highgo.opendbt.experiment.model.ContainersInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.highgo.opendbt.experiment.domain.TContainers
 */
public interface TContainersMapper extends BaseMapper<TContainers> {
//镜像列表查询
    List<ContainersInfo> listContainer(@Param("courseId") int courseId, @Param("containerName") String containerName, @Param("code") String code);
}




