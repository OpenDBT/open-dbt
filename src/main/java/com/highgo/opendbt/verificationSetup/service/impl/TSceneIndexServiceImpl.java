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
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneIndex;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.service.TCheckIndexService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneIndexService;
import com.highgo.opendbt.verificationSetup.mapper.TSceneIndexMapper;
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
 * 查询索引信息
 */
@Service
public class TSceneIndexServiceImpl extends ServiceImpl<TSceneIndexMapper, TSceneIndex>
  implements TSceneIndexService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneIndexService sceneIndexService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  private RunAnswerService runAnswerService;

  @Override
  public VerificationList getIndexList(HttpServletRequest request, long sceneDetailId, int exerciseId) {

    VerificationList verificationList = new VerificationList();
    //查询场景表索引信息
    List<TSceneIndex> sceneIndexList = sceneIndexService.list(new QueryWrapper<TSceneIndex>()
      .eq("scene_detail_id", sceneDetailId));
    if (!sceneIndexList.isEmpty()) {
      verificationList.setSceneIndexList(sceneIndexList);
    }
    //查询校验表索引信息
    List<TCheckIndex> checkIndexList = checkIndexService.list(new QueryWrapper<TCheckIndex>()
      .eq("scene_detail_id", sceneDetailId)
      .eq("exercise_id", exerciseId));
    if (!checkIndexList.isEmpty()) {
      verificationList.setCheckIndexList(checkIndexList);
    }
    //索引信息为空，查询一下索引信息
    if (sceneIndexList.isEmpty()) {
      // UserInfo userInfo = Authentication.getCurrentUser(request);
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //提取表的索引信息
      List<TSceneIndex> indexList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.INDEX, TSceneIndex.class);
      //保存字段信息到场景字段表
      if (indexList != null && !indexList.isEmpty()) {
        saveSceneIndexList(indexList, sceneDetailId);
        verificationList.setSceneIndexList(indexList);
      }
    }
    return verificationList;
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneIndexList(List<TSceneIndex> indexList, long sceneDetailId) {
    indexList.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneIndexService.saveBatch(indexList);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




