package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckSequensSave;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TCheckSeqService extends IService<TCheckSeq> {
  //保存序列
  boolean saveCheckSequence(HttpServletRequest request, CheckSequensSave sequensSave);
}
