package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CopyUtils;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.temp.domain.entity.TCheckFieldTemp;
import com.highgo.opendbt.temp.service.TCheckFieldTempService;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckField;
import com.highgo.opendbt.verificationSetup.domain.entity.TSceneField;
import com.highgo.opendbt.verificationSetup.domain.model.FieldModel;
import com.highgo.opendbt.verificationSetup.domain.model.TSceneFieldDisplay;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import com.highgo.opendbt.verificationSetup.mapper.TSceneFieldMapper;
import com.highgo.opendbt.verificationSetup.service.TCheckFieldService;
import com.highgo.opendbt.verificationSetup.service.TSceneDetailService;
import com.highgo.opendbt.verificationSetup.service.TSceneFieldService;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段相关信息查询
 */
@Service
public class TSceneFieldServiceImpl extends ServiceImpl<TSceneFieldMapper, TSceneField>
  implements TSceneFieldService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TCheckFieldTempService checkFieldTempService;

  @Override
  public VerificationList getFieldList(HttpServletRequest request, FieldModel model) {
    UserInfo userInfo = Authentication.getCurrentUser(request);
    //存储返回值
    VerificationList verificationList = new VerificationList();
    //场景字段
    List<TSceneField> sceneFields = null;
    //展示使用集合
    List<TSceneFieldDisplay> sceneFieldDisplays = new ArrayList<>();
    //为空时为新增表时没有场景信息表字段
    if (model.getSceneDetailId() != -1) {
      //场景表字段
      sceneFields = sceneFieldService.list(new QueryWrapper<TSceneField>().eq("scene_detail_id", model.getSceneDetailId()));
      if ( sceneFields.isEmpty()) {
        //场景字段为空时，重新提取
        initSceneField(model.getSceneDetailId(), userInfo, model.getExerciseId());
        sceneFields = sceneFieldService.list(new QueryWrapper<TSceneField>().eq("scene_detail_id", model.getSceneDetailId()));
      }

      if (!sceneFields.isEmpty()) {
        verificationList.setSceneFields(sceneFields);
        //赋值给display
        for (TSceneField sourceItem : sceneFields) {
          TSceneFieldDisplay destinationItem = new TSceneFieldDisplay();
          BeanUtils.copyProperties(sourceItem, destinationItem);
          sceneFieldDisplays.add(destinationItem);
        }
      }
    }
    //校验字段
    if (model.getExerciseId() != null) {
      //校验点查询
      List<TCheckField> checkFields = null;
      if (exerciseService.isSave(model.getExerciseId())) {
        checkFields = checkFieldService.list(new QueryWrapper<TCheckField>()
          .eq(model.getSceneDetailId()!=-1,"scene_detail_id",model.getSceneDetailId())
          .eq("exercise_id", model.getExerciseId())
          .eq(model.getSceneDetailId()==-1,"table_name",model.getTableName()));
      } else {
        //在临时表中查询
        List<TCheckFieldTemp> fieldTemps = checkFieldTempService.list(new QueryWrapper<TCheckFieldTemp>()
          .eq(model.getSceneDetailId()!=-1,"scene_detail_id",model.getSceneDetailId())
          .eq("exercise_id", model.getExerciseId())
          .eq(model.getSceneDetailId()==-1,"table_name",model.getTableName()));
        checkFields = CopyUtils.copyListProperties(fieldTemps, TCheckField.class);
      }
      if (!checkFields.isEmpty()) {
        verificationList.setCheckFields(checkFields);
        //添加到展示对象display
        addCheckToDisplay(checkFields, sceneFieldDisplays);
      }
    }
    verificationList.setSceneFieldDisplays(sceneFieldDisplays);
    return verificationList;
  }

  //添加到展示对象display
  private void addCheckToDisplay(List<TCheckField> checkFields, List<TSceneFieldDisplay> sceneFieldDisplays) {
    checkFields.forEach(tCheckField -> {
      if ( tCheckField.getSceneFieldId() == null) {
        sceneFieldDisplays.add(new TSceneFieldDisplay().setDetail(tCheckField));
      } else {
        sceneFieldDisplays.forEach(item -> {
          if (item.getId() != null && item.getId().equals(tCheckField.getSceneFieldId())) {
            item.setDetail(tCheckField);
          }
        });
      }
    });
  }

  //场景字段为空时，重新提取
  private void initSceneField(Long sceneDetailId, UserInfo userInfo, Long exerciseId) {
    //新建模式，新模式下执行初始化场景
    List<TSceneField> fieldObject = TableInfoUtil.getInfo(sceneDetailId, userInfo, exerciseId, TableInfoEvent.FIELD, TSceneField.class);
    //保存字段信息到场景字段表
    if (fieldObject != null && !fieldObject.isEmpty()) {
      saveSceneFields(fieldObject, sceneDetailId);
    }

  }

  @Transactional(rollbackFor = Exception.class)
  public void saveSceneFields(List<TSceneField> fieldObject, long sceneDetailId) {
    fieldObject.forEach(item -> item.setSceneDetailId(sceneDetailId));
    boolean res = sceneFieldService.saveBatch(fieldObject);
    BusinessResponseEnum.SAVEFAIL.assertIsTrue(res);
  }
}




