package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TSceneFieldService extends IService<TSceneField> {
//字段相关信息查询
  VerificationList getFieldList(HttpServletRequest request, long sceneDetailId, int exerciseId);
}
