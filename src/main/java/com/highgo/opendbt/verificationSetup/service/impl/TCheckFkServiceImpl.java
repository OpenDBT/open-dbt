package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckFk;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFksSave;
import com.highgo.opendbt.verificationSetup.service.TCheckFkService;
import com.highgo.opendbt.verificationSetup.mapper.TCheckFkMapper;
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
 * 保存外键校验点并验证校验点是否正确
 */
@Service
public class TCheckFkServiceImpl extends ServiceImpl<TCheckFkMapper, TCheckFk>
  implements TCheckFkService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;

  @Override
  public boolean saveCheckForeignKey(HttpServletRequest request, CheckFksSave fks) {
    List<TCheckFk> checkFks = fks.getCheckFks();
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");
    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkFks, userInfo);
    //保存
    return fksSave(checkFks);
  }

  private void generatorTableAnswer(List<TCheckFk> checkFks, UserInfo userInfo) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FOREIGN_KEY).generatorAnswer(checkFks);
    logger.info("答案sql:" + answerSql.toString());
    //筛选有场景的校验
    List<TCheckFk> tCheckIndices = checkFks.stream().filter(item -> item.getSceneDetailId() != null).collect(Collectors.toList());
    TSceneDetail sceneDetail = null;
    if (!tCheckIndices.isEmpty()) {
      //查询场景详情
      sceneDetail = sceneDetailService.getById(tCheckIndices.get(0).getSceneDetailId());
    }
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneDetail == null ? -1 : sceneDetail.getSceneId(), checkFks.get(0).getExerciseId(), answerSql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean fksSave(List<TCheckFk> checkFks) {
    return this.saveOrUpdateBatch(checkFks);
  }
}




