package com.highgo.opendbt.experiment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.highgo.opendbt.common.bean.PageParam;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CamelCaseToUnderscoreConverter;
import com.highgo.opendbt.common.utils.ValidationUtil;
import com.highgo.opendbt.experiment.domain.TBackup;
import com.highgo.opendbt.experiment.domain.TContainers;
import com.highgo.opendbt.experiment.domain.TExperiment;
import com.highgo.opendbt.experiment.domain.TImages;
import com.highgo.opendbt.experiment.manageer.DockerComposeBuilder;
import com.highgo.opendbt.experiment.manageer.DockerContainerManager;
import com.highgo.opendbt.experiment.model.ContainerConfig;
import com.highgo.opendbt.experiment.model.ContainerModel;
import com.highgo.opendbt.experiment.model.ContainersInfo;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import com.highgo.opendbt.experiment.service.TContainersService;
import com.highgo.opendbt.experiment.service.TDockerCommandService;
import com.highgo.opendbt.experiment.service.TExperimentService;
import com.highgo.opendbt.experiment.service.TImagesService;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.Excel2Html;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.PDF2Html;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.PPT2Html;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.Word2Html;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.bean.dto.DocHtmlDto;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.highgo.opendbt.experiment.manageer.DockerContainerManager.getPath;


/**
 * @Description: 接收前端发来的命令
 * @Title: DockerCommandController
 * @Package com.highgo.opendbt.experiment.controller
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/18 10:43
 */
@RestController
@RequestMapping("/experiment")
public class DockerCommandController {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Value("${docker.connect.host}")
  private String dockerHost;
  @Value("${docker.connect.port}")
  private int dockerPort;
  @Value("${docker.connect.username}")
  private String userName;
  @Value("${docker.connect.key}")
  private String key;
  @Value("${docker.connect.upload.path}")
  private String path;
  @Value("${ssh.port}")
  private int port;
  @Value("${upload.dir}") // 从配置文件中读取上传目录
  private String uploadDir;

  @Autowired
  private TExperimentService experimentService;
  @Autowired
  private TImagesService imagesService;
  @Autowired
  private TDockerCommandService dockerCommandService;
  @Autowired
  private TContainersService containersService;

  @ApiOperation(value = "在线实验容器启动")
  @PostMapping({"/container"})
  public ContainerModel buildContainer(HttpServletRequest request, @RequestBody @Valid ContainerConfig config, BindingResult result) throws Exception {
    logger.info("Enter,  progress= {}", config.toString());
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //校验
    ValidationUtil.Validation(result);
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    ContainerModel model = composeBuilder.createAndStartContainer(getPath(config.getExperimentId(), config.getCourseId(), config.getStudentCode(), config.getImageName(), config.getImagePort(), config.getCpu(), config.getMemory()), config.getExperimentId(), toString().valueOf(config.getCourseId()), config.getStudentCode(), config.getImageName(), userName, key, path, dockerHost, port);
    logger.info("容器已创建并启动！");
    logger.info("Docker Compose 构建和运行成功！".concat(model.getContainerId()));
    //更新容器
    updateContainer(config, loginUser, model);
    return model;
  }

