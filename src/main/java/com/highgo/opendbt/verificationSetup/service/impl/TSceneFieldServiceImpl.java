package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneFieldMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * 字段相关信息查询
 */
@Service
public class TSceneFieldServiceImpl extends ServiceImpl<TSceneFieldMapper, TSceneField>
  implements TSceneFieldService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TSceneDetailService sceneDetailService;

  @Override
  public VerificationList getFieldList(HttpServletRequest request, long sceneDetailId, int exerciseId) {
    TSceneDetail sceneDetail = sceneDetailService.getById(sceneDetailId);
    VerificationList verificationList = new VerificationList();
    //场景表字段
    List<TSceneField> sceneFields = sceneFieldService.list(new QueryWrapper<TSceneField>().eq("scene_detail_id", sceneDetailId));
    if (!sceneFields.isEmpty()) {
      verificationList.setSceneFields(sceneFields);
    }
    //校验点查询
    List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
      .eq("exercise_id", exerciseId)
      .eq("scene_detail_id", sceneDetailId));
    if (!checkFields.isEmpty()) {
      verificationList.setCheckFields(checkFields);
    }
    //判断场景表信息没有被提取
    if (sceneFields.isEmpty()) {
      // UserInfo userInfo = Authentication.getCurrentUser(request);
      UserInfo userInfo = new UserInfo();
      userInfo.setCode("003");
      //新建模式，新模式下执行初始化场景
      List<TSceneField> fieldObject = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.FIELD, TSceneField.class);
      //保存字段信息到场景字段表
      if (fieldObject != null && !fieldObject.isEmpty()) {
        saveSceneFields(fieldObject, sceneDetailId);
        verificationList.setSceneFields(fieldObject);
      }
    }
    return verificationList;
  }

  @Transactional(rollbackFor = Exception.class)
  public void saveSceneFields(List<TSceneField> fieldObject, long sceneDetailId) {
    fieldObject.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneFieldService.saveBatch(fieldObject);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




