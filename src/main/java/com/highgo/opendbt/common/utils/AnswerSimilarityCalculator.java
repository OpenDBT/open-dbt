package com.highgo.opendbt.common.utils;

/**
 * @Description: 相似度比较
 * @Title: CheatingDetectionExample
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/14 13:25
 */


import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class AnswerSimilarityCalculator {

  // 计算单选、多选、判断题相似性
  public static double calculateChoiceQuestionSimilarity(String answer1, String answer2) {
    return calculateSimilarity(answer1, answer2);
  }

  // 计算填空题相似性
  public static double calculateFillInTheBlankSimilarity(String answer1, String answer2) {
    //return calculateLevenshteinDistance(answer1, answer2);
    return calculateSimilarity(answer1, answer2);
  }

  // 计算简答题相似性（假设使用 TF-IDF 加权余弦相似度）
  public static double calculateShortAnswerSimilarity(String answer1, String answer2) {
    // 实现 TF-IDF 加权余弦相似度的计算
    return CosineSimilarity.getSimilarity(answer1, answer2);
  }

  // 计算 SQL 题相似性（假设使用语法树比较）
  public static double calculateSQLSimilarity(String sql1, String sql2) {
    // 实现语法树比较的计算
   // return SQLSyntaxTreeComparator.calculateSyntaxTreeSimilarity(sql1, sql2);
    return CosineSimilarity.getSimilarity(sql1, sql2);
  }


  // 计算 Jaccard 相似度
  public static double calculateJaccardSimilarity(String[] set1, String[] set2) {
    HashSet<String> set1HashSet = new HashSet<>(Arrays.asList(set1));
    HashSet<String> set2HashSet = new HashSet<>(Arrays.asList(set2));

    HashSet<String> intersection = new HashSet<>(set1HashSet);
    intersection.retainAll(set2HashSet);

    HashSet<String> union = new HashSet<>(set1HashSet);
    union.addAll(set2HashSet);

    if (union.isEmpty()) {
      return 0.0; // 两个集合都为空，相似度为0
    }

    return (double) intersection.size() / union.size();
  }
  public static double calculateSimilarity(String answer1, String answer2) {
    String[] tokens1 = answer1.split("[\\s@_@]+");
    String[] tokens2 = answer2.split("[\\s@_@]+");

    HashSet<String> set1 = new HashSet<>(Arrays.asList(tokens1));
    HashSet<String> set2 = new HashSet<>(Arrays.asList(tokens2));

    int commonCount = 0;
    int additionalWeight = 0; // 用于记录额外的权重
    for (String token : set1) {
      if (!token.isEmpty() && set2.contains(token)) {
        commonCount++;

        // 对于答案相同且不以 1 开头的情况，增加相似度权重，但要确保总的权重不超过1
        if (!token.startsWith("1")) {
          if (additionalWeight < 1 - commonCount) {
            additionalWeight++;
          }
        }
      }
    }

    int maxLen = Math.max(set1.size(), set2.size());
    double similarity = (double) commonCount / maxLen;

    // 确保相似度范围在 0 到 1 之间，且总权重不超过1
    double totalSimilarity = Math.min(1.0, similarity + additionalWeight);
    return totalSimilarity;
  }


  // 综合计算总的相似度
  public static double calculateSimilarity(Map<Integer, String> givenStudentTypeMap, Map<Integer, String> otherStudentTypeMap) {
    double totalSimilarity = 0.0;
    int totalTypes = 0;

    for (int type = 1; type <= 4; type++) {
      String givenAnswer = givenStudentTypeMap.get(type);
      String otherAnswer = otherStudentTypeMap.get(type);

      if (givenAnswer != null && otherAnswer != null) {
        double similarity = 0.0;

        if (type == 1) {
          similarity = calculateChoiceQuestionSimilarity(givenAnswer, otherAnswer);
        } else if (type == 2) {
          //similarity = 1.0 - (double) calculateFillInTheBlankSimilarity(givenAnswer, otherAnswer) / Math.max(givenAnswer.length(), otherAnswer.length());
          similarity = calculateChoiceQuestionSimilarity(givenAnswer, otherAnswer);
        } else if (type == 3) {
          similarity = calculateShortAnswerSimilarity(givenAnswer, otherAnswer);
        } else if (type == 4) {
          similarity = calculateSQLSimilarity(givenAnswer, otherAnswer);
        }
        // 对于错误的题目完全相同情况，赋予更高的相似度权重
        if (similarity == 1.0) {
          similarity *= 1.5; // 调整权重
        }
        totalSimilarity += similarity;
        totalTypes++;
      }
    }

    if (totalTypes > 0) {
      return totalSimilarity / totalTypes;
    } else {
      return 0.0;
    }
  }



  public static void main(String[] args) {
    AnswerSimilarityCalculator calculator = new AnswerSimilarityCalculator();

    String type = "choice";
    String answer1 = "A b c";
    String answer2 = "A b c";

    // double similarity = calculateChoiceQuestionSimilarity( answer1, answer2);
    //  System.out.println("题型：" + type + "，相似度：" + similarity);
  }

  //更新相似度
  public void getSimilarity(int homeworkId) {
    //根据作业id作业id查询作业答案
    //TStuHomeworkInfoVO

  }

  // 计算 Levenshtein 距离
  private static int calculateLevenshteinDistance(String str1, String str2) {
    int[][] dp = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      for (int j = 0; j <= str2.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          dp[i][j] = Math.min(
            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
            dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1)
          );
        }
      }
    }

    return dp[str1.length()][str2.length()];
  }


}
