package com.highgo.opendbt.experiment.service;

import com.highgo.opendbt.experiment.domain.TContainers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.experiment.model.ContainersInfo;

import java.util.List;

/**
 *
 */
public interface TContainersService extends IService<TContainers> {
  //查询镜像列表
  List<ContainersInfo> listContainer(int courseId, String containerName, String code);

  //根据实验id删除容器
  boolean delContainer(long id);
}
