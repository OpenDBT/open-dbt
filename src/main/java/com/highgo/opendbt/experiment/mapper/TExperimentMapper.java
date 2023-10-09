package com.highgo.opendbt.experiment.mapper;

import com.highgo.opendbt.experiment.domain.TExperiment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.highgo.opendbt.experiment.domain.TExperiment
 */
public interface TExperimentMapper extends BaseMapper<TExperiment> {
  //根据id查询
  ExperimentInfo getExperimentInfo(@Param("id") long id,@Param("code") String code);

  //列表查询
  List<TExperiment> listExperiment(@Param("courseId") Integer courseId, @Param("experimentName") String experimentName);
}




