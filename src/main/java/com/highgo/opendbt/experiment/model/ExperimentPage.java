package com.highgo.opendbt.experiment.model;

import com.highgo.opendbt.common.bean.PageTO;
import lombok.Data;

/**
 * @Description:
 * @Title: ExperimentPage
 * @Package com.highgo.opendbt.experiment.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/26 15:38
 */
@Data
public class ExperimentPage extends PageTO {
  private String experimentName;
  private Integer courseId;
}
