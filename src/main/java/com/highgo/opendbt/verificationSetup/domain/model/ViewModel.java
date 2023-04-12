package com.highgo.opendbt.verificationSetup.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @Description: 查询视图是否存在的中间类
 * @Title: ViewModel
 * @Package com.highgo.opendbt.verificationSetup.domain.model
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/4/10 17:31
 */
@Data
public class ViewModel {
  @TableField
  private Boolean exists;
}
