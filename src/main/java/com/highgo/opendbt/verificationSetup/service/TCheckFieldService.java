package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFieldsSave;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TCheckFieldService extends IService<TCheckField> {
  //保存校验字段
  boolean saveCheckField(HttpServletRequest request, CheckFieldsSave fields);
}
