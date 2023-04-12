package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TSceneConstraintService extends IService<TSceneConstraint> {
  //约束信息查询
  VerificationList getConstraintList(HttpServletRequest request, long sceneDetailId, int exerciseId);
}
