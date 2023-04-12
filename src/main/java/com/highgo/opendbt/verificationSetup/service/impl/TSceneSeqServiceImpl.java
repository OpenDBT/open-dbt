package com.highgo.opendbt.verificationSetup.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.mapper.TSceneSeqMapper;
import com.highgo.opendbt.verificationSetup.tools.ResultSetMapper;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static com.highgo.opendbt.verificationSetup.tools.TableInfoUtil.extractTableInfo;

/**
 *序列查询
 */
@Service
public class TSceneSeqServiceImpl extends ServiceImpl<TSceneSeqMapper, TSceneSeq>
  implements TSceneSeqService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneSeqService sceneSeqService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  private RunAnswerService runAnswerService;

  @Override
  public VerificationList getSequenceList(HttpServletRequest request, long sceneDetailId, int exerciseId) {

    VerificationList verificationList = new VerificationList();
    //查询场景表约束信息
    List<TSceneSeq> sceneSeqs = sceneSeqService.list(new QueryWrapper<TSceneSeq>()
      .eq("scene_detail_id", sceneDetailId));
    if (!sceneSeqs.isEmpty()) {
      verificationList.setSceneSeqs(sceneSeqs);
    }
    //查询校验序列信息
    List<TCheckSeq> tCheckSeqs = checkSeqService.list(new QueryWrapper<TCheckSeq>()
      .eq("scene_detail_id", sceneDetailId)
      .eq("exercise_id", exerciseId));
    if (!tCheckSeqs.isEmpty()) {
      verificationList.setCheckSeqs(tCheckSeqs);
    }
    //序列信息为空，查询一下序列信息
    if (sceneSeqs.isEmpty()) {
      // UserInfo userInfo = Authentication.getCurrentUser(request);
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //提取序列信息
      List<TSceneSeq> sceneSeqList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.SEQUENCE, TSceneSeq.class);
      //保存序列信息到场景序列表
      if (sceneSeqList != null && !sceneSeqList.isEmpty()) {
        saveSceneSeqList(sceneSeqList, sceneDetailId);
        verificationList.setSceneSeqs(sceneSeqList);
      }
    }
    return verificationList;
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneSeqList(List<TSceneSeq> sceneSeqs, long sceneDetailId) {
    sceneSeqs.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneSeqService.saveBatch(sceneSeqs);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




