package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFieldsSave;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.mapper.TCheckFieldMapper;
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
 * 校验字段实现类
 */
@Service
public class TCheckFieldServiceImpl extends ServiceImpl<TCheckFieldMapper, TCheckField>
  implements TCheckFieldService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;

  @Override
  public boolean saveCheckField(HttpServletRequest request, CheckFieldsSave fields) {
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");
    List<TCheckField> checkFields = fields.getFields();
    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkFields, userInfo);
    //保存
    return fieldsSave(checkFields);
  }

  private void generatorTableAnswer(List<TCheckField> checkFields, UserInfo userInfo) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(checkFields);
    logger.info("答案sql:" + answerSql.toString());
    //筛选有场景的字段校验
    List<TCheckField> tCheckFields = checkFields.stream().filter(item -> item.getSceneDetailId() != null).collect(Collectors.toList());
    TSceneDetail sceneDetail = null;
    if (!tCheckFields.isEmpty()) {
      //查询场景详情
      sceneDetail = sceneDetailService.getById(tCheckFields.get(0).getSceneDetailId());
    }
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneDetail == null ? -1 : sceneDetail.getSceneId(), checkFields.get(0).getExerciseId(), answerSql);
  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean fieldsSave(List<TCheckField> checkFields) {
    return this.saveOrUpdateBatch(checkFields);
  }
}




