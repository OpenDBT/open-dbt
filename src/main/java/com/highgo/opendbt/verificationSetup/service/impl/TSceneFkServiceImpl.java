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
import com.highgo.opendbt.verificationSetup.mapper.TSceneFkMapper;
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
 * 外键约束查询
 */
@Service
public class TSceneFkServiceImpl extends ServiceImpl<TSceneFkMapper, TSceneFk>
  implements TSceneFkService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TSceneFkService sceneFkService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  private RunAnswerService runAnswerService;

  @Override
  public VerificationList getForeignKeyList(HttpServletRequest request, long sceneDetailId, int exerciseId) {

    VerificationList verificationList = new VerificationList();
    //查询场景表约束信息
    List<TSceneFk> sceneFks = sceneFkService.list(new QueryWrapper<TSceneFk>()
      .eq("scene_detail_id", sceneDetailId));
    if (!sceneFks.isEmpty()) {
      verificationList.setSceneFks(sceneFks);
    }
    //查询校验表外键信息
    List<TCheckFk> checkFks = checkFkService.list(new QueryWrapper<TCheckFk>()
      .eq("scene_detail_id", sceneDetailId)
      .eq("exercise_id", exerciseId));
    if (!checkFks.isEmpty()) {
      verificationList.setCheckFks(checkFks);
    }
    //外键信息为空，查询一下外键束信息
    if (sceneFks.isEmpty()) {
      // UserInfo userInfo = Authentication.getCurrentUser(request);
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //提取表的外键信息
      List<TSceneFk> sceneFkList = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.FOREIGN_KEY, TSceneFk.class);
      //保存字段信息到场景字段表
      if (sceneFkList != null && !sceneFkList.isEmpty()) {
        saveSceneFkList(sceneFkList, sceneDetailId);
        verificationList.setSceneFks(sceneFkList);
      }
    }
    return verificationList;
  }


  @Transactional(rollbackFor = Exception.class)
  public void saveSceneFkList(List<TSceneFk> fks, long sceneDetailId) {
    fks.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneFkService.saveBatch(fks);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




