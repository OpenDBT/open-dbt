package com.highgo.opendbt.api;

import com.highgo.opendbt.common.utils.ValidationUtil;
import com.highgo.opendbt.verificationSetup.domain.model.*;
import com.highgo.opendbt.verificationSetup.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Description: DDL类型习题设置校验点API
 * @Title: VerificationSetup
 * @Package com.highgo.opendbt.api
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/27 17:09
 */
@Api(tags = "习题设置校验点")
@RestController
@RequestMapping("/verificationSetup")
@CrossOrigin
public class VerificationSetupAPI {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckDetailService checkDetailService;
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TSceneIndexService sceneIndexService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  TSceneConstraintService sceneConstraintService;
  @Autowired
  TCheckConstraintService checkConstraintService;
  @Autowired
  TSceneFkService sceneForeignKeyService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  TSceneSeqService sceneSeqService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  VerifyCommonService verifyCommonService;


  ///////////////////////////////////查询/////////////////////////////////////////////////
  @ApiOperation(value = "查询场景表相关表结构信息")
  @PostMapping("/getSceneDetailList")
  public VerificationList getSceneDetailList(HttpServletRequest request,
                                             @RequestBody @Valid TableModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneDetailService.getSceneDetailList(request, model.getSceneId(), model.getExerciseId());
  }



  @ApiOperation(value = "查询字段相关信息")
  @PostMapping("/getFieldList")
  public VerificationList getFieldList(HttpServletRequest request,
                                       @RequestBody @Valid FieldModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneFieldService.getFieldList(request, model);
  }

  @ApiOperation(value = "查询约束相关信息")
  @PostMapping("/getConstraintList")
  public VerificationList getConstraintList(HttpServletRequest request, @RequestBody @Valid SearchModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneConstraintService.getConstraintList(request, model);
  }

  @ApiOperation(value = "查询外键相关信息")
  @PostMapping("/getForeignKeyList")
  public VerificationList getForeignKeyList(HttpServletRequest request, @RequestBody @Valid SearchModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneForeignKeyService.getForeignKeyList(request, model);
  }

  @ApiOperation(value = "查询索引相关信息")
  @PostMapping("/getIndexList")
  public VerificationList getIndexList(HttpServletRequest request, @RequestBody @Valid SearchModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneIndexService.getIndexList(request, model);
  }

  @ApiOperation(value = "查询序列相关信息")
  @PostMapping("/getSequenceList")
  public VerificationList getSequenceList(HttpServletRequest request, @RequestBody @Valid SearchModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return sceneSeqService.getSequenceList(request,model);
  }
///////////////////////////////////保存更新/////////////////////////////////////////////////

  @ApiOperation(value = "保存表结构信息")
  @PostMapping("/saveCheckDetail")
  public Long saveCheckDetail(HttpServletRequest request, @RequestBody @Valid TCheckDetailSave detail, BindingResult result) throws Exception {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkDetailService.saveCheckDetail(request, detail);
  }

  @ApiOperation(value = "保存表字段信息")
  @PostMapping("/saveCheckField")
  public boolean saveCheckField(HttpServletRequest request, @RequestBody @Valid CheckFieldsSave fields, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkFieldService.saveCheckField(request, fields);
  }
  @ApiOperation(value = "保存表约束信息")
  @PostMapping("/saveCheckConstraint")
  public boolean saveCheckConstraint(HttpServletRequest request, @RequestBody @Valid CheckConstraintsSave constraints, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkConstraintService.saveCheckConstraint(request, constraints);
  }

  @ApiOperation(value = "保存表外键信息")
  @PostMapping("/saveCheckForeignKey")
  public boolean saveCheckForeignKey(HttpServletRequest request, @RequestBody @Valid CheckFksSave fks, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkFkService.saveCheckForeignKey(request, fks);
  }
  @ApiOperation(value = "保存表索引信息")
  @PostMapping("/saveCheckIndex")
  public boolean saveCheckIndex(HttpServletRequest request, @RequestBody @Valid CheckIndexListSave indexList, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkIndexService.saveCheckIndex(request, indexList);
  }


