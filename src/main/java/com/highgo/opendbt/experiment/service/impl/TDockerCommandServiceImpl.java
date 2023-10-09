package com.highgo.opendbt.experiment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.experiment.domain.TExperiment;
import com.highgo.opendbt.experiment.domain.TExperimentDocuments;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import com.highgo.opendbt.experiment.service.TDockerCommandService;
import com.highgo.opendbt.experiment.service.TExperimentDocumentsService;
import com.highgo.opendbt.experiment.service.TExperimentService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description:
 * @Title: TDockerCommandServiceImpl
 * @Package com.highgo.opendbt.experiment.service.impl
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/28 9:54
 */
@Service
public class TDockerCommandServiceImpl implements TDockerCommandService {
  @Autowired
  private TExperimentDocumentsService documentsService;
  @Autowired
  private TExperimentService experimentService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveExperiment(HttpServletRequest request, ExperimentInfo experiment) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //拷贝属性
    TExperiment tExperiment = new TExperiment();
    BeanUtils.copyProperties(experiment, tExperiment);
    //设置人员时间
    if(experiment.getId()!=null){
      tExperiment.setUpdateTime(new Date());
      tExperiment.setUpdateUser(loginUser.getUserId());
    }else{
      tExperiment.setCreateTime(new Date());
      tExperiment.setCreateUser(loginUser.getUserId());
    }
    //保存实验
    boolean save = experimentService.saveOrUpdate(tExperiment);
    if(!save){
      throw new APIException("保存失败");
    }
    //判断实验文档是否为空
    if(StringUtils.isNotBlank(experiment.getExperimentContent())){
      //查询实验文档
      TExperimentDocuments documents = documentsService.getOne(new QueryWrapper<TExperimentDocuments>().eq("experiment_id", tExperiment.getId()));
      if (documents == null) {
        documents = new TExperimentDocuments();
      }
      //设置实验文档属性
      documents.setExperimentId(tExperiment.getId());
      documents.setExperimentContent(experiment.getExperimentContent());
      //保存实验文档
      boolean doumentSaveRes = documentsService.saveOrUpdate(documents);
      if(!doumentSaveRes){
        throw new APIException("保存失败");
      }
      return doumentSaveRes;
    }
    return save;
  }
}
