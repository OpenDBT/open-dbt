package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneConstraintMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckConstraintService;
import com.highgo.opendbt.verificationSetup.service.TSceneConstraintService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@Service
public class TSceneConstraintServiceImpl extends ServiceImpl<TSceneConstraintMapper, TSceneConstraint>
  implements TSceneConstraintService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneConstraintService sceneConstraintService;
  @Autowired
  TCheckConstraintService checkConstraintService;

  @Override
  public VerificationList getConstraintList(HttpServletRequest request, long sceneDetailId, int exerciseId) {

    VerificationList verificationList = new VerificationList();
    //查询场景表约束信息
    List<TSceneConstraint> sceneConstraints = sceneConstraintService.list(new QueryWrapper<TSceneConstraint>()
      .eq("scene_detail_id", sceneDetailId));
    if (!sceneConstraints.isEmpty()) {
      verificationList.setSceneConstraints(sceneConstraints);
    }
    //查询校验表约束信息
    List<TCheckConstraint> checkConstraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
      .eq("scene_detail_id", sceneDetailId)
      .eq("exercise_id", exerciseId));
    if (!checkConstraints.isEmpty()) {
      verificationList.setCheckConstraints(checkConstraints);
    }
    //约束信息为空，查询一下约束信息
    if (sceneConstraints.isEmpty()) {
      // UserInfo userInfo = Authentication.getCurrentUser(request);
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //提取表的索引信息
      List<TSceneConstraint> constraintList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.CONSTRAINT, TSceneConstraint.class);
      //保存字段信息到场景字段表
      if (constraintList != null && !constraintList.isEmpty()) {
        saveSceneConstraintList(constraintList, sceneDetailId);
        verificationList.setSceneConstraints(constraintList);
      }
    }
    return verificationList;
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneConstraintList(List<TSceneConstraint> constraints, long sceneDetailId) {
    constraints.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneConstraintService.saveBatch(constraints);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




