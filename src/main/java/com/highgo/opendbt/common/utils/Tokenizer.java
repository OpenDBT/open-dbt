package com.highgo.opendbt.common.utils;

/**
 * @Description: 分词工具类
 * @Title: Tokenizer
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/14 16:06
 */
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 中文分词工具类*/
public class Tokenizer {

  /**
   * 分词*/
  public static List<Word> segment(String sentence) {

    //1、 采用HanLP中文自然语言处理中标准分词进行分词
    List<Term> termList = HanLP.segment(sentence);

    //上面控制台打印信息就是这里输出的
    System.out.println(termList.toString());

    //2、重新封装到Word对象中（term.word代表分词后的词语，term.nature代表改词的词性）
    return termList.stream().map(term -> new Word(term.word, term.nature.toString())).collect(Collectors.toList());
  }
}
