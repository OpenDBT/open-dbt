package com.highgo.opendbt.verificationSetup.service.impl;

import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckConstraintTemp;
import com.highgo.opendbt.temp.service.TCheckConstraintTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.CheckConstraintsSave;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.mapper.TCheckConstraintMapper;
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
 *
 */
@Service
public class TCheckConstraintServiceImpl extends AbstractCheckService<TCheckConstraintMapper, TCheckConstraint>
  implements TCheckConstraintService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  private TNewExerciseService exerciseService;

  @Autowired
  private TCheckConstraintTempService checkConstraintTempService;

  @Autowired
  private TSceneConstraintService sceneConstraintService;


  @Override
  public boolean saveCheckConstraint(HttpServletRequest request, CheckConstraintsSave constraints) {

    List<TCheckConstraint> checkConstraints = constraints.getCheckConstraints();
    UserInfo userInfo = Authentication.getCurrentUser(request);
    boolean isSave = exerciseService.isSave(checkConstraints.get(0).getExerciseId());
    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkConstraints, userInfo,isSave,constraints.getSceneId());
    //保存
    return constraintsSave(checkConstraints,isSave);
  }

  private void generatorTableAnswer(List<TCheckConstraint> constraints, UserInfo userInfo, boolean isSave, Integer sceneId) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints);
    logger.info("答案sql:" + answerSql.toString());
    Long exerciseId = constraints.get(0).getExerciseId();
    String tableName = constraints.get(0).getTableName();
    Long sceneDetailId = constraints.get(0).getSceneDetailId();
    String checkStatus = constraints.get(0).getCheckStatus();
    Long id = constraints.get(0).getId();
    //追加上表依赖和字段依赖
    StringBuilder sql = addOtherSql( answerSql,exerciseId,tableName,isSave,sceneDetailId,"OTHER", id);
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneId, constraints.get(0).getExerciseId(), sql);

  }



  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean constraintsSave(List<TCheckConstraint> constraints,boolean isSave) {
    if (isSave) {//正式表
      return this.saveOrUpdateBatch(constraints);
    } else {//临时表
      List<TCheckConstraintTemp> constraintTemps = CopyUtils.copyListProperties(constraints, TCheckConstraintTemp.class);
      return checkConstraintTempService.saveOrUpdateBatch(constraintTemps);
    }
  }

}




