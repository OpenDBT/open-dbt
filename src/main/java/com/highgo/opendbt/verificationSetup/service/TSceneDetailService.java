package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TSceneDetailService extends IService<TSceneDetail> {
  //查询表相关信息（场景表的和校验表的）
  VerificationList getSceneDetailList(HttpServletRequest request, int sceneId, int exerciseId);
}
