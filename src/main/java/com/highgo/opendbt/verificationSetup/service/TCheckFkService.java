package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFksSave;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TCheckFkService extends IService<TCheckFk> {
  //保存外键
  boolean saveCheckForeignKey(HttpServletRequest request, CheckFksSave fks);
}
