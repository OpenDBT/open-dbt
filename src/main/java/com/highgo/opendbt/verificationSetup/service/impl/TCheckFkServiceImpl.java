package com.highgo.opendbt.verificationSetup.service.impl;

import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckFkTemp;
import com.highgo.opendbt.temp.service.TCheckFkTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFksSave;
import com.highgo.opendbt.verificationSetup.mapper.TCheckFkMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckFkService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 保存外键校验点并验证校验点是否正确
 */
@Service
public class TCheckFkServiceImpl extends AbstractCheckService<TCheckFkMapper, TCheckFk> implements TCheckFkService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckFkTempService checkFkTempService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Override
  public boolean saveCheckForeignKey(HttpServletRequest request, CheckFksSave fks) {
    List<TCheckFk> checkFks = fks.getCheckFks();
    UserInfo userInfo = Authentication.getCurrentUser(request);
    boolean isSave = exerciseService.isSave(checkFks.get(0).getExerciseId());
    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkFks, userInfo,isSave,fks.getSceneId());
    //保存
    return fksSave(checkFks,isSave);
  }

  private void generatorTableAnswer(List<TCheckFk> checkFks, UserInfo userInfo, boolean isSave, Integer sceneId) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(checkFks);
    logger.info("答案sql:" + answerSql.toString());
    Long exerciseId = checkFks.get(0).getExerciseId();
    String tableName = checkFks.get(0).getTableName();
    Long sceneDetailId = checkFks.get(0).getSceneDetailId();
    String checkStatus = checkFks.get(0).getCheckStatus();
    Long id = checkFks.get(0).getId();
    //追加上表依赖和字段依赖
    StringBuilder sql = addOtherSql(answerSql, exerciseId, tableName,isSave, sceneDetailId, "OTHER", id);
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneId, checkFks.get(0).getExerciseId(), sql);

  }


  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean fksSave(List<TCheckFk> checkFks,  boolean isSave) {
    if (isSave) {//正式表
      return this.saveOrUpdateBatch(checkFks);
    } else {//临时表
      List<TCheckFkTemp> fkTemps = CopyUtils.copyListProperties(checkFks, TCheckFkTemp.class);
      return checkFkTempService.saveOrUpdateBatch(fkTemps);
    }
  }
}




