package com.highgo.opendbt.verificationSetup.service.impl;

import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckIndexTemp;
import com.highgo.opendbt.temp.service.TCheckIndexTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.model.CheckIndexListSave;
import com.highgo.opendbt.verificationSetup.mapper.TCheckIndexMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckIndexService;
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
 * 保存索引
 */
@Service
public class TCheckIndexServiceImpl extends AbstractCheckService<TCheckIndexMapper, TCheckIndex>
  implements TCheckIndexService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TCheckIndexTempService checkIndexTempService;


  @Override
  public boolean saveCheckIndex(HttpServletRequest request, CheckIndexListSave indexList) {
    List<TCheckIndex> indices = indexList.getIndexList();
    UserInfo userInfo = Authentication.getCurrentUser(request);
    boolean isSave = exerciseService.isSave(indices.get(0).getExerciseId());
    //根据校验点生成答案并验证答案
    generatorTableAnswer(indices, userInfo,isSave,indexList.getSceneId());
    //保存
    return indicesSave(indices,isSave);
  }


  private void generatorTableAnswer(List<TCheckIndex> indices, UserInfo userInfo, boolean isSave, Integer sceneId) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices);
    logger.info("答案sql:" + answerSql.toString());
    Long exerciseId = indices.get(0).getExerciseId();
    String tableName = indices.get(0).getTableName();
    Long sceneDetailId = indices.get(0).getSceneDetailId();
    Long id = indices.get(0).getId();
    //追加上表依赖和字段依赖
    StringBuilder sql = addOtherSql(answerSql, exerciseId, tableName,isSave, sceneDetailId, "OTHER", id);
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneId, indices.get(0).getExerciseId(), sql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean indicesSave(List<TCheckIndex> indices, boolean isSave) {
    if (isSave) {//正式表
      return this.saveOrUpdateBatch(indices);
    } else {//临时表
      List<TCheckIndexTemp> checkIndexTemps = CopyUtils.copyListProperties(indices, TCheckIndexTemp.class);
      return checkIndexTempService.saveOrUpdateBatch(checkIndexTemps);
    }
  }
}




