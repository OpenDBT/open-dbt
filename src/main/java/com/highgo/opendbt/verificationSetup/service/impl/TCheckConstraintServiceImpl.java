package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckConstraint;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.CheckConstraintsSave;
import com.highgo.opendbt.verificationSetup.service.TCheckConstraintService;
import com.highgo.opendbt.verificationSetup.mapper.TCheckConstraintMapper;
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
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class TCheckConstraintServiceImpl extends ServiceImpl<TCheckConstraintMapper, TCheckConstraint>
    implements TCheckConstraintService{
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Override
  public boolean saveCheckConstraint(HttpServletRequest request, CheckConstraintsSave constraints) {

   List<TCheckConstraint> checkConstraints = constraints.getCheckConstraints();
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");
    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkConstraints, userInfo);
    //保存
    return constraintsSave(checkConstraints);
  }

  private void generatorTableAnswer(List<TCheckConstraint> constraints, UserInfo userInfo) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.CONSTRAINT).generatorAnswer(constraints);
    logger.info("答案sql:" + answerSql.toString());
    //筛选有场景的约束校验
    List<TCheckConstraint> tCheckIndices = constraints.stream().filter(item -> item.getSceneDetailId() != null).collect(Collectors.toList());
    TSceneDetail sceneDetail = null;
    if (!tCheckIndices.isEmpty()) {
      //查询场景详情
      sceneDetail = sceneDetailService.getById(tCheckIndices.get(0).getSceneDetailId());
    }
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneDetail == null ? -1 : sceneDetail.getSceneId(), constraints.get(0).getExerciseId(), answerSql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean constraintsSave(List<TCheckConstraint> constraints) {
    return this.saveOrUpdateBatch(constraints);
  }
}




