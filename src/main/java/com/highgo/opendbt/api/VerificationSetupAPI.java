package com.highgo.opendbt.api;

import com.highgo.opendbt.common.utils.ValidationUtil;
import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
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

  @ApiOperation(value = "查询场景表相关表结构信息")
  @GetMapping("/getSceneDetailList/{sceneId}/{exerciseId}")
  public VerificationList getSceneDetailList(HttpServletRequest request,
                                             @PathVariable("sceneId") @Valid int sceneId,
                                             @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneDetailService.getSceneDetailList(request, sceneId, exerciseId);
  }

  @ApiOperation(value = "更新表结构信息")
  @PostMapping("/saveCheckDetail")
  public boolean saveCheckDetail(HttpServletRequest request, @RequestBody @Valid TCheckDetail detail, BindingResult result) throws Exception {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkDetailService.saveCheckDetail(request, detail);
  }

  @ApiOperation(value = "新增表结构和表字段信息")
  @PostMapping("/saveCheckDetailAndFields")
  public boolean saveCheckDetailAndFields(HttpServletRequest request, @RequestBody @Valid CheckDetailAndFields detail, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkDetailService.saveCheckDetailAndFields(request, detail);
  }

  @ApiOperation(value = "查询场景字段相关表结构信息")
  @GetMapping("/getFieldList/{sceneDetailId}/{exerciseId}")
  public VerificationList getFieldList(HttpServletRequest request,
                                       @PathVariable("sceneDetailId") @Valid long sceneDetailId,
                                       @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneFieldService.getFieldList(request, sceneDetailId, exerciseId);
  }

  @ApiOperation(value = "保存表字段信息")
  @PostMapping("/saveCheckField")
  public boolean saveCheckField(HttpServletRequest request, @RequestBody @Valid CheckFieldsSave fields, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkFieldService.saveCheckField(request, fields);
  }

  @ApiOperation(value = "查询场景索引相关信息")
  @GetMapping("/getIndexList/{sceneDetailId}/{exerciseId}")
  public VerificationList getIndexList(HttpServletRequest request,
                                       @PathVariable("sceneDetailId") @Valid long sceneDetailId,
                                       @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneIndexService.getIndexList(request, sceneDetailId, exerciseId);
  }

  @ApiOperation(value = "保存表索引信息")
  @PostMapping("/saveCheckIndex")
  public boolean saveCheckIndex(HttpServletRequest request, @RequestBody @Valid CheckIndexListSave indexList, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkIndexService.saveCheckIndex(request, indexList);
  }

  @ApiOperation(value = "查询场景约束相关信息")
  @GetMapping("/getConstraintList/{sceneDetailId}/{exerciseId}")
  public VerificationList getConstraintList(HttpServletRequest request,
                                            @PathVariable("sceneDetailId") @Valid long sceneDetailId,
                                            @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneConstraintService.getConstraintList(request, sceneDetailId, exerciseId);
  }

  @ApiOperation(value = "保存表约束信息")
  @PostMapping("/saveCheckConstraint")
  public boolean saveCheckConstraint(HttpServletRequest request, @RequestBody @Valid CheckConstraintsSave constraints, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkConstraintService.saveCheckConstraint(request, constraints);
  }

  @ApiOperation(value = "查询场景外键相关信息")
  @GetMapping("/getForeignKeyList/{sceneDetailId}/{exerciseId}")
  public VerificationList getForeignKeyList(HttpServletRequest request,
                                            @PathVariable("sceneDetailId") @Valid long sceneDetailId,
                                            @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneForeignKeyService.getForeignKeyList(request, sceneDetailId, exerciseId);
  }

  @ApiOperation(value = "保存表外键信息")
  @PostMapping("/saveCheckForeignKey")
  public boolean saveCheckForeignKey(HttpServletRequest request, @RequestBody @Valid CheckFksSave fks, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkFkService.saveCheckForeignKey(request, fks);
  }

  @ApiOperation(value = "查询序列相关信息")
  @GetMapping("/getSequenceList/{sceneDetailId}/{exerciseId}")
  public VerificationList getSequenceList(HttpServletRequest request,
                                          @PathVariable("sceneDetailId") @Valid long sceneDetailId,
                                          @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return sceneSeqService.getSequenceList(request, sceneDetailId, exerciseId);
  }

  @ApiOperation(value = "保存序列信息")
  @PostMapping("/saveCheckSequence")
  public boolean saveCheckSequence(HttpServletRequest request, @RequestBody @Valid CheckSequensSave sequensSave, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return checkSeqService.saveCheckSequence(request, sequensSave);
  }


  @ApiOperation(value = "删除新增的表信息")
  @PostMapping("/deleteNewTableInfo")
  public boolean deleteNewTableInfo(HttpServletRequest request, @RequestBody @Valid NewTableInfoDel infoDel, BindingResult result) {
    logger.debug("Enter,");
    ValidationUtil.Validation(result);
    return checkDetailService.deleteNewTableInfo(request, infoDel);
  }

  @ApiOperation(value = "一键生成答案")
  @GetMapping("/generatesAnswer/{sceneId}/{exerciseId}")
  public StoreAnswer generatesAnswer(HttpServletRequest request,
                                     @PathVariable("sceneId") @Valid int sceneId,
                                     @PathVariable("exerciseId") @Valid int exerciseId) {
    logger.debug("Enter,");
    return verifyCommonService.generatesAnswer(request, sceneId, exerciseId);
  }

  @ApiOperation(value = "测试运行")
  @PostMapping("/testRun")
  public boolean testRun(HttpServletRequest request, @RequestBody @Valid TestRunModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return verifyCommonService.testRun(request, model);
  }

  @ApiOperation(value = "一键恢复,只适用于场景表恢复，新增表只可删除不可一键恢复")
  @PostMapping("/recovery")
  public boolean recovery(HttpServletRequest request, @RequestBody @Valid RecoveryModel model, BindingResult result) {
    logger.debug("Enter,");
    //校验
    ValidationUtil.Validation(result);
    return verifyCommonService.recovery(request, model);
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