  @ApiOperation(value = "保存序列信息")
  @PostMapping("/saveCheckSequence")
  public boolean saveCheckSequence(HttpServletRequest request, @RequestBody @Valid CheckSequensSave sequensSave, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkSeqService.saveCheckSequence(request, sequensSave);
  }
  ///////////////////////////////////校验恢复/////////////////////////////////////////////////
  @ApiOperation(value = "字段恢复")
  @GetMapping("/fieldRecovery/{exerciseId}/{id}")
  public boolean fieldRecovery(HttpServletRequest request, @PathVariable Long exerciseId, @PathVariable Long id) {
    logger.debug("Enter,id=" + id);
    //校验
    return verifyCommonService.fieldRecovery(request, id, exerciseId);
  }



  @ApiOperation(value = "约束恢复")
  @GetMapping("/constraintRecovery/{exerciseId}/{id}")
  public boolean constraintRecovery(HttpServletRequest request, @PathVariable Long exerciseId, @PathVariable Long id) {
    logger.debug("Enter,id=" + id);
    //校验
    return verifyCommonService.constraintRecovery(request, id, exerciseId);
  }

  @ApiOperation(value = "索引恢复")
  @GetMapping("/indexRecovery/{exerciseId}/{id}")
  public boolean indexRecovery(HttpServletRequest request, @PathVariable Long exerciseId, @PathVariable Long id) {
    logger.debug("Enter,id=" + id);
    //校验
    return verifyCommonService.indexRecovery(request, id, exerciseId);
  }

  @ApiOperation(value = "序列恢复")
  @GetMapping("/seqRecovery/{exerciseId}/{id}")
  public boolean seqRecovery(HttpServletRequest request, @PathVariable Long exerciseId, @PathVariable Long id) {
    logger.debug("Enter,id=" + id);
    //校验
    return verifyCommonService.seqRecovery(request, id, exerciseId);
  }

  @ApiOperation(value = "外键恢复")
  @GetMapping("/fkRecovery/{exerciseId}/{id}")
  public boolean fkRecovery(HttpServletRequest request, @PathVariable Long exerciseId, @PathVariable Long id) {
    logger.debug("Enter,id=" + id);
    //校验
    return verifyCommonService.fkRecovery(request, id, exerciseId);
  }


  ///////////////////////////////////删除/////////////////////////////////////////////////


  @ApiOperation(value = "删除新增的表信息")
  @PostMapping("/deleteNewTableInfo")
  public boolean deleteNewTableInfo(HttpServletRequest request, @RequestBody @Valid NewTableInfoDel infoDel, BindingResult result) {
    logger.debug("Enter,");
    ValidationUtil.Validation(result);
    return checkDetailService.deleteNewTableInfo(request, infoDel);
  }

///////////////////////////////////一键恢复/////////////////////////////////////////////////

  @ApiOperation(value = "一键恢复,只适用于场景表恢复，新增表只可删除不可一键恢复")
  @PostMapping("/recovery")
  public boolean recovery(HttpServletRequest request, @RequestBody @Valid RecoveryModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return verifyCommonService.recovery(request, model);
  }







  @ApiOperation(value = "新增表结构和表字段信息")
  @PostMapping("/saveCheckDetailAndFields")
  public boolean saveCheckDetailAndFields(HttpServletRequest request, @RequestBody @Valid CheckDetailAndFields detail, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkDetailService.saveCheckDetailAndFields(request, detail);
  }








  @ApiOperation(value = "一键生成答案")
  @GetMapping("/generatesAnswer/{sceneId}/{exerciseId}")
  public String generatesAnswer(HttpServletRequest request,
                                @PathVariable("sceneId") @Valid int sceneId,
                                @PathVariable("exerciseId") @Valid Long exerciseId) {
    logger.debug("Enter,");
    return verifyCommonService.generatesAnswer(request, sceneId, exerciseId);
  }


  @ApiOperation(value = "自动生成描述")
  @PostMapping("/generateDescriptions")
  public String generateDescriptions(HttpServletRequest request, @RequestBody @Valid GeneratorDescription model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return verifyCommonService.generateDescriptions(request, model);
  }

}
