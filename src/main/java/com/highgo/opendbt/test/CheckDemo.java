package com.highgo.opendbt.test;

import com.highgo.opendbt.verificationSetup.domain.entity.TCheckDetail;
import com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule.CheckEventProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.answerverVerificationModule.CheckProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 校验测试
 * @Title: CheckDemo
 * @Package com.highgo.opendbt.test
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/23 17:48
 */
@CrossOrigin
@RestController
@RequestMapping("/CheckDemoTest")
@Api(tags = "校验接口")
public class CheckDemo {
  @GetMapping("/check")
  @ApiOperation(value = "check")
  public void check(){
    final TCheckDetail detail = new TCheckDetail();
    detail.setId(0L);
    detail.setSceneDetailId(0L);
    detail.setExerciseId(0);
    detail.setCheckStatus("INSERT");
    detail.setTableName("tableName");
    detail.setDescribe("describe");
    final List<TCheckDetail> checkDetails = Arrays.asList(detail);
    final TCheckDetail detail1 = new TCheckDetail();
    detail1.setId(0L);
    detail1.setSceneDetailId(0L);
    detail1.setExerciseId(0);
    detail1.setCheckStatus("INSERT");
    detail1.setTableName("tableName");
    detail1.setDescribe("describe");
    final List<TCheckDetail> details = Arrays.asList(detail1);
    CheckProcess eventProcess = CheckEventProcessFactory.createEventProcess(TableInfoEvent.TABLE);
    try{
      boolean verify = eventProcess.verify(checkDetails,details);
      System.out.println("zhengquexing="+verify);
    }catch (Exception e){
      System.out.println("错误信息="+e.getMessage());
    }


  }
}
