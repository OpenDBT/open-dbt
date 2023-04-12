package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneDetailModel;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.service.TCheckDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.mapper.TSceneDetailMapper;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表级信息查询
 */
@Service
public class TSceneDetailServiceImpl extends ServiceImpl<TSceneDetailMapper, TSceneDetail>
  implements TSceneDetailService {
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckDetailService checkDetailService;

  @Override
  public VerificationList getSceneDetailList(HttpServletRequest request, int sceneId, int exerciseId) {
    List<TSceneDetail> sceneDetails = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", sceneId));
    List<TCheckDetail> checkDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", exerciseId));
    VerificationList verificationList = new VerificationList();
    List<TSceneDetailModel> models = new ArrayList<>();
    if (!checkDetails.isEmpty()) {
      verificationList.setCheckDetails(checkDetails);
      //查询新增校验数据
      List<TCheckDetail> checkDetailList = checkDetails.stream()
        .filter(item -> item.getSceneDetailId() == null && item.getExerciseId() == exerciseId)
        .collect(Collectors.toList());
      for (TCheckDetail checkDetail : checkDetailList) {
        TSceneDetailModel model = new TSceneDetailModel();
        model.setDetail(checkDetail);
        models.add(model);
      }
    }
    if (!sceneDetails.isEmpty()) {
      verificationList.setSceneDetails(sceneDetails);
      for (TSceneDetail sceneDetail : sceneDetails) {
        TSceneDetailModel model = new TSceneDetailModel();
        //放入原始数据
        BeanUtils.copyProperties(sceneDetail, model);
        //查询校验数据
        if (!checkDetails.isEmpty()) {
          List<TCheckDetail> checkDetailList = checkDetails.stream()
            .filter(item -> verificationList.equals(item.getSceneDetailId()))
            .collect(Collectors.toList());
          if (checkDetailList != null && checkDetailList.size() == 1) {
            //放入对应校验数据
            model.setDetail(checkDetailList.get(0));
          }
        }
        models.add(model);
      }
    }
    verificationList.setSceneDetailModels(models);
    return verificationList;
  }
}




