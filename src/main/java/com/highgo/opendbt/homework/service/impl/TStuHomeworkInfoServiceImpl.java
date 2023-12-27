package com.highgo.opendbt.homework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.highgo.opendbt.homework.domain.model.TStuHomeworkInfoVO;
import com.highgo.opendbt.homework.mapper.TStuHomeworkInfoMapper;
import com.highgo.opendbt.homework.service.TStuHomeworkInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TStuHomeworkInfoServiceImpl extends ServiceImpl<TStuHomeworkInfoMapper, TStuHomeworkInfo>
    implements TStuHomeworkInfoService{
  @Autowired
  TStuHomeworkInfoMapper stuHomeworkInfoMapper;

  @Override
  public List<TStuHomeworkInfo> searchStuHomeworkInfo(int homeworkId) {
    return stuHomeworkInfoMapper.getStuHomeworkInfos(homeworkId);
  }
}




