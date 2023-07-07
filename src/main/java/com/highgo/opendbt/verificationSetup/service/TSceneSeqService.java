package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TSceneSeq;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TSceneSeqService extends IService<TSceneSeq> {
  //序列查询
  VerificationList getSequenceList(HttpServletRequest request, SearchModel model);
}
