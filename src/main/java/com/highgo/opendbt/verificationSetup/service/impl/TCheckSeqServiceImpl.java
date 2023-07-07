package com.highgo.opendbt.verificationSetup.service.impl;

import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckSeqTemp;
import com.highgo.opendbt.temp.service.TCheckSeqTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckSeq;
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

/**
 *
 */
@Service
public class TCheckSeqServiceImpl extends AbstractCheckService<TCheckSeqMapper, TCheckSeq>
  implements TCheckSeqService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TCheckSeqTempService checkSeqTempService;
  @Override
  public boolean saveCheckSequence(HttpServletRequest request, CheckSequensSave checkSequensSave) {
    List<TCheckSeq> sequences = checkSequensSave.getCheckSeqs();
     UserInfo userInfo = Authentication.getCurrentUser(request);
    boolean isSave = exerciseService.isSave(sequences.get(0).getExerciseId());
    //根据校验点生成答案并验证答案
    generatorTableAnswer(sequences, userInfo,isSave,checkSequensSave.getSceneId());
    //保存
    return sequencesSave(sequences,isSave);
  }
  private void generatorTableAnswer(List<TCheckSeq> checkSequences, UserInfo userInfo, boolean isSave, Integer sceneId) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.SEQUENCE).generatorAnswer(checkSequences);
    logger.info("答案sql:" + answerSql.toString());
    Long exerciseId = checkSequences.get(0).getExerciseId();
    String tableName = checkSequences.get(0).getTableName();
    Long sceneDetailId = checkSequences.get(0).getSceneDetailId();
    Long id = checkSequences.get(0).getId();
    //追加上表依赖和字段依赖
    StringBuilder sql = addOtherSql( answerSql,exerciseId,tableName,isSave, sceneDetailId, "OTHER", id);
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo,sceneId, checkSequences.get(0).getExerciseId(), sql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean sequencesSave(List<TCheckSeq> sequences, boolean isSave) {
    if (isSave) {//正式表
      return this.saveOrUpdateBatch(sequences);
    } else {//临时表
      List<TCheckSeqTemp> checkSeqTemps = CopyUtils.copyListProperties(sequences, TCheckSeqTemp.class);
      return checkSeqTempService.saveOrUpdateBatch(checkSeqTemps);
    }
  }
}




