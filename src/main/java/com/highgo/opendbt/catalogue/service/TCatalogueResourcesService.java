package com.highgo.opendbt.catalogue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.catalogue.domain.entity.TCatalogueResources;

/**
 *
 */
public interface TCatalogueResourcesService extends IService<TCatalogueResources> {
  //根据资源id查询是资源是否在使用
  boolean resourcesUse(int resourcesId);
}
