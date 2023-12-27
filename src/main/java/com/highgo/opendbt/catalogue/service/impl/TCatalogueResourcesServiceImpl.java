package com.highgo.opendbt.catalogue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.catalogue.mapper.TCatalogueResourcesMapper;
import com.highgo.opendbt.catalogue.domain.entity.TCatalogueResources;
import com.highgo.opendbt.catalogue.service.TCatalogueResourcesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TCatalogueResourcesServiceImpl extends ServiceImpl<TCatalogueResourcesMapper, TCatalogueResources>
  implements TCatalogueResourcesService {

  public boolean resourcesUse(int resourcesId) {
    List<TCatalogueResources> catalogueResources = this.list(new QueryWrapper<TCatalogueResources>()
      .eq("resources_id", resourcesId)
      .eq("delete_flag", 0));
    if (catalogueResources != null && catalogueResources.size() > 0) {
      return true;
    } else {
    }
    return false;
  }
}




