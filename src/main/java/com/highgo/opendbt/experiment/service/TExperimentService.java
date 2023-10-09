package com.highgo.opendbt.experiment.service;

import com.highgo.opendbt.experiment.domain.TExperiment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.experiment.model.ExperimentInfo;

import java.util.List;

/**
 *
 */
public interface TExperimentService extends IService<TExperiment> {
  //在线实验详情查询
  ExperimentInfo getExperiment(long id,String code);
  //实验列表查询
  List<TExperiment>  listExperiment(Integer courseId, String experimentName);
}
