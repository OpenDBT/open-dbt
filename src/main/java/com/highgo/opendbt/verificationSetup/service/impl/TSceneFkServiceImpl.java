package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckFkTemp;
import com.highgo.opendbt.temp.service.TCheckFkTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneFk;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneConstraintDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneFKDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneFkMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckFkService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneFkService;
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

/**
 * 外键外键查询
 */
@Service
public class TSceneFkServiceImpl  extends AbstractVerifyService<TSceneFkMapper, TSceneFk, TCheckFk, TSceneFKDisplay>
  implements TSceneFkService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneFkService sceneFkService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TCheckFkTempService checkFkTempService;

  @Override
  public VerificationList getForeignKeyList(HttpServletRequest request, SearchModel model) {
    return this.getDisplayList(request, model);
  }


  private List<TSceneFk> querySceneFks(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    if (sceneDetailId == -1) {
      return new ArrayList<>();
    }

    List<TSceneFk> sceneFks = list(new QueryWrapper<TSceneFk>()
      .eq("scene_detail_id", sceneDetailId));

    if (sceneFks.isEmpty()) {
      initSceneFk(sceneDetailId, userInfo, exerciseId);
      sceneFks = list(new QueryWrapper<TSceneFk>()
        .eq("scene_detail_id", sceneDetailId));
    }

    return sceneFks;
  }

  private List<TCheckFk> queryCheckFks(Long exerciseId, Long sceneDetailId, String tableName) {
    if (exerciseService.isSave(exerciseId)) {
      return checkFkService.list(new QueryWrapper<TCheckFk>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
    } else {
      List<TCheckFkTemp> constraintTemps = checkFkTempService.list(new QueryWrapper<TCheckFkTemp>()
        .eq(sceneDetailId != -1, "scene_detail_id", sceneDetailId)
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
      return CopyUtils.copyListProperties(constraintTemps, TCheckFk.class);
    }
  }


  @Override
  protected void setVerificationList(VerificationList verificationList, List<TSceneFk> entities, List<TSceneFKDisplay> entityDisplays) {
    verificationList.setSceneFks(entities);
    verificationList.setSceneFKDisplays(entityDisplays);
  }

  @Override
  protected List<TSceneFk> queryScenes(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    //查询初始化的外键信息，无外键信息根据初始化sql重新提取
    List<TSceneFk> sceneFks = querySceneFks(sceneDetailId, userInfo, exerciseId);
    return sceneFks;
  }

  @Override
  protected List<TCheckFk> queryChecks(Long exerciseId, Long sceneDetailId, String tableName) {
    //查询外键的校验信息
    List<TCheckFk> checkFks = queryCheckFks(exerciseId, sceneDetailId, tableName);
    return checkFks;
  }
  @Override
  protected void addCheckToDisplay(List<TCheckFk> checkFks, VerificationList verificationList) {
    List<TSceneFKDisplay> sceneFKDisplays =  verificationList.getSceneFKDisplays()==null?new ArrayList<>():verificationList.getSceneFKDisplays();
    checkFks.forEach(checkFk -> {
      if (checkFk.getSceneFkId() == null) {
        sceneFKDisplays.add(new TSceneFKDisplay().setDetail(checkFk));
      } else {
        sceneFKDisplays.stream()
          .filter(item -> item.getId() != null && item.getId().equals(checkFk.getSceneFkId()))
          .forEach(item -> item.setDetail(checkFk));
      }
    });
    verificationList.setSceneFKDisplays(sceneFKDisplays);
    verificationList.setCheckFks(checkFks);
  }

  @Override
  protected TSceneFKDisplay createDisplayEntity() {
    TSceneFKDisplay display = new TSceneFKDisplay();
    return display;
  }

  private void initSceneFk(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneFk> constraintList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId,
      TableInfoEvent.FOREIGN_KEY, TSceneFk.class);

    if (constraintList!=null&&!constraintList.isEmpty()) {
      saveSceneFkList(constraintList, sceneDetailId);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void saveSceneFkList(List<TSceneFk> fks, Long sceneDetailId) {
    fks.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean success = saveBatch(fks);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(success);
  }
}