  /**
   * @description: 更新容器
   * @author:
   * @date: 2023/10/25 16:28
   * @param: [config, loginUser, model容器信息]
   * @return: void
   **/
  private void updateContainer(@RequestBody @Valid ContainerConfig config, UserInfo loginUser, ContainerModel model) {
    // 更新到数据库t_containers表 返回后刷新
    TContainers container = containersService.getOne(new QueryWrapper<TContainers>()
      .eq("code", config.getStudentCode())
      .eq("image_id", config.getImageId())
      .eq("course_id", config.getCourseId())
      .eq("experiment_id", config.getExperimentId())
      .eq("delete_flag", 0));
    if (container == null) {
      //查询容器设定的cpu、memory
      if (StringUtils.isBlank(config.getCpu())) {
        List<TContainers> containers = containersService.list(new QueryWrapper<TContainers>()
          .eq("image_id", config.getImageId())
          .eq("course_id", config.getCourseId())
          .eq("experiment_id", config.getExperimentId())
          .eq("delete_flag", 0)
          .isNotNull("cpu"));
        if (container != null) {
          String cpu = containers.get(0).getCpu();
          String memory = containers.get(0).getMemory();
          config.setCpu(cpu);
          config.setMemory(memory);
        }
      }
      TContainers tContainers = new TContainers()
        .setCode(config.getStudentCode())
        .setCourseId(config.getCourseId())
        .setImageId(config.getImageId())
        .setContainerId(model.getContainerId())
        .setContainerName(model.getContainerName())
        .setContainerPort(model.getContainerPort())
        .setExperimentId(config.getExperimentId())
        .setCpu(config.getCpu())
        .setMemory(config.getMemory());
      tContainers.setCreateTime(new Date()).setCreateUser(loginUser.getUserId());
      boolean res = containersService.save(tContainers);
      if (!res) {
        throw new APIException("在线实验启动失败,请联系管理员");
      }
    } else {
      boolean res = containersService.saveOrUpdate(container
        .setContainerId(model.getContainerId())
        .setContainerPort(model.getContainerPort())
        .setContainerName(model.getContainerName())
      );
      if (!res) {
        throw new APIException("在线实验启动失败,请联系管理员");
      }
    }
  }

  /**
   * @description: 创建镜像
   * @author:
   * @date: 2023/10/20 9:55
   * @param: []
   * @return: void
   **/
  @GetMapping({"/image/{imageName}"})
  public void image(@PathVariable("imageName") String imageName) {
    DockerContainerManager dockerContainerManager = DockerContainerManager.getInstance();
    dockerContainerManager.buildImage(getPath("kylin-highgo.Dockerfile"), imageName);
    logger.info("成功创建镜像！");
  }

  /**
   * @description: 在线实验部分文件上传
   * @author:
   * @date: 2023/9/26 15:27
   * @param: [request, file]
   * @return: java.lang.String
   **/
  @PostMapping("/upload-image")
  @ResponseBody
  public String uploadImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
    if (file != null && !file.isEmpty()) {
      try {
        // 生成唯一的文件名
        String uniqueFileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        // 构建保存文件的完整路径
        File targetFile = new File(uploadDir, uniqueFileName);
        // 将文件保存到目标路径
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
        DocHtmlDto docHtmlDto = null;
        //视频类型
        String[] array = {"video/mp4", "video/avi", "video/x-matroska", "video/mov", "video/wmv"};
        List<String> videoExtensions = Arrays.asList(array); // 将数组转换为列表
        //判断图片类型
        String fileType = file.getContentType();
        logger.info(fileType + "_____");
        if (fileType.equalsIgnoreCase("application/msword") || fileType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
          docHtmlDto = new Word2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("application/vnd.ms-excel") || fileType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
          docHtmlDto = new Excel2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("application/pdf")) {
          docHtmlDto = new PDF2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("application/vnd.ms-powerpoint") || fileType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
          docHtmlDto = new PPT2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else
          //图片
          if (fileType.equalsIgnoreCase("image/jpeg")
            || fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/bmp")
            || fileType.equalsIgnoreCase("image/gif") || fileType.equalsIgnoreCase("image/webp")
            || fileType.equalsIgnoreCase("image/eps")) {
            return uploadDir + File.separator + uniqueFileName;
          } else
            //视频
            if (videoExtensions.contains(fileType.toLowerCase())) {
              return uploadDir + File.separator + uniqueFileName;
            } else {
              throw new APIException(fileType + "类型暂不支持！");
            }
      } catch (IOException e) {
        e.printStackTrace();
        return "Image upload failed";
      }
    } else {
      return "No image file uploaded";
    }
  }

