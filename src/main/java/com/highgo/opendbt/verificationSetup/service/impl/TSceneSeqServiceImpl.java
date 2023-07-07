package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckSeqTemp;
import com.highgo.opendbt.temp.service.TCheckSeqTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneSeq;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneIndexDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneSeqDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneSeqMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckSeqService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneSeqService;
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
 * 序列查询
 */
@Service
public class TSceneSeqServiceImpl extends AbstractVerifyService<TSceneSeqMapper, TSceneSeq, TCheckSeq, TSceneSeqDisplay>
  implements TSceneSeqService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneSeqService sceneSeqService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  private TCheckSeqTempService checkSeqTempService;

  @Override
  public VerificationList getSequenceList(HttpServletRequest request, SearchModel model) {
    return this.getDisplayList(request, model);
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneSeqList(List<TSceneSeq> sceneSeqs, long sceneDetailId) {
    sceneSeqs.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneSeqService.saveBatch(sceneSeqs);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }


  @Override
  protected void setVerificationList(VerificationList verificationList, List<TSceneSeq> entities, List<TSceneSeqDisplay> entityDisplays) {
    verificationList.setSceneSeqs(entities);
    verificationList.setSceneSeqDisplays(entityDisplays);
  }

  @Override
  protected List<TSceneSeq> queryScenes(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneSeq> sceneConstraints = querySceneConstraints(sceneDetailId, userInfo, exerciseId);
    return sceneConstraints;
  }

  private List<TSceneSeq> querySceneConstraints(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    if (sceneDetailId == -1) {
      return new ArrayList<>();
    }

    List<TSceneSeq> sceneSeqs = list(new QueryWrapper<TSceneSeq>()
      .eq("scene_detail_id", sceneDetailId));

    if (sceneSeqs.isEmpty()) {
      initSceneSeq(sceneDetailId, userInfo, exerciseId);
      sceneSeqs = list(new QueryWrapper<TSceneSeq>()
        .eq("scene_detail_id", sceneDetailId));
    }

    return sceneSeqs;
  }

  private void initSceneSeq(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    List<TSceneSeq> constraintList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId,
      TableInfoEvent.SEQUENCE, TSceneSeq.class);

    if (constraintList!=null&&!constraintList.isEmpty()) {
      saveSceneSeqList(constraintList, sceneDetailId);
    }
  }

  @Override
  protected List<TCheckSeq> queryChecks(Long exerciseId, Long sceneDetailId, String tableName) {
    return queryCheckSeqs(exerciseId, sceneDetailId, tableName);
  }

  private List<TCheckSeq> queryCheckSeqs(Long exerciseId, Long sceneDetailId, String tableName) {
    if (exerciseService.isSave(exerciseId)) {
      return checkSeqService.list(new QueryWrapper<TCheckSeq>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
    } else {
      List<TCheckSeqTemp> seqTemps = checkSeqTempService.list(new QueryWrapper<TCheckSeqTemp>()
        .eq("exercise_id", exerciseId)
        .eq(sceneDetailId!=null&&sceneDetailId!=-1,"scene_detail_id", sceneDetailId)
        .eq(sceneDetailId==null||sceneDetailId == -1, "table_name", tableName));
      return CopyUtils.copyListProperties(seqTemps, TCheckSeq.class);
    }
  }

  @Override
  protected void addCheckToDisplay(List<TCheckSeq> checkSeqs, VerificationList verificationList) {
    List<TSceneSeqDisplay> sceneSeqDisplay =  verificationList.getSceneSeqDisplays()==null?new ArrayList<>():verificationList.getSceneSeqDisplays();
    checkSeqs.forEach(checkSeq -> {
      if (checkSeq.getSceneSeqId() == null) {
        sceneSeqDisplay.add(new TSceneSeqDisplay().setDetail(checkSeq));
      } else {
        sceneSeqDisplay.stream()
          .filter(item -> item.getId() != null && item.getId().equals(checkSeq.getSceneSeqId()))
          .forEach(item -> item.setDetail(checkSeq));
      }
    });
    verificationList.setSceneSeqDisplays(sceneSeqDisplay);
    verificationList.setCheckSeqs(checkSeqs);
  }

  @Override
  protected TSceneSeqDisplay createDisplayEntity() {
    return new TSceneSeqDisplay();
  }
}




