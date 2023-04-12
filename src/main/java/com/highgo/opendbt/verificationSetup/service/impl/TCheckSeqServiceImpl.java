package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.CheckSequensSave;
import com.highgo.opendbt.verificationSetup.service.TCheckSeqService;
import com.highgo.opendbt.verificationSetup.mapper.TCheckSeqMapper;
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
public class TCheckSeqServiceImpl extends ServiceImpl<TCheckSeqMapper, TCheckSeq>
  implements TCheckSeqService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Override
  public boolean saveCheckSequence(HttpServletRequest request, CheckSequensSave checkSequensSave) {
    List<TCheckSeq> sequences = checkSequensSave.getCheckSeqs();
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");
    //根据校验点生成答案并验证答案
    generatorTableAnswer(sequences, userInfo);
    //保存
    return sequencesSave(sequences);
  }
  private void generatorTableAnswer(List<TCheckSeq> checkSequences, UserInfo userInfo) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(checkSequences);
    logger.info("答案sql:" + answerSql.toString());
    //筛选有场景的校验
    List<TCheckSeq> sequenceList = checkSequences.stream().filter(item -> item.getSceneDetailId() != null).collect(Collectors.toList());
    TSceneDetail sceneDetail = null;
    if (!sequenceList.isEmpty()) {
      //查询场景详情
      sceneDetail = sceneDetailService.getById(sequenceList.get(0).getSceneDetailId());
    }
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneDetail == null ? -1 : sceneDetail.getSceneId(), checkSequences.get(0).getExerciseId(), answerSql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean sequencesSave(List<TCheckSeq> sequences) {
    return this.saveOrUpdateBatch(sequences);
  }
}




