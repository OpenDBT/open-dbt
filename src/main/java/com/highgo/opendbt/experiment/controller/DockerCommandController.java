package com.highgo.opendbt.experiment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.highgo.opendbt.common.bean.PageParam;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.common.utils.CamelCaseToUnderscoreConverter;
import com.highgo.opendbt.common.utils.ValidationUtil;
import com.highgo.opendbt.experiment.domain.TContainers;
import com.highgo.opendbt.experiment.domain.TExperiment;
import com.highgo.opendbt.experiment.domain.TExperimentDocuments;
import com.highgo.opendbt.experiment.domain.TImages;
import com.highgo.opendbt.experiment.manageer.DockerComposeBuilder;
import com.highgo.opendbt.experiment.manageer.DockerContainerManager;
import com.highgo.opendbt.experiment.model.ContainerConfig;
import com.highgo.opendbt.experiment.model.ContainerModel;
import com.highgo.opendbt.experiment.model.ExperimentInfo;
import com.highgo.opendbt.experiment.model.ExperimentPage;
import com.highgo.opendbt.experiment.service.*;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
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
    logger.debug("Enter,  progress= {}", config.toString());
    //校验
    ValidationUtil.Validation(result);
    DockerComposeBuilder composeBuilder = new DockerComposeBuilder(dockerHost, dockerPort);
    ContainerModel model = composeBuilder.createAndStartContainer(getPath(config.getStudentCode(), config.getImageName(), config.getImagePort()), config.getStudentCode(), config.getImageName(), userName, key, path, dockerHost, port);
    System.out.println("容器已创建并启动！");
    System.out.println("Docker Compose 构建和运行成功！".concat(model.getContainerId()));
    // 更新到数据库t_containers表 返回后刷新
    TContainers container = containersService.getOne(new QueryWrapper<TContainers>().eq("code", config.getStudentCode())
      .eq("image_id", config.getImageId()));
    if (container == null) {
      boolean res = containersService.save(new TContainers()
        .setCode(config.getStudentCode())
        .setImageId(config.getImageId())
        .setContainerId(model.getContainerId())
        .setContainerPort(model.getContainerPort())
      );
      if (!res) {
        throw new APIException("在线实验启动失败,请联系管理员");
      }
    }
    return model;
  }

  @GetMapping({"/image"})
  public void image() {
    DockerContainerManager dockerContainerManager = DockerContainerManager.getInstance();
    dockerContainerManager.buildImage(getPath("postgres.Dockerfile"), "cantos_pg");
    System.out.println("成功创建镜像！");
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
        String[] array = {"mp4", "avi", "mkv", "mov", "wmv"};
        List<String> videoExtensions = Arrays.asList(array); // 将数组转换为列表
        //判断图片类型
        String fileType = uniqueFileName.substring(uniqueFileName.lastIndexOf(".") + 1, uniqueFileName.length());
        if (fileType.equalsIgnoreCase("doc") || fileType.equalsIgnoreCase("docx")) {
          docHtmlDto = new Word2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsx")) {
          docHtmlDto = new Excel2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("pdf")) {
          docHtmlDto = new PDF2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else if (fileType.equalsIgnoreCase("ppt") || fileType.equalsIgnoreCase("pptx")) {
          docHtmlDto = new PPT2Html().doc2Html(uploadDir + File.separator + uniqueFileName);
          return docHtmlDto.getHtml();
        } else
          //图片
          if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("jpeg")
            || fileType.equalsIgnoreCase("png") || fileType.equalsIgnoreCase("bmp")
            || fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("webp")
            || fileType.equalsIgnoreCase("EPS") || fileType.equalsIgnoreCase("jfif")) {
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
}


