package com.highgo.opendbt.experiment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.experiment.domain.TImages;
import com.highgo.opendbt.experiment.service.TImagesService;
import com.highgo.opendbt.experiment.mapper.TImagesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TImagesServiceImpl extends ServiceImpl<TImagesMapper, TImages>
    implements TImagesService{

  @Override
  public List<TImages> getImages() {
    return this.list();
  }
}




