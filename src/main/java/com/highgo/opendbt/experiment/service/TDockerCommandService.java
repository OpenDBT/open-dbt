package com.highgo.opendbt.experiment.service;

import com.highgo.opendbt.experiment.domain.TBackup;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface TDockerCommandService {
  //保存实验
  boolean saveExperiment(HttpServletRequest request, ExperimentInfo experiment);

  //删除容器
  boolean deleteContainer(HttpServletRequest request, String dockerHost, int dockerPort, String containerName);

  //备份容器
  void backUpContainer(HttpServletRequest request, String dockerHost, int dockerPort, String containerName, String imageName);

  //恢复容器
  void restoreContainer(HttpServletRequest request, String userName, String key, String path, String dockerHost, int dockerPort, int port, String containerName, String imageName, String dataBackupPath);

  //容器备份列表
  List<TBackup> listContainerBackUp(HttpServletRequest request, String containerName);

  //上传镜像
  void uploadImage(HttpServletRequest request, String dockerHost, int dockerPort, String fileName, MultipartFile file);


}
