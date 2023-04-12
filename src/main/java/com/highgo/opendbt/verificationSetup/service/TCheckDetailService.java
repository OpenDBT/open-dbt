package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckDetailAndFields;
import com.highgo.opendbt.verificationSetup.domain.model.NewTableInfoDel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface TCheckDetailService extends IService<TCheckDetail> {
  //保存表校验表信息
  boolean saveCheckDetail(HttpServletRequest request, TCheckDetail detail) throws Exception;

  //保存表和字段信息
  boolean saveCheckDetailAndFields(HttpServletRequest request, CheckDetailAndFields detail);

  //删除相关校验信息
  boolean deleteNewTableInfo(HttpServletRequest request, NewTableInfoDel infoDel);
}
