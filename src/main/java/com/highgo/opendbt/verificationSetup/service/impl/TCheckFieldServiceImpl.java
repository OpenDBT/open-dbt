package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckFieldTemp;
import com.highgo.opendbt.temp.service.TCheckDetailTempService;
import com.highgo.opendbt.temp.service.TCheckFieldTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneDetail;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.model.CheckFieldsSave;
import com.highgo.opendbt.verificationSetup.mapper.TCheckFieldMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckDetailService;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验字段实现类
 */
@Service
public class TCheckFieldServiceImpl extends AbstractCheckService<TCheckFieldMapper, TCheckField>
  implements TCheckFieldService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TCheckFieldTempService checkFieldTempService;

  @Autowired
  private TSceneFieldService sceneFieldService;

  @Autowired
  private TCheckDetailTempService checkDetailTempService;

  @Autowired
  private TCheckDetailService checkDetailService;

  @Override
  public boolean saveCheckField(HttpServletRequest request, CheckFieldsSave fields) {
    UserInfo userInfo = Authentication.getCurrentUser(request);
    List<TCheckField> checkFields = fields.getFields();
    boolean isSave = exerciseService.isSave(checkFields.get(0).getExerciseId());
    //校验字段名称
    validCheckFields(checkFields);

    //根据校验点生成答案并验证答案
    generatorTableAnswer(checkFields, userInfo, isSave,fields.getSceneId());
    //保存
    return fieldsSave(checkFields);
  }

  private void validCheckFields(List<TCheckField> checkFields) {
    if (exerciseService.isSave(checkFields.get(0).getExerciseId())) {
      for (TCheckField checkField : checkFields) {

        //查询字段表不能存在相同的字段名
        TCheckField tCheckField = this.getOne(new QueryWrapper<TCheckField>().eq("field_name", checkField.getFieldName()).eq("table_name", checkField.getTableName()).eq("exercise_id", checkField.getExerciseId()));
        TSceneField tSceneField = sceneFieldService.getOne(new QueryWrapper<TSceneField>().in("field_name", checkField.getFieldName()).eq("scene_detail_id", checkField.getSceneDetailId()));
        if (checkField.getCheckStatus().equals("UPDATE")) {
          if (tCheckField != null && !tCheckField.getId().equals(checkField.getId())) {
            throw new APIException("一张表中不允许出现给相同的字段名");
          }
        }

        if (checkField.getCheckStatus().equals("INSERT")) {
          if ((tCheckField != null && !tCheckField.getId().equals(checkField.getId())) ||
            (tSceneField != null && !tSceneField.getId().equals(checkField.getId()))
          ) {
            throw new APIException("一张表中不允许出现给相同的字段名");
          }
        }
      }
    } else {
      for (TCheckField checkField : checkFields) {
        //查询字段表不能存在相同的字段名
        TCheckFieldTemp tCheckField = checkFieldTempService.getOne(new QueryWrapper<TCheckFieldTemp>().eq("field_name", checkField.getFieldName()).eq("table_name", checkField.getTableName()).eq("exercise_id", checkField.getExerciseId()));
        TSceneField tSceneField = sceneFieldService.getOne(new QueryWrapper<TSceneField>().in("field_name", checkField.getFieldName()).eq("scene_detail_id", checkField.getSceneDetailId()));
        if (checkField.getCheckStatus().equals("UPDATE")) {
          if (tCheckField != null && !tCheckField.getId().equals(checkField.getId())) {
            throw new APIException("一张表中不允许出现给相同的字段名");
          }
        }
        if (checkField.getCheckStatus().equals("INSERT")) {
          if ((tCheckField != null && !tCheckField.getId().equals(checkField.getId())) ||
            (tSceneField != null && !tSceneField.getId().equals(checkField.getId()))
          ) {
            throw new APIException("一张表中不允许出现给相同的字段名");
          }
        }
      }
    }
  }

  private void generatorTableAnswer(List<TCheckField> checkFields, UserInfo userInfo, boolean isSave,  Integer sceneId) {
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.FIELD).generatorAnswer(checkFields);

    //查询出表级sql以备追加到字段sql上
    Long exerciseId = checkFields.get(0).getExerciseId();
    String tableName = checkFields.get(0).getTableName();
    Long sceneDetailId = checkFields.get(0).getSceneDetailId();
    Long id = checkFields.get(0).getId();
    StringBuilder sql = addOtherSql(answerSql, exerciseId, tableName, isSave, sceneDetailId, "FIELD",id);
    logger.info("答案sql:" + answerSql.toString());
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, sceneId, checkFields.get(0).getExerciseId(), sql);
  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean fieldsSave(List<TCheckField> checkFields) {
    if (exerciseService.isSave(checkFields.get(0).getExerciseId())) {
      return this.saveOrUpdateBatch(checkFields);
    } else {
      return checkFieldTempService.saveOrUpdateBatch(CopyUtils.copyListProperties(checkFields, TCheckFieldTemp.class));
    }

  }
}




