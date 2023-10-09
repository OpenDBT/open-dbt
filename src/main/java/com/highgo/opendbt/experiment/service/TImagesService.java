package com.highgo.opendbt.experiment.service;

import com.highgo.opendbt.experiment.domain.TImages;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface TImagesService extends IService<TImages> {
//查询所有镜像
  List<TImages> getImages();
}
