package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.model.SearchModel;
import com.highgo.opendbt.verificationSetup.domain.model.VerificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 模板模式抽象类
 * @Title: AbstractVerificationService
 * @Package com.highgo.opendbt.verificationSetup.service.impl
 * @Author: highgo
 * Param: M 对应mapper,T 初始化实体类 ,C 对应的校验类，D 对应的页面显示类display
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/6/16 14:13
 */
@Service
public abstract class AbstractVerifyService<M extends BaseMapper<T>, T extends Object, C extends Object, D extends Object> extends ServiceImpl<M, T> {
  private static final Logger logger = LoggerFactory.getLogger(AbstractVerifyService.class);

  @Autowired
  protected TNewExerciseService exerciseService;

  public VerificationList getDisplayList(HttpServletRequest request, SearchModel model) {
    UserInfo userInfo = Authentication.getCurrentUser(request);
    VerificationList verificationList = new VerificationList();
    Long sceneDetailId = model.getSceneDetailId();
    Long exerciseId = model.getExerciseId();
    String tableName = model.getTableName();
    //查询初始化信息
    List<T> entities = queryScenes(sceneDetailId, userInfo, exerciseId);
    if (!entities.isEmpty()) {
      List<D> entityDisplays = entities.stream()
        .map(entity -> {
          D display = createDisplayEntity();
          BeanUtils.copyProperties(entity, display);
          return display;
        })
        .collect(Collectors.toList());
      setVerificationList(verificationList,entities,entityDisplays);
    }
    //查询校验信息
    List<C> checkInfos = queryChecks(exerciseId, sceneDetailId, tableName);
    if (!checkInfos.isEmpty()) {
      addCheckToDisplay(checkInfos, verificationList);
    }
    return verificationList;
  }

  protected abstract void setVerificationList(VerificationList verificationList,List<T> entities, List<D> entityDisplays);

  //查询初始场景信息
  protected abstract List<T> queryScenes(Long sceneDetailId, UserInfo userInfo, Long exerciseId);

  //查询校验信息
  protected abstract List<C> queryChecks(Long exerciseId, Long sceneDetailId, String tableName);

  //将校验信息放入display
  protected abstract void addCheckToDisplay(List<C> checkConstraints, VerificationList entityDisplays);

  //实例化display
  protected abstract D createDisplayEntity();
}
