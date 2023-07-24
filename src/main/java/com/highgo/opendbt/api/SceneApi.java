package com.highgo.opendbt.api;

/**
 * @Description: 场景api
 * @Title: SceneApi
 * @Package com.highgo.opendbt.api
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/7/10 15:56
 */

import com.highgo.opendbt.scene.domain.entity.TScene;
import com.highgo.opendbt.scene.service.TSceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "场景接口")
@RestController
@CrossOrigin
@RequestMapping("/scene")

public class SceneApi {
  @Autowired
  private TSceneService sceneService;
  @ApiOperation(value = "根据id查询场景")
  @GetMapping("/getScene/{sceneId}")
  public TScene getOneSceneById(@PathVariable("sceneId") Integer sceneId){
    return sceneService.getScene(sceneId);
  }
}
