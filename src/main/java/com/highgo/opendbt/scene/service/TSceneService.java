package com.highgo.opendbt.scene.service;

import com.highgo.opendbt.scene.domain.entity.TScene;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface TSceneService extends IService<TScene> {

  TScene getScene(int id);

}
