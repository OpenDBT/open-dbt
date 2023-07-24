package com.highgo.opendbt.verificationSetup.service;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.verificationSetup.domain.model.CheckIndexListSave;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TCheckIndexService extends IService<TCheckIndex> {
    //保存索引
    boolean saveCheckIndex(HttpServletRequest request, CheckIndexListSave indexList);
}
