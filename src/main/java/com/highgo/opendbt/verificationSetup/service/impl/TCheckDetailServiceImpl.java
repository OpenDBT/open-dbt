package com.highgo.opendbt.verificationSetup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.Authentication;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import com.highgo.opendbt.verificationSetup.domain.entity.*;
import com.highgo.opendbt.verificationSetup.domain.model.CheckDetailAndFields;
import com.highgo.opendbt.verificationSetup.domain.model.NewTableInfoDel;
import com.highgo.opendbt.verificationSetup.service.*;
import com.highgo.opendbt.verificationSetup.mapper.TCheckDetailMapper;
import com.highgo.opendbt.verificationSetup.tools.TableInfoUtil;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorAnswerProcessFactory;
import com.highgo.opendbt.verificationSetup.tools.generatorAnswerModule.GeneratorCreateTableProcess;
import com.highgo.opendbt.verificationSetup.tools.generatorSqlModule.TableInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保存校验表结构信息
 */
@Service
public class TCheckDetailServiceImpl extends ServiceImpl<TCheckDetailMapper, TCheckDetail>
  implements TCheckDetailService {
  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  TCheckFieldService checkFieldService;
  @Autowired
  TSceneDetailService sceneDetailService;
  @Autowired
  TCheckIndexService checkIndexService;
  @Autowired
  TCheckConstraintService checkConstraintService;
  @Autowired
  TCheckFkService checkFkService;
  @Autowired
  TCheckSeqService checkSeqService;
  @Autowired
  GeneratorCreateTableProcess generatorCreateTableProcess;

  /**
   * @description: 保存表级信息
   * @author:
   * @date: 2023/3/28 17:20
   * @param: [request, detail]
   * @return: boolean
   **/
  @Override
  public boolean saveCheckDetail(HttpServletRequest request, TCheckDetail detail) {
    UserInfo userInfo = Authentication.getCurrentUser(request);
    //根据校验点生成答案并验证答案
    generatorTableAnswer(detail, userInfo);
    return detailSave(detail);
  }


  //根据校验点生成答案
  private void generatorTableAnswer(TCheckDetail detail, UserInfo userInfo) {
    List<TCheckDetail> tCheckDetails = new ArrayList<>();
    tCheckDetails.add(detail);
    //生成答案
    StringBuilder answerSql = GeneratorAnswerProcessFactory.createEventProcess(TableInfoEvent.TABLE).generatorAnswer(tCheckDetails);
    logger.info("答案sql:" + answerSql.toString());
    //查询场景详情
    TSceneDetail sceneDetail = sceneDetailService.getById(detail.getSceneDetailId());
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, detail.getSceneDetailId() == null ? -1 : sceneDetail.getSceneId(), detail.getExerciseId(), answerSql);
  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean detailSave(TCheckDetail detail) {
    return this.saveOrUpdate(detail);
  }

  /**
   * @description: 保存表和字段信息
   * @author:
   * @date: 2023/3/28 17:21
   * @param: [request, detail]
   * @return: boolean
   **/
  @Override
  public boolean saveCheckDetailAndFields(HttpServletRequest request, CheckDetailAndFields detail) {
    // UserInfo userInfo = Authentication.getCurrentUser(request);
    UserInfo userInfo = new UserInfo();
    userInfo.setCode("003");

    //根据校验点生成答案并验证答案
    generatorAnswer(detail, userInfo);
    //保存
    return detailAndFieldsSave(detail);
  }

  private void generatorAnswer(CheckDetailAndFields detail, UserInfo userInfo) {

    StringBuilder answerSql = generatorCreateTableProcess.generatorAnswer(detail.getCheckDetail(), detail.getFields());
    //生成答案
    logger.info("答案sql:" + answerSql.toString());
    //执行验证答案
    TableInfoUtil.extractAnswer(userInfo, -1, detail.getCheckDetail().getExerciseId(), answerSql);
  }

  //保存
  @Transactional(rollbackFor = Exception.class)
  public boolean detailAndFieldsSave(CheckDetailAndFields detail) {
    boolean update = this.saveOrUpdate(detail.getCheckDetail());
    boolean batch = checkFieldService.saveOrUpdateBatch(detail.getFields());
    return update && batch;
  }


  /**
   * @description:新增信息删除
   * @author:
   * @date: 2023/3/29 11:01
   * @param: [request, id: check id, exerciseId: 习题id, types: TABLE,FIELD,INDEX,CONSTRAINT,FOREIGN_KEY,SEQUENCE]
   * @return: boolean
   **/
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteNewTableInfo(HttpServletRequest request, NewTableInfoDel infoDel) {
    //需要删除的相关校验表id
    List<Long> ids = infoDel.getIds();
    Integer exerciseId = infoDel.getExerciseId();
    String types = infoDel.getTypes();
    //删除表校验信息，将会把相关索引，约束，外键，字段，序列均删除
    if (TableInfoEvent.TABLE.toString().equals(types)) {
      for (Long id : ids) {
        //查询要删除的校验表
        TCheckDetail checkDetail = this.getById(id);
        //删除校验表
        boolean removeTable = this.removeById(id);
        BusinessResponseEnum.DELFAIL.assertIsTrue(removeTable);
        //查询场景详情id
        String tableName = checkDetail.getTableName();
        //查询字段校验
        List<TCheckField> fieldList = checkFieldService.list(new QueryWrapper<TCheckField>()
          .eq("table_name", tableName)
          .eq("exercise_id", exerciseId));
        //删除字段
        if (!fieldList.isEmpty()) {
          boolean removeField = checkFieldService.removeByIds(fieldList.stream().map(TCheckField::getId).collect(Collectors.toList()));
          BusinessResponseEnum.DELFAIL.assertIsTrue(removeField);
        }
        //查询索引校验
        List<TCheckIndex> indexList = checkIndexService.list(new QueryWrapper<TCheckIndex>()
          .eq("table_name", tableName)
          .eq("exercise_id", exerciseId));
        //删除索引
        if (!indexList.isEmpty()) {
          boolean removeIndex = checkIndexService.removeByIds(indexList.stream().map(TCheckIndex::getId).collect(Collectors.toList()));
          BusinessResponseEnum.DELFAIL.assertIsTrue(removeIndex);
        }
        //查询约束校验
        List<TCheckConstraint> constraints = checkConstraintService.list(new QueryWrapper<TCheckConstraint>()
          .eq("table_name", tableName)
          .eq("exercise_id", exerciseId));
        //删除约束
        if (!constraints.isEmpty()) {
          boolean removeConstraint = checkConstraintService.removeByIds(constraints.stream().map(TCheckConstraint::getId).collect(Collectors.toList()));
          BusinessResponseEnum.DELFAIL.assertIsTrue(removeConstraint);
        }
        //查询外键校验
        List<TCheckFk> fks = checkFkService.list(new QueryWrapper<TCheckFk>()
          .eq("table_name", tableName)
          .eq("exercise_id", exerciseId));
        //删除外键
        if (!fks.isEmpty()) {
          boolean removeFk = checkFkService.removeByIds(fks.stream().map(TCheckFk::getId).collect(Collectors.toList()));
          BusinessResponseEnum.DELFAIL.assertIsTrue(removeFk);
        }
        //查询序列校验
        List<TCheckSeq> seqList = checkSeqService.list(new QueryWrapper<TCheckSeq>()
          .eq("table_name", tableName)
          .eq("exercise_id", exerciseId));
        //删除序列
        if (!fks.isEmpty()) {
          boolean removeSeq = checkSeqService.removeByIds(seqList.stream().map(TCheckSeq::getId).collect(Collectors.toList()));
          BusinessResponseEnum.DELFAIL.assertIsTrue(removeSeq);
        }
      }

    }
    //删除新增字段
    if (TableInfoEvent.FIELD.toString().equals(types)) {
      //删除字段
      boolean removeField = checkFieldService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeField);
    }
    //删除新增索引
    if (TableInfoEvent.INDEX.toString().equals(types)) {
      boolean removeIndex = checkIndexService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeIndex);
    }
    //删除新增约束
    if (TableInfoEvent.CONSTRAINT.toString().equals(types)) {
      boolean removeConstraint = checkConstraintService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeConstraint);
    }
    //删除新增外键
    if (TableInfoEvent.FOREIGN_KEY.toString().equals(types)) {
      boolean removeFk = checkFkService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeFk);
    }
    //删除新增外序列
    if (TableInfoEvent.SEQUENCE.toString().equals(types)) {
      boolean removeSeq = checkSeqService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeSeq);
    }
    return true;
  }


}




