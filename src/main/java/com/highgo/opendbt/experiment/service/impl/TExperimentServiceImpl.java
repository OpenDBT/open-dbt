package com.highgo.opendbt.experiment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.experiment.domain.TExperiment;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import com.highgo.opendbt.experiment.service.TExperimentService;
import com.highgo.opendbt.experiment.mapper.TExperimentMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TExperimentServiceImpl extends ServiceImpl<TExperimentMapper, TExperiment>
    implements TExperimentService{
@Autowired
  TExperimentMapper  experimentMapper;
  @Override
  public ExperimentInfo getExperiment(long id,String code) {
    return experimentMapper.getExperimentInfo(id,code);
  }

  @Override
  public List<TExperiment> listExperiment(Integer courseId, String experimentName) {
    return experimentMapper.listExperiment(courseId,experimentName);
  }
}




