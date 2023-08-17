package com.highgo.opendbt.homework.service;

import com.highgo.opendbt.homework.domain.entity.TStuHomeworkInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.homework.domain.model.TStuHomeworkInfoVO;

import java.util.List;

/**
 *
 */
public interface TStuHomeworkInfoService extends IService<TStuHomeworkInfo> {
//根据作业查询作业答题详情
  List<TStuHomeworkInfo> searchStuHomeworkInfo(int homeworkId);
}
