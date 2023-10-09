package com.highgo.opendbt.experiment.service;

import com.highgo.opendbt.experiment.model.ExperimentInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
@Service
public interface TDockerCommandService {
  //保存实验
  boolean saveExperiment(HttpServletRequest request, ExperimentInfo experiment);
}
