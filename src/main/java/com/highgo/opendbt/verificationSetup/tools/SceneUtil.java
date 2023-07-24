package com.highgo.opendbt.verificationSetup.tools;

import com.highgo.opendbt.course.domain.model.SceneDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 场景相关工具类
 * @Title: SceneUtil
 * @Package com.highgo.opendbt.tools
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/3/15 9:44
 */
public class SceneUtil {
  /**
   * @description: 解析SQL脚本获取表名
   * @author:
   * @date: 2023/3/15 9:39
   * @param: [sceneId, initShell]
   * @return: java.util.List<com.highgo.opendbt.course.domain.model.SceneDetail>
   **/
  public static List<SceneDetail> parsSQLGetTableName(int sceneId, String initShell) {
    List<SceneDetail> sceneDetailList = new ArrayList<SceneDetail>();

    String matchStr = "createtable";
    String matchDesc = "commentontable";
    // 去掉脚本字符串中的所有换行和空格
    String newStr = initShell.replaceAll("[\\t\\n\\r]", "").replace(" ", "");
    // 用分号分隔获取每个SQL
    String[] newStrArray = newStr.split(";");
    Map<String, String> desc = new HashMap<>();
    //得到表描述
    for (int i = 0; i < newStrArray.length; i++) {
      String element = newStrArray[i];
      //表描述
      if (element.toLowerCase().startsWith(matchDesc)) {
        String tableDesc = element.substring(element.toLowerCase().indexOf("is") + 2).replaceAll("'", "").replaceAll("\"", "");
        String tableName = element.substring(element.toLowerCase().indexOf(matchDesc) + 14, element.toLowerCase().indexOf("is")).trim().replaceAll("'", "").replaceAll("\"", "");
        desc.put(tableName, tableDesc);
      }
    }
    for (int i = 0; i < newStrArray.length; i++) {
      String element = newStrArray[i];
      // 转成小写判断是否是"createtable"开头
      if (element.toLowerCase().startsWith(matchStr)) {
        // 截取出表名
        String tableName = element.substring(matchStr.length(), element.indexOf("(")).replaceAll("'", "").replaceAll("\"", "");
        ;

        // 如果表名是加引号的就原样保存，否则就转成小写保存
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
          sceneDetailList.add(new SceneDetail(sceneId, tableName, desc.get(tableName.toLowerCase())));
        } else {
          sceneDetailList.add(new SceneDetail(sceneId, tableName.toLowerCase(), desc.get(tableName.toLowerCase())));
        }
      }
    }
    return sceneDetailList;
  }


}
