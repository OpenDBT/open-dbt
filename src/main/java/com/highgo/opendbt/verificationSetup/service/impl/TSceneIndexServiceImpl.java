package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckIndexTemp;
import com.highgo.opendbt.temp.service.TCheckIndexTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneFKDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneIndexDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneIndexMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckIndexService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneIndexService;
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
 * 查询索引信息
 */
@Service
public class TSceneIndexServiceImpl extends AbstractVerifyService<TSceneIndexMapper, TSceneIndex, TCheckIndex, TSceneIndexDisplay>
  implements TSceneIndexService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneIndexService sceneIndexService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  TCheckIndexTempService checkIndexTempService;

  @Override
  public VerificationList getIndexList(HttpServletRequest request, SearchModel model) {
    return this.getDisplayList(request, model);
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneIndexList(List<TSceneIndex> sceneIndices, long sceneDetailId) {
    sceneIndices.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneIndexService.saveBatch(sceneIndices);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }


  @Override
  protected void setVerificationList(VerificationList verificationList, List<TSceneIndex> entities, List<TSceneIndexDisplay> entityDisplays) {
    verificationList.setSceneIndexList(entities);
    verificationList.setSceneIndexDisplays(entityDisplays);
  }

  @Override
  protected List<TSceneIndex> queryScenes(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneIndex> sceneIndex = querySceneIndex(sceneDetailId, userInfo, exerciseId);
    return sceneIndex;
  }

  private List<TSceneIndex> querySceneIndex(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    if (sceneDetailId == -1) {
      return new ArrayList<>();
    }

    List<TSceneIndex> sceneIndexList = list(new QueryWrapper<TSceneIndex>()
      .eq("scene_detail_id", sceneDetailId));

    if (sceneIndexList.isEmpty()) {
      initSceneIndex(sceneDetailId, userInfo, exerciseId);
      sceneIndexList = list(new QueryWrapper<TSceneIndex>()
        .eq("scene_detail_id", sceneDetailId));
    }

    return sceneIndexList;
  }

  private void initSceneIndex(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneIndex> constraintList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId,
      TableInfoEvent.INDEX, TSceneIndex.class);

    if (constraintList!=null&&!constraintList.isEmpty()) {
      saveSceneIndexList(constraintList, sceneDetailId);
    }
  }

  @Override
  protected List<TCheckIndex> queryChecks(Long exerciseId, Long sceneDetailId, String tableName) {
    return queryCheckIndexes(exerciseId, sceneDetailId, tableName);
  }

  private List<TCheckIndex> queryCheckIndexes(Long exerciseId, Long sceneDetailId, String tableName) {
    if (exerciseService.isSave(exerciseId)) {
      return checkIndexService.list(new QueryWrapper<TCheckIndex>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
    } else {
      List<TCheckIndexTemp> seqTemps = checkIndexTempService.list(new QueryWrapper<TCheckIndexTemp>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
      return CopyUtils.copyListProperties(seqTemps, TCheckIndex.class);
    }
  }

  @Override
  protected void addCheckToDisplay(List<TCheckIndex> checkIndices, VerificationList verificationList) {
    List<TSceneIndexDisplay> sceneIndexDisplays =  verificationList.getSceneIndexDisplays()==null?new ArrayList<>():verificationList.getSceneIndexDisplays();
    checkIndices.forEach(checkIndex -> {
      if (checkIndex.getSceneIndexId() == null) {
        sceneIndexDisplays.add(new TSceneIndexDisplay().setDetail(checkIndex));
      } else {
        sceneIndexDisplays.stream()
          .filter(item -> item.getId() != null && item.getId().equals(checkIndex.getSceneIndexId()))
          .forEach(item -> item.setDetail(checkIndex));
      }
    });
    verificationList.setSceneIndexDisplays(sceneIndexDisplays);
    verificationList.setCheckIndexList(checkIndices);

  }

  @Override
  protected TSceneIndexDisplay createDisplayEntity() {
    return new TSceneIndexDisplay();
  }
}