  /**
   * @description: 在线实验列表分页查询
   * @author:
   * @date: 2023/9/26 16:31
   * @param: [experiment]
   * @return: com.github.pagehelper.PageInfo<com.highgo.opendbt.experiment.domain.TExperiment>
   **/
  @PostMapping("/listExperiment")
  public PageInfo<TExperiment> listExperiment(@RequestBody PageParam<TExperiment> param) {
    logger.debug("Enter, pageTO = " + param.toString());
    if (CamelCaseToUnderscoreConverter.isSort(param.getOrderBy())) {
      PageHelper.startPage(param.getPageNum(), param.getPageSize()).setOrderBy(CamelCaseToUnderscoreConverter.convert(param.getOrderBy()).concat(" , update_time desc, create_time desc"));
    } else {
      PageHelper.startPage(param.getPageNum(), param.getPageSize()).setOrderBy(" update_time desc , create_time desc");
    }
    List<TExperiment> list = experimentService.listExperiment(param.getParam().getCourseId(), param.getParam().getExperimentName());
    return new PageInfo<TExperiment>(list);
  }

  /**
   * @description: 查询实验详情
   * @author:
   * @date: 2023/10/25 10:33
   * @param: [id 实验id, code 学号]
   * @return: com.highgo.opendbt.experiment.model.ExperimentInfo
   **/
  @GetMapping("/getExperiment/{id}/{code}")
  public ExperimentInfo getExperiment(@PathVariable("id") long id, @PathVariable("code") String code) {
    logger.debug("Enter, id = " + id);
    return experimentService.getExperiment(id, code);
  }

  @GetMapping("/getImages")
  public List<TImages> getImages() {
    logger.debug("Enter,  = ");
    return imagesService.getImages();
  }

  @PostMapping("/saveExperiment")
  public boolean saveExperiment(HttpServletRequest request, @RequestBody @Valid ExperimentInfo experiment, BindingResult result) {
    ValidationUtil.Validation(result);
    return dockerCommandService.saveExperiment(request, experiment);
  }

  /**
   * @description: 删除实验
   * @author:
   * @date: 2023/10/16 11:16
   * @param: [id 实验id]
   * @return: boolean
   **/
  @GetMapping("/delExperiment/{id}")
  public boolean delExperiment(@PathVariable("id") long id) {
    logger.debug("Enter, id = " + id);
    experimentService.delExperiment(id);
    return containersService.delContainer(id);
  }

  /**
   * @description: 容器列表
   * @author:
   * @date: 2023/10/19 12:04
   * @param: [courseId 课程id]
   * @return: java.util.List<com.highgo.opendbt.experiment.model.ContainersInfo>
   **/
  @PostMapping("/listContainer")
  public PageInfo<ContainersInfo> listContainer(@RequestBody PageParam<TContainers> param) {
    logger.debug("Enter, id = " + param.getParam().getCourseId());
    if (CamelCaseToUnderscoreConverter.isSort(param.getOrderBy())) {
      PageHelper.startPage(param.getPageNum(), param.getPageSize()).setOrderBy(CamelCaseToUnderscoreConverter.convert(param.getOrderBy()).concat(" , update_time desc, create_time desc"));
    } else {
      PageHelper.startPage(param.getPageNum(), param.getPageSize()).setOrderBy(" update_time desc , create_time desc");
    }
    List<ContainersInfo> listContainer = containersService.listContainer(param.getParam().getCourseId(), param.getParam().getContainerName(), param.getParam().getCode());
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    for (ContainersInfo info : listContainer) {
      if (composeBuilder.getContainerStatus(info.getContainerId()) != null) {
        info.setStatus(composeBuilder.getContainerStatus(info.getContainerId()));
      }
    }
    return new PageInfo<ContainersInfo>(listContainer);
  }

