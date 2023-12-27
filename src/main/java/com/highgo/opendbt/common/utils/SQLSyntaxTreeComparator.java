package com.highgo.opendbt.common.utils;

/**
 * @Description: sql相似度比较
 * @Title: SQLSyntaxTreeComparator
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/14 16:20
 */

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SQLSyntaxTreeComparator {
  public static void main(String[] args) {
    String sql1 = "SELECT * FROM table1 WHERE column1 = 'value'";
    String sql2 = "SELECT column1 FROM table1 WHERE column1 = 'value'";

    double syntaxTreeSimilarity = calculateSyntaxTreeSimilarity(sql1, sql2);
    System.out.println("语法树相似度：" + syntaxTreeSimilarity);
  }

  // 计算语法树相似度
  public static double calculateSyntaxTreeSimilarity(String sql1, String sql2) {
    try {
      Select select1 = (Select) CCJSqlParserUtil.parse(sql1);
      Select select2 = (Select) CCJSqlParserUtil.parse(sql2);

      // 假设我们简单比较两棵语法树的节点数目作为相似度
      int nodeCount1 = countNodes(select1);
      int nodeCount2 = countNodes(select2);

      // 计算相似度
      return (double) Math.min(nodeCount1, nodeCount2) / Math.max(nodeCount1, nodeCount2);
    } catch (Exception e) {
      e.printStackTrace();
      return 0.0; // 解析错误，相似度为0
    }
  }

  // 递归计算语法树节点数目
  private static int countNodes(Select select) {
    PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
    // 在这里递归遍历子节点，可以根据具体情况遍历其他类型的节点
    return countNodes(plainSelect);
  }

  private static int countNodes(PlainSelect plainSelect) {
    // 在这里计算节点数目，可以遍历 plainSelect 中的各种子节点
    return 1; // 示例值，实际需要根据具体情况计算
  }
}
