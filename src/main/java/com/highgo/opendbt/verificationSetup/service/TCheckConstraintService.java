package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckConstraintsSave;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TCheckConstraintService extends IService<TCheckConstraint> {
  //保存约束
  boolean saveCheckConstraint(HttpServletRequest request, CheckConstraintsSave constraints);
}