  /**
   * @description:
   * @author: 关闭容器
   * @date: 2023/10/20 10:06
   * @param: [containerName]
   * @return: void
   **/
  @GetMapping("/stopContainer/{containerName}")
  public boolean stopContainer(@PathVariable("containerName") String containerName) {
    logger.debug("Enter, id = " + containerName);
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    composeBuilder.stopContainer(containerName);
    return true;
  }

  /**
   * @description: 删除容器
   * @author:
   * @date: 2023/10/20 15:01
   * @param: [request, containerName]
   * @return: boolean
   **/
  @GetMapping("/deleteContainer/{containerName}")
  public boolean deleteContainer(HttpServletRequest request, @PathVariable("containerName") String containerName) {
    logger.debug("Enter, id = " + containerName);
    return dockerCommandService.deleteContainer(request, dockerHost, dockerPort, containerName);
  }

  /**
   * @description: 备份
   * @author:
   * @date: 2023/10/20 16:39
   * @param: [request, container]
   * @return: void
   **/
  @PostMapping("/backUpContainer")
  public boolean backUpContainer(HttpServletRequest request, @Valid @RequestBody ContainersInfo container, BindingResult result) {
    logger.debug("Enter, id = " + container.toString());
    ValidationUtil.Validation(result);
    dockerCommandService.backUpContainer(request, dockerHost, dockerPort, container.getContainerName(), container.getImageName());
    return true;
  }

  /**
   * @description: 恢复
   * @author:
   * @date: 2023/10/20 16:39
   * @param: [request, backup]
   * @return: void
   **/
  @PostMapping("/restoreContainer")
  public boolean restoreContainer(HttpServletRequest request, @Valid @RequestBody TBackup backup, BindingResult result) {
    logger.debug("Enter, id = " + backup.toString());
    ValidationUtil.Validation(result);
    dockerCommandService.restoreContainer(request, userName, key, path, dockerHost, dockerPort, port, backup.getContainerName(), backup.getImageName(), backup.getBackupPath());
    return true;
  }

  /**
   * @description: 容器备份列表
   * @author:
   * @date: 2023/10/24 10:27
   * @param: [request, containerName 容器名称]
   * @return: boolean
   **/
  @GetMapping("/listContainerBackUp/{containerName}")
  public List<TBackup> listContainerBackUp(HttpServletRequest request, @PathVariable("containerName") String containerName) {
    logger.debug("Enter, id = " + containerName);
    return dockerCommandService.listContainerBackUp(request, containerName);
  }

  /**
   * @description: 镜像列表
   * @author:
   * @date: 2023/10/27 13:35
   * @param: [param]
   * @return: com.github.pagehelper.PageInfo<com.highgo.opendbt.experiment.domain.TImages>
   **/
  @PostMapping("/listImage")
  public List<TImages> listImage(@RequestBody TImages tImages) {
    logger.debug("Enter, id = " + tImages.toString());
    List<TImages> images = imagesService.list(new QueryWrapper<TImages>()
      //.eq("course_id", tImages.getCourseId())
      .eq("delete_flag", 0)
      .eq(StringUtils.isNotBlank(tImages.getId()), "image_name", tImages.getImageName()));
    return images;
  }

  /**
   * @description: 镜像上传
   * @author:
   * @date: 2023/10/27 13:41
   * @param: [request, file]
   * @return: boolean
   **/
  public boolean uploadImages(HttpServletRequest request, MultipartFile file) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //文件名
    String fileName = file.getOriginalFilename();
    //先存储到本地
    // String filePath = uploadFile(fileName, file);

    dockerCommandService.uploadImage(request, dockerHost, dockerPort, fileName, file);
    return true;
  }

  private String uploadFile(String fileName, MultipartFile file) {
    String folderPath = File.separator + "path" + File.separator + "images";
    File folderPathFile = new File(folderPath);

    if (!folderPathFile.exists()) {
      folderPathFile.mkdirs();
    }
    String filePath = folderPath + File.separator + fileName;
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      bos.write(file.getBytes());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new APIException(e.getMessage());
    }
    return filePath;
  }

}
