package com.highgo.opendbt.course.service.impl;

import au.com.bytecode.opencsv.CSVReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.highgo.opendbt.common.bean.SchemaConnection;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.service.RunAnswerService;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CloseUtil;
import com.highgo.opendbt.common.utils.ExcelUtil;
import com.highgo.opendbt.common.utils.TimeUtil;
import com.highgo.opendbt.course.domain.entity.Exercise;
import com.highgo.opendbt.course.domain.model.ImportExerciseTO;
import com.highgo.opendbt.course.domain.model.Scene;
import com.highgo.opendbt.course.domain.model.SceneDetail;
import com.highgo.opendbt.course.domain.model.ScenePage;
import com.highgo.opendbt.course.mapper.ExerciseMapper;
import com.highgo.opendbt.course.mapper.SceneDetailMapper;
import com.highgo.opendbt.course.mapper.SceneMapper;
import com.highgo.opendbt.course.service.SceneService;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.tools.SceneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class SceneServiceImpl implements SceneService {

  Logger logger = LoggerFactory.getLogger(getClass());

  private final SceneMapper sceneMapper;

  private final SceneDetailMapper sceneDetailMapper;

  private final RunAnswerService runAnswerService;

  private final ExerciseMapper exerciseMapper;

  public SceneServiceImpl(SceneMapper sceneMapper, SceneDetailMapper sceneDetailMapper, RunAnswerService runAnswerService, ExerciseMapper exerciseMapper) {
    this.sceneMapper = sceneMapper;
    this.sceneDetailMapper = sceneDetailMapper;
    this.runAnswerService = runAnswerService;
    this.exerciseMapper = exerciseMapper;
  }

  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckDetailService checkDetailService;
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  TCheckConstraintService checkConstraintService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  TSceneFieldService sceneFieldService;
  @Autowired
  TSceneIndexService sceneIndexService;
  @Autowired
  TSceneConstraintService sceneConstraintService;
  @Autowired
  TSceneFkService sceneForeignKeyService;
  @Autowired
  TSceneSeqService sceneSeqService;
  @Override
  public PageInfo<Scene> getScene(ScenePage scenePage) {
    // 分页查询配置
    PageHelper.startPage(scenePage.getCurrent(), scenePage.getPageSize());
    List<Scene> sceneList = sceneMapper.getScene(scenePage.getCourseId());
    return new PageInfo<Scene>(sceneList);
  }

  @Override
  public List<Scene> getShareScene(int courseId) {
    return sceneMapper.getScene(courseId);
  }

  @Override
  public Scene getSceneDetail(int sceneId) {
    // 获取场景表信息
    Scene scene = sceneMapper.getSceneDetail(sceneId);
    // 获取场景明细表信息
    List<SceneDetail> sceneDetailList = sceneDetailMapper.getSceneDetailById(sceneId);
    scene.setSceneDetailList(sceneDetailList);
    return scene;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer updateScene(HttpServletRequest request, Scene scene) {
    // 场景id等于-1为新增
    if (scene.getSceneId() == -1) {
      // 新增场景
      Integer addScene = sceneMapper.addScene(scene);
      // 解析SQL脚本获取表名，并添加到场景明细表
      if (scene.getInitShell().length() > 0) {
        List<SceneDetail> sceneDetailList = SceneUtil.parsSQLGetTableName(scene.getSceneId(), scene.getInitShell());
        if (sceneDetailList != null && !sceneDetailList.isEmpty()) {
          sceneDetailMapper.addSceneDetail(scene.getSceneId(), sceneDetailList);
        }
      }
      return addScene;
    } else {
      //判断场景是否在使用中是否允许修改删除
      determineIsUsed(scene.getSceneId());
      //查询要删除的场景明细
      List<TSceneDetail> details = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", scene.getSceneId()));
      // 删除场景明细
      sceneDetailMapper.deleteSceneDetail(scene.getSceneId());
      // 解析SQL脚本获取表名，并添加到场景明细表
      if (scene.getInitShell().length() > 0) {
        List<SceneDetail> sceneDetailList = SceneUtil.parsSQLGetTableName(scene.getSceneId(), scene.getInitShell());
        if (sceneDetailList != null && !sceneDetailList.isEmpty()) {
          sceneDetailMapper.addSceneDetail(scene.getSceneId(), sceneDetailList);
        }
      }
      //删除提取的表相关的结构信息
      delSceneInfo(details);
      // 更新场景
      return sceneMapper.updateScene(scene);
    }
  }

  private void delSceneInfo(List<TSceneDetail> details) {
    sceneFieldService.remove(new QueryWrapper<TSceneField>().in("scene_detail_id",details.stream().map(item->item.getId()).collect(Collectors.toList())));
    sceneIndexService.remove(new QueryWrapper<TSceneIndex>().in("scene_detail_id",details.stream().map(item->item.getId()).collect(Collectors.toList())));
    sceneConstraintService.remove(new QueryWrapper<TSceneConstraint>().in("scene_detail_id",details.stream().map(item->item.getId()).collect(Collectors.toList())));
    sceneForeignKeyService.remove(new QueryWrapper<TSceneFk>().in("scene_detail_id",details.stream().map(item->item.getId()).collect(Collectors.toList())));
    sceneSeqService.remove(new QueryWrapper<TSceneSeq>().in("scene_detail_id",details.stream().map(item->item.getId()).collect(Collectors.toList())));

  }

  //判断场景是否在使用中是否允许修改删除
  private void determineIsUsed(int sceneId) {
    //查询场景详情
    List<TSceneDetail> sceneDetails = sceneDetailService.list(new QueryWrapper<TSceneDetail>().eq("scene_id", sceneId));
    if (!sceneDetails.isEmpty()) {
      List<TCheckDetail> checkDetails = checkDetailService.list(new QueryWrapper<TCheckDetail>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkDetails);
      List<TCheckField> checkFields = checkFieldService.list(new QueryWrapper<TCheckField>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkFields);
      List<TCheckIndex> checkIndices = checkIndexService.list(new QueryWrapper<TCheckIndex>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkIndices);
      List<TCheckConstraint> checkConstraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkConstraints);
      List<TCheckFk> checkFks = checkFkService.list(new QueryWrapper<TCheckFk>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkFks);
      List<TCheckSeq> checkSeqs = checkSeqService.list(new QueryWrapper<TCheckSeq>().in("scene_detail_id", sceneDetails.stream().map(TSceneDetail::getId).collect(Collectors.toList())));
      BusinessResponseEnum.CANNOTMODIFYWHILEINUSE.assertIsEmpty(checkSeqs);
    }
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer deleteScene(HttpServletRequest request, int sceneId) {
    //判断场景是否在使用中是否允许修改删除
    determineIsUsed(sceneId);
    List<Exercise> exerciseList = exerciseMapper.getExerciseBySceneId(sceneId);
    BusinessResponseEnum.SCENEISUSENOTDELETE.assertIsEmpty(exerciseList);
    Integer deleteScene = sceneMapper.deleteScene(sceneId);
    return deleteScene;

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer copyScene(int sceneId) {
    // 获取需要复制的场景信息
    Scene scene = sceneMapper.getSceneDetail(sceneId);
    BusinessResponseEnum.GETCOPYSCENEFAIL.assertNotNull(scene);
    // 获取需要复制的场景明细
    List<SceneDetail> sceneDetailList = sceneDetailMapper.getSceneDetailById(sceneId);
    Integer addScene = sceneMapper.addScene(scene);
    if (sceneDetailList != null && !sceneDetailList.isEmpty()) {
      sceneDetailMapper.addSceneDetail(scene.getSceneId(), sceneDetailList);
    }
    return addScene;
  }

  @Override
  public Integer testSceneSQLShell(HttpServletRequest request, String initShell) {
    Connection connection = null;
    Statement statement = null;
    Integer executeUpdate = null;
    SchemaConnection schemaConnection = new SchemaConnection();
    try {
      // 获取用户信息
      UserInfo loginUser = Authentication.getCurrentUser(request);
      // 初始化脚本并获取指定schema的连接
      runAnswerService.getSchemaConnection(loginUser, schemaConnection);
      if (null != schemaConnection.getConnection()) {
        connection = schemaConnection.getConnection();
        statement = connection.createStatement();
        // 执行sql脚本
        executeUpdate = statement.executeUpdate(initShell);
      }
      return executeUpdate;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new APIException(e.getMessage());
    } finally {
      runAnswerService.dropSchema(schemaConnection.getSchemaName());
      CloseUtil.close(statement);
      CloseUtil.close(connection);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer copySceneToMyCourse(int sceneId, int courseId) {
    // 获取需要复制的场景信息
    Scene scene = sceneMapper.getSceneDetail(sceneId);
    BusinessResponseEnum.GETCOPYSCENEFAIL.assertNotNull(scene);
    // 获取需要复制的场景明细
    List<SceneDetail> sceneDetailList = sceneDetailMapper.getSceneDetailById(sceneId);
    scene.setCourseId(courseId);
    Integer addScene = sceneMapper.addScene(scene);
    if (sceneDetailList != null && !sceneDetailList.isEmpty()) {
      sceneDetailMapper.addSceneDetail(scene.getSceneId(), sceneDetailList);
    }
    return addScene;
  }

  @Override
  public String exportSceneList(HttpServletRequest request, int courseId) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    // 导出文件名
    String fileName = "scene_export_" + loginUser.getCode() + "_" + TimeUtil.getDateTimeStr() + ".csv";
    // 表头字段名
    String[] columnNameArray = new String[]{"场景ID", "场景名称", "场景描述", "场景SQL脚本"};
    List<Map<Integer, Object>> mapList = new ArrayList<Map<Integer, Object>>();
    List<Scene> sceneList = sceneMapper.getScene(courseId);
    // 转换为map的list，方便写入文件时每个字段的循环以及公共方法参数一致
    for (int i = 0; i < sceneList.size(); i++) {
      Scene scene = sceneList.get(i);

      Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
      dataMap.put(0, scene.getSceneId());
      dataMap.put(1, scene.getSceneName());
      dataMap.put(2, scene.getSceneDesc());
      dataMap.put(3, scene.getInitShell());
      mapList.add(dataMap);
    }
    // 调用写入文件方法
    ExcelUtil.writeCSV(fileName, columnNameArray, mapList);
    return fileName;
  }

  @Override
  public String exportSceneById(HttpServletRequest request, int sceneId) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    // 表头字段名
    String[] columnNameArray = new String[]{"场景ID", "场景名称", "场景描述", "场景SQL脚本"};
    List<Map<Integer, Object>> mapList = new ArrayList<Map<Integer, Object>>();
    Scene scene = sceneMapper.getSceneDetail(sceneId);
    Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
    dataMap.put(0, scene.getSceneId());
    dataMap.put(1, scene.getSceneName());
    dataMap.put(2, scene.getSceneDesc());
    dataMap.put(3, scene.getInitShell());
    mapList.add(dataMap);
    // 导出文件名
    String fileName = loginUser.getCode() + "_" + scene.getSceneName() + ".csv";
    // 调用写入文件方法
    ExcelUtil.writeCSV(fileName, columnNameArray, mapList);
    return fileName;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String uploadSceneListFile(HttpServletRequest request, MultipartFile file) {
    FileOutputStream fos = null;
    try {
      // 获取用户信息
      UserInfo loginUser = Authentication.getCurrentUser(request);

      String fileName = file.getOriginalFilename();
      String[] fileNameArray = fileName.split("\\.");
      String newFileName = "scene_import_" + loginUser.getUserId() + "_" + loginUser.getCode() + "." + fileNameArray[fileNameArray.length - 1];

      String folderPath = ExcelUtil.getProjectPath() + "/temp";

      File folderPathFile = new File(folderPath);
      if (!folderPathFile.exists()) {
        folderPathFile.mkdir();
      }
      String filePath = folderPath + "/" + newFileName;
      fos = new FileOutputStream(filePath);
      fos.write(file.getBytes());
      return filePath;
    } catch (Exception e) {
      throw new APIException(e.getMessage());
    } finally {
      try {
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String importScene(HttpServletRequest request, ImportExerciseTO importExerciseTO) {
    CSVReader csvReader = null;
    try {
      for (String filePath : importExerciseTO.getFilePathList()) {
        // 检查文件是否存在，是否是excel文案
        ExcelUtil.checkFileExists(filePath);
        csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("UTF-8")));
        List<String[]> dataList = csvReader.readAll();
        logger.info("data count = " + dataList.size());
        if (dataList.size() > 1) {
          // 读取数据进行导入
          for (int i = 1; i < dataList.size(); i++) {
            try {
              String[] data = dataList.get(i);
              Scene scene = new Scene();

              scene.setSceneName(data[1]);
              scene.setSceneDesc(data[2]);
              scene.setInitShell(data[3]);
              scene.setCourseId(importExerciseTO.getCourseId());
              sceneMapper.addScene(scene);
              // 解析SQL脚本获取表名，并添加到场景明细表
              if (scene.getInitShell().length() > 0) {
                List<SceneDetail> sceneDetailList = SceneUtil.parsSQLGetTableName(scene.getSceneId(), scene.getInitShell());
                if (sceneDetailList != null && !sceneDetailList.isEmpty()) {
                  sceneDetailMapper.addSceneDetail(scene.getSceneId(), sceneDetailList);
                }
              }
            } catch (Exception e) {
              logger.error(e.getMessage(), e);
            }
          }
        }
      }
      return "";
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new APIException(e.getMessage());
    } finally {
      CloseUtil.close(csvReader);
    }
  }

}
