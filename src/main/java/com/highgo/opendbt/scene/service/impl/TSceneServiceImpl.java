package com.highgo.opendbt.scene.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.scene.domain.entity.TScene;
import com.highgo.opendbt.scene.service.TSceneService;
import com.highgo.opendbt.scene.mapper.TSceneMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TSceneServiceImpl extends ServiceImpl<TSceneMapper, TScene>
    implements TSceneService{

  @Override
  @Cacheable(value = "scene", key = "#id")
  public TScene getScene(int id) {
    return this.getById(id);
  }
}




