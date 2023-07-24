package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TSceneIndexService extends IService<TSceneIndex> {
  //查询索引信息
  VerificationList getIndexList(HttpServletRequest request, SearchModel model);
}
