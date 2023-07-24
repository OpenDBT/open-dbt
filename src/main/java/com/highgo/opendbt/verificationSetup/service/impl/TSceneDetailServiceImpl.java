package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.temp.domain.entity.TCheckDetailTemp;
import com.highgo.opendbt.temp.service.TCheckDetailTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneDetailDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.service.TCheckDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.mapper.TSceneDetailMapper;
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
  @Autowired
  TCheckDetailTempService checkDetailTempService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Override
  public VerificationList getSceneDetailList(HttpServletRequest request, Integer sceneId, Long exerciseId) {
    List<TSceneDetail> sceneDetails =null;
    if(sceneId!=-1){
      //场景详情表信息
      sceneDetails = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", sceneId));
    }

   //该题目所有校验信息
    List<TCheckDetail> checkDetails = null;
    if (exerciseId != null) {
      //该习题已保存
      if(exerciseService.isSave(exerciseId)){
        checkDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().eq("exercise_id", exerciseId));
      }else{
        //该习题未保存，查询临时表
        List<TCheckDetailTemp> checkDetailTemps = checkDetailTempService.list(new QueryWrapper<TCheckDetailTemp>().eq("exercise_id", exerciseId));
        checkDetails = CopyUtils.copyListProperties(checkDetailTemps, TCheckDetail.class);
      }

    }
    //返回对象结果
    VerificationList verificationList = new VerificationList();
    //组装的对象方便页面显示
    List<TSceneDetailDisplay> models = new ArrayList<>();

    if (sceneDetails!=null&&!sceneDetails.isEmpty()) {
      verificationList.setSceneDetails(sceneDetails);
      for (TSceneDetail sceneDetail : sceneDetails) {
        TSceneDetailDisplay model = new TSceneDetailDisplay();
        //放入原始数据
        BeanUtils.copyProperties(sceneDetail, model);
        //查询校验数据
        if (checkDetails!=null&&!checkDetails.isEmpty()) {
          List<TCheckDetail> checkDetailList = checkDetails.stream()
            .filter(item -> sceneDetail.getId().equals(item.getSceneDetailId()))
            .collect(Collectors.toList());
          if (checkDetailList != null && checkDetailList.size() == 1) {
            //放入对应校验数据
            model.setDetail(checkDetailList.get(0));
          }
        }
        models.add(model);
      }
    }
    if (checkDetails!=null&&!checkDetails.isEmpty()) {
      verificationList.setCheckDetails(checkDetails);
      //查询新增校验数据
      List<TCheckDetail> checkDetailList = checkDetails.stream()
        .filter(item -> item.getSceneDetailId() == null && item.getExerciseId().equals(exerciseId))
        .collect(Collectors.toList());
      for (TCheckDetail checkDetail : checkDetailList) {
        TSceneDetailDisplay model = new TSceneDetailDisplay();
        model.setDetail(checkDetail);
        models.add(model);
      }
    }
    verificationList.setSceneDetailDisplays(models);
    return verificationList;
  }
}




