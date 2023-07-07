package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckConstraintTemp;
import com.highgo.opendbt.temp.service.TCheckConstraintTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneConstraint;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneConstraintDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneDetailDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneConstraintMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckConstraintService;
import com.highgo.opendbt.verificationSetup.service.TSceneConstraintService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class TSceneConstraintServiceImpl extends AbstractVerifyService<TSceneConstraintMapper, TSceneConstraint, TCheckConstraint, TSceneConstraintDisplay>
  implements TSceneConstraintService {
  private static final Logger logger = LoggerFactory.getLogger(TSceneConstraintServiceImpl.class);


  @Autowired
  private TCheckConstraintService checkConstraintService;


  @Autowired
  private TCheckConstraintTempService checkConstraintTempService;

  @Override
  public VerificationList getConstraintList(HttpServletRequest request, SearchModel model) {
    return this.getDisplayList(request, model);
  }


  private List<TSceneConstraint> querySceneConstraints(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    if (sceneDetailId == -1) {
      return new ArrayList<>();
    }

    List<TSceneConstraint> sceneConstraints = list(new QueryWrapper<TSceneConstraint>()
      .eq("scene_detail_id", sceneDetailId));

    if (sceneConstraints.isEmpty()) {
      initSceneConstraint(sceneDetailId, userInfo, exerciseId);
      sceneConstraints = list(new QueryWrapper<TSceneConstraint>()
        .eq("scene_detail_id", sceneDetailId));
    }

    return sceneConstraints;
  }

  private List<TCheckConstraint> queryCheckConstraints(Long exerciseId, Long sceneDetailId, String tableName) {
    if (exerciseService.isSave(exerciseId)) {
      return checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
    } else {
      List<TCheckConstraintTemp> constraintTemps = checkConstraintTempService.list(new QueryWrapper<TCheckConstraintTemp>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
      return CopyUtils.copyListProperties(constraintTemps, TCheckConstraint.class);
    }
  }

  @Override
  protected void setVerificationList(VerificationList verificationList,List<TSceneConstraint> entities, List<TSceneConstraintDisplay> entityDisplays) {
    verificationList.setSceneConstraints(entities);
    verificationList.setSceneConstraintDisplays(entityDisplays);

  }

  @Override
  protected List<TSceneConstraint> queryScenes(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    //查询初始化的约束信息，无约束信息根据初始化sql重新提取
    List<TSceneConstraint> sceneConstraints = querySceneConstraints(sceneDetailId, userInfo, exerciseId);
    return sceneConstraints;
  }

  @Override
  protected List<TCheckConstraint> queryChecks(Long exerciseId, Long sceneDetailId, String tableName) {
    //查询约束的校验信息
    List<TCheckConstraint> checkConstraints = queryCheckConstraints(exerciseId, sceneDetailId, tableName);
    return checkConstraints;
  }

  @Override
  protected void addCheckToDisplay(List<TCheckConstraint> checkConstraints, VerificationList verificationList) {
    List<TSceneConstraintDisplay> sceneConstraintDisplay =  verificationList.getSceneConstraintDisplays()==null?new ArrayList<>():verificationList.getSceneConstraintDisplays();
    checkConstraints.forEach(checkConstraint -> {
      if (checkConstraint.getSceneConstraintId() == null) {
        sceneConstraintDisplay.add(new TSceneConstraintDisplay().setDetail(checkConstraint));
      } else {
        sceneConstraintDisplay.stream()
          .filter(item -> item.getId() != null && item.getId().equals(checkConstraint.getSceneConstraintId()))
          .forEach(item -> item.setDetail(checkConstraint));
      }
    });
    verificationList.setSceneConstraintDisplays(sceneConstraintDisplay);
    verificationList.setCheckConstraints(checkConstraints);
  }

  @Override
  protected TSceneConstraintDisplay createDisplayEntity() {
    TSceneConstraintDisplay display = new TSceneConstraintDisplay();
    return display;
  }

  private void initSceneConstraint(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneConstraint> constraintList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId,
      TableInfoEvent.CONSTRAINT, TSceneConstraint.class);

    if (constraintList!=null&&!constraintList.isEmpty()) {
      saveSceneConstraintList(constraintList, sceneDetailId);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void saveSceneConstraintList(List<TSceneConstraint> constraints, Long sceneDetailId) {
    constraints.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean success = saveBatch(constraints);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(success);
  }
}
