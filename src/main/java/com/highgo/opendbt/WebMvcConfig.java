package com.highgo.opendbt;

import com.highgo.opendbt.common.utils.ExcelUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * SpringBoot 自定义拦截器
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	Logger logger = LoggerFactory.getLogger(getClass());
  @Value("${baseDir}")
  private String staticResourcesLocation;
  @Value("${upload.dir}")
  private String staticResourcesImgLocation;
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOriginPatterns("*")
				.allowedHeaders("*")
				.allowCredentials(true)
				.allowedMethods("*");
	}

	// 欢迎页
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:index.html");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new JwtInterceptor()) //注册自定义拦截器
				.addPathPatterns("/**") //拦截所有路径
				.excludePathPatterns("/login") //排除登陆请求
        .excludePathPatterns("/stomp/**");
	}
	@SneakyThrows
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//String path = new File(ResourceUtils.getURL("classpath:").getPath()).getParent();
    String path =staticResourcesImgLocation;
    logger.info("===============path:".concat(path));
    //logger.info("===============ExcelUtil.getProjectPath():".concat(ExcelUtil.getProjectPath()));

		//String dirpath = URLDecoder.decode(path, "UTF-8");
		String folderPath = path + File.separator + "resourcesStore";
		registry.addResourceHandler("/readResourse/image/**").addResourceLocations("file:"+folderPath+File.separator+"image/");
		registry.addResourceHandler("/readResourse/video/**").addResourceLocations("file:"+folderPath+File.separator+"video/");
		registry.addResourceHandler("/readResourse/excel/**").addResourceLocations("file:"+folderPath+File.separator+"excel/");
		registry.addResourceHandler("/readResourse/word/**").addResourceLocations("file:"+folderPath+File.separator+"word/");
		registry.addResourceHandler("/readResourse/pdf/**").addResourceLocations("file:"+folderPath+File.separator+"pdf/");
		registry.addResourceHandler("/readResourse/ppt/**").addResourceLocations("file:"+folderPath+File.separator+"ppt/");
    registry.addResourceHandler("/readResourse/upload/**").addResourceLocations("file:"+ staticResourcesLocation +"/");
    registry.addResourceHandler("/readResourse/img/**").addResourceLocations("file:"+ path +"/");
    registry.addResourceHandler("/readResourse/tinymce_ppt/**").addResourceLocations("file:"+ staticResourcesLocation +"/");
    registry.addResourceHandler("/uploadedavatar/**").addResourceLocations("file:" + path + "/uploadedavatar" + "/");
	}

  }
