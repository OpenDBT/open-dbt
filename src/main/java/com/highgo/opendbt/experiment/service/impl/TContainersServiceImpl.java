package com.highgo.opendbt.experiment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.experiment.domain.TContainers;
import com.highgo.opendbt.experiment.model.ContainersInfo;
import com.highgo.opendbt.experiment.service.TContainersService;
import com.highgo.opendbt.experiment.mapper.TContainersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TContainersServiceImpl extends ServiceImpl<TContainersMapper, TContainers>
    implements TContainersService{
@Autowired
private TContainersMapper mapper;
  @Override
  public List<ContainersInfo> listContainer(int courseId, String containerName, String code) {
    return mapper.listContainer(courseId,containerName,code);
  }

  @Override
  public boolean delContainer(long id) {
    return this.remove(new QueryWrapper<TContainers>().eq("experiment_id",id));
  }

}




