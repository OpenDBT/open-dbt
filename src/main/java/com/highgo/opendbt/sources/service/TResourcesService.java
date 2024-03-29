package com.highgo.opendbt.sources.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.highgo.opendbt.sources.domain.entity.TResources;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface TResourcesService extends IService<TResources> {
  //上传资源
  TResources uploadResources(HttpServletRequest request, MultipartFile file) throws IOException;

  //读取资源
  void readResourse(HttpServletRequest request, HttpServletResponse response, int id, String resourcesType);

  //资源树
  List<TResources> listResourcesTree(HttpServletRequest request, TResources resources);

  //修改资源共享状态
  boolean updateAuthType(HttpServletRequest request, TResources resources);

  //删除资源
  boolean delResources(HttpServletRequest request, TResources resources);
}
