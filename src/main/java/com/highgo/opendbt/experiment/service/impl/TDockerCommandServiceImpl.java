package com.highgo.opendbt.experiment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.TimeUtil;
import com.highgo.opendbt.experiment.domain.TBackup;
import com.highgo.opendbt.experiment.domain.TContainers;
import com.highgo.opendbt.experiment.domain.TExperiment;
import com.highgo.opendbt.experiment.domain.TExperimentDocuments;
import com.highgo.opendbt.experiment.manageer.DockerComposeBuilder;
import com.highgo.opendbt.experiment.mapper.TBackupMapper;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import com.highgo.opendbt.experiment.service.*;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Title: TDockerCommandServiceImpl
 * @Package com.highgo.opendbt.experiment.service.impl
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/28 9:54
 */
@Service
public class TDockerCommandServiceImpl implements TDockerCommandService {
  @Autowired
  private TExperimentDocumentsService documentsService;
  @Autowired
  private TExperimentService experimentService;
  @Autowired
  private TContainersService containersService;
  @Autowired
  private TBackupService backupService;
  @Autowired
  private TBackupMapper mapper;
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveExperiment(HttpServletRequest request, ExperimentInfo experiment) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //拷贝属性
    TExperiment tExperiment = new TExperiment();
    BeanUtils.copyProperties(experiment, tExperiment);
    //设置人员时间
    if (experiment.getId() != null) {
      tExperiment.setUpdateTime(new Date());
      tExperiment.setUpdateUser(loginUser.getUserId());
    } else {
      tExperiment.setCreateTime(new Date());
      tExperiment.setCreateUser(loginUser.getUserId());
    }
    //保存实验
    boolean save = experimentService.saveOrUpdate(tExperiment);
    if (!save) {
      throw new APIException("保存失败");
    }
    //保存容器cpu、memory
//    TContainers container = containersService.getOne(new QueryWrapper<TContainers>()
//      .eq("code", loginUser.getCode())
//      .eq("image_id", tExperiment.getImageId())
//      .eq("delete_flag",0));
    TContainers container = containersService.getById(tExperiment.getId());
    if (container == null) {
      TContainers tContainers = new TContainers()
        .setCode(loginUser.getCode())
        .setCourseId(experiment.getCourseId())
        .setExperimentId(tExperiment.getId())
        .setImageId(tExperiment.getImageId())
        .setCpu(experiment.getCpu())
        .setMemory(experiment.getMemory());

      tContainers.setDeleteFlag(0)
        .setCreateTime(new Date())
      .setCreateUser(loginUser.getUserId());
      boolean res = containersService.save(tContainers);
      if (!res) {
        throw new APIException("保存失败");
      }
    }
    //判断实验文档是否为空
    if (StringUtils.isNotBlank(experiment.getExperimentContent())) {
      //查询实验文档
      TExperimentDocuments documents = documentsService.getOne(new QueryWrapper<TExperimentDocuments>().eq("experiment_id", tExperiment.getId()));
      if (documents == null) {
        documents = new TExperimentDocuments();
      }
      //设置实验文档属性
      documents.setExperimentId(tExperiment.getId());
      documents.setExperimentContent(experiment.getExperimentContent());
      //保存实验文档
      boolean doumentSaveRes = documentsService.saveOrUpdate(documents);
      if (!doumentSaveRes) {
        throw new APIException("保存失败");
      }
      return doumentSaveRes;
    }
    return save;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteContainer(HttpServletRequest request, String dockerHost, int dockerPort, String containerName) {
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //校验该容器是否有实验在使用
    TContainers container = containersService.getOne(new QueryWrapper<TContainers>()
      .eq("container_name", containerName)
      .eq("delete_flag",0));
    if (container == null) {
      throw new APIException("容器不存在");
    }
    //更更新容器表
    container.setDeleteTime(new Date()).setDeleteFlag(1).setDeleteUser(loginUser.getUserId());
    boolean update = containersService.saveOrUpdate(container);
    //关闭容器
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    composeBuilder.deleteContainer(containerName);
    return update;
  }

  /**
   * @description: 容器备份
   * @author:
   * @date: 2023/10/20 17:04
   * @param: [request, dockerHost, dockerPort, containerName 需要备份的容器名称, imageName 备份后的镜像名称可以和原来镜像相同，覆盖原有镜像, dataBackupPath 备份路径]
   * @return: void
   **/
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void backUpContainer(HttpServletRequest request, String dockerHost, int dockerPort, String containerName, String imageName) {
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    //备份地址
   String dataBackupPath= (File.separator).concat("path").concat(File.separator).concat("to").concat(File.separator)
      .concat(TimeUtil.getDateTimeDay()).concat(File.separator).concat(TimeUtil.getDateTimeStr());
   //判断路径是否存在
    checkAndCreateDirectoryIfNotExists(dataBackupPath);
    //备份容器信息
    composeBuilder.backupContainerWithData(containerName, imageName.split(":")[0], dataBackupPath);
    //保存备份信息
    boolean res = backupService.save(new TBackup().setContainerName(containerName).setImageName(imageName).setBackupTime(new Date())
      .setBackupPath(dataBackupPath));
    if (!res) {
      throw new APIException("备份失败");
    }
  }

  private void checkAndCreateDirectoryIfNotExists(String dataBackupPath) {
    // 创建 File 对象
    File directory = new File(dataBackupPath);

    // 判断路径是否存在
    if (directory.exists()) {
      System.out.println("路径已经存在");
    } else {
      // 如果路径不存在，尝试创建它
      boolean created = directory.mkdirs();
      if (created) {
        System.out.println("路径已创建");
      } else {
        System.out.println("无法创建路径");
      }
    }
  }

  /**
   * @description: 备份恢复
   * @author:
   * @date: 2023/10/20 17:08
   * @param: [request, dockerHost, dockerPort, containerName 恢复后的容器名称，先判断容器是否存在，不存在可以恢复, imageId 备份的镜像id, dataBackupPath 备份的路径]
   * @return: void
   **/
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void restoreContainer(HttpServletRequest request,String userName, String key, String path, String dockerHost, int dockerPort,int port, String containerName, String imageName, String dataBackupPath) {
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    String containerId = composeBuilder.restoreContainerWithData(containerName, imageName, dataBackupPath, userName,  key,  path,  dockerHost,  port);
    TContainers containers = containersService.getOne(new QueryWrapper<TContainers>().eq("container_name", containerName)
      .eq("delete_flag",0));
    boolean res = containersService.saveOrUpdate(containers.setContainerId(containerId));
    if(!res){
      throw new APIException("更新备份结果失败，请联系管理员！");
    }
  }
/**
 * @description:  容器备份列表
 * @author:
 * @date: 2023/10/24 10:29
 * @param: [request, containerName]
 * @return: boolean
 **/
  @Override
  public List<TBackup> listContainerBackUp(HttpServletRequest request, String containerName) {
    return backupService.list(new QueryWrapper<TBackup>()
      .eq("container_name",containerName)
      .orderByDesc("backup_time"));
  }

  /**
   * @description:
   * @author:
   * @date: 2023/10/27 14:01
   * @param: [fileName 镜像名称, filePath 本地镜像地址]
   * @return: void
   **/
  @Override
  public void uploadImage(HttpServletRequest request, String dockerHost, int dockerPort, String fileName, MultipartFile file) {
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    // composeBuilder.uploadImage(fileName, file);
  }



}
