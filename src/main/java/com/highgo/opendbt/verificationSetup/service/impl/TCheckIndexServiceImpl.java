package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckIndex;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.CheckIndexListSave;
import com.highgo.opendbt.verificationSetup.service.TCheckIndexService;
import com.highgo.opendbt.verificationSetup.mapper.TCheckIndexMapper;
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
 * 保存索引
 */
@Service
public class TCheckIndexServiceImpl extends ServiceImpl<TCheckIndexMapper, TCheckIndex>
  implements TCheckIndexService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;

  @Override
  public boolean saveCheckIndex(HttpServletRequest request, CheckIndexListSave indexList) {
    List<TCheckIndex> indices = indexList.getIndexList();
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");
    //根据校验点生成答案并验证答案
    generatorTableAnswer(indices, userInfo);
    //保存
    return indicesSave(indices);
  }


  private void generatorTableAnswer(List<TCheckIndex> indices, UserInfo userInfo) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.INDEX).generatorAnswer(indices);
    logger.info("答案sql:" + answerSql.toString());
    //筛选有场景的索引校验
    List<TCheckIndex> tCheckIndices = indices.stream().filter(item -> item.getSceneDetailId() != null).collect(Collectors.toList());
    TSceneDetail sceneDetail = null;
    if (!tCheckIndices.isEmpty()) {
      //查询场景详情
      sceneDetail = sceneDetailService.getById(tCheckIndices.get(0).getSceneDetailId());
    }
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneDetail == null ? -1 : sceneDetail.getSceneId(), indices.get(0).getExerciseId(), answerSql);

  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean indicesSave(List<TCheckIndex> indices) {
    return this.saveOrUpdateBatch(indices);
  }
}




