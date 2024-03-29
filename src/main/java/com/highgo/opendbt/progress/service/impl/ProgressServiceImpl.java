package com.highgo.opendbt.progress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.highgo.opendbt.common.bean.ResultTO;
import com.highgo.opendbt.common.exception.APIException;
import com.highgo.opendbt.common.exception.BusinessException;
import com.highgo.opendbt.common.exception.enums.BusinessResponseEnum;
import com.highgo.opendbt.common.utils.*;
import com.highgo.opendbt.exam.service.TScoreService;
import com.highgo.opendbt.exercise.domain.entity.TNewExercise;
import com.highgo.opendbt.exercise.domain.model.PublishExercise;
import com.highgo.opendbt.exercise.domain.model.SharedExercise;
import com.highgo.opendbt.exercise.mapper.TNewExerciseMapper;
import com.highgo.opendbt.exercise.service.TNewExerciseService;
import com.highgo.opendbt.scene.domain.entity.TScene;
import com.highgo.opendbt.scene.mapper.TSceneMapper;
import com.highgo.opendbt.scene.service.TSceneService;
import com.highgo.opendbt.score.domain.model.SubmitResult;
import com.highgo.opendbt.progress.manage.AsyncSubmitAnswer;
import com.highgo.opendbt.progress.mapper.ProgressMapper;
import com.highgo.opendbt.progress.model.*;
import com.highgo.opendbt.progress.service.ProgressService;
import com.highgo.opendbt.sclass.domain.entity.Sclass;
import com.highgo.opendbt.sclass.mapper.SclassMapper;
import com.highgo.opendbt.score.domain.model.Score;
import com.highgo.opendbt.score.mapper.ScoreMapper;
import com.highgo.opendbt.student.manage.AsyncSubmitExerciseAnswer;
import com.highgo.opendbt.system.domain.entity.UserInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private ProgressMapper progressMapper;
  @Autowired
  private SclassMapper sclassMapper;
  @Autowired
  private ScoreMapper scoreMapper;
  @Autowired
  private ThreadPoolTaskExecutor threadPoolTaskExecutor;
  @Autowired
  private TNewExerciseService exerciseService;
  @Autowired
  private TScoreService scoreService;
  @Autowired
  private TNewExerciseMapper exerciseMapper;
  @Autowired
  private AsyncSubmitAnswer asyncSubmitAnswer;
  @Autowired
  private AsyncSubmitExerciseAnswer asyncSubmitExerciseAnswer;
  @Autowired
  private TSceneService sceneService;
  @Autowired
  private TSceneMapper sceneMapper;

  @Override
  public Sclass getCourseProgressByStu(HttpServletRequest request, int classId, int courseId) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    return sclassMapper.getCourseProgressByStu(loginUser.getUserId(), classId, courseId);

  }

  @Override
  public List<StuKnowledgeExerciseInfo> getStuKnowledgeExerciseInfo(HttpServletRequest request, int classId, int courseId, int number) {
    // 获取用户信息
    UserInfo loginUser = Authentication.getCurrentUser(request);
    // 获取有习题的知识点的list
    List<StuKnowledgeExerciseInfo> stuKnowledgeExerciseInfoList = progressMapper
      .getStuKnowledgeExerciseInfo(loginUser.getUserId(), classId, courseId, number);
    // 如果前端页面需要的个数为0或者有习题的知识点的list的大小小于前端页面需要的个数则添加未分组的习题信息
    if (number == 0 || stuKnowledgeExerciseInfoList.size() < number) {
      StuKnowledgeExerciseInfo exerciseNumberTO = progressMapper.getNotKnowledgeExerciseCount(courseId);
      if (null != exerciseNumberTO) {
        int exerciseNumber = exerciseNumberTO.getExerciseNumber();
        if (exerciseNumber != 0) {
          StuKnowledgeExerciseInfo stuKnowledgeExerciseInfo = progressMapper
            .getNotKnowledgeExerciseInfo(loginUser.getUserId(), classId, courseId);
          stuKnowledgeExerciseInfo.setName(Message.get("NotGroup"));
          stuKnowledgeExerciseInfoList.add(stuKnowledgeExerciseInfo);
        }
      }
    }
    return stuKnowledgeExerciseInfoList;
  }

  @Override
  public ResultTO<List<KnowledgeExerciseCount>> getKnowExerciseCountByCourseId(int courseId) {
    try {
      return ResultTO.OK(progressMapper.getKnowExerciseCountByCourseId(courseId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<KnowledgeExerciseCount>> getStuCourseKnowledgeItemProgress(HttpServletRequest request, int courseId) {
    try {
      // 获取用户信息
      UserInfo loginUser = Authentication.getCurrentUser(request);
      return ResultTO.OK(progressMapper.getStuCourseKnowledgeItemProgress(courseId, loginUser.getUserId()));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<SclassCorrect>> getSclassCorrect(HttpServletRequest request, int sclassId) {
    try {
      // 正确率
      return ResultTO.OK(progressMapper.getSclassCorrect(sclassId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<SclassCoverage>> getSclassCoverage(HttpServletRequest request, int sclassId, int isFuzzyQuery, String searchValue) {
    try {
      // 覆盖率
      if (isFuzzyQuery == 0 || searchValue.trim().equals("")) {
        return ResultTO.OK(progressMapper.getSclassCoverage(sclassId));
      } else {
        return ResultTO.OK(progressMapper.getSclassCoverageByNameAndCode(sclassId, searchValue.trim()));
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<StudentCorrect>> getStudentCorrect(HttpServletRequest request, int sclassId, int userId) {
    try {
      // 正确率
      return ResultTO.OK(progressMapper.getStudentCorrect(sclassId, userId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<StudentCoverage>> getStudentCoverage(HttpServletRequest request, int sclassId, int userId) {
    try {
      // 覆盖率
      return ResultTO.OK(progressMapper.getStudentCoverage(sclassId, userId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<List<Score>> getStuAnswerSituation(int classId, int userId) {
    try {
      return ResultTO.OK(scoreMapper.getStuAnswerSituation(classId, userId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<Score> getStuScoreById(int scoreId) {
    try {
      return ResultTO.OK(scoreMapper.getStuScoreById(scoreId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }

  @Override
  public ResultTO<String> exportStatisticsInfo(HttpServletRequest request, int sclassId, int type, int isFuzzyQuery, String searchValue) {
    try {
      // 获取用户信息
      UserInfo loginUser = Authentication.getCurrentUser(request);

      // 班级情况文件名
      String fileName = null;
      // 表头字段名
      String[] columnNameArray = new String[0];
      List<Map<Integer, Object>> mapList = new ArrayList<Map<Integer, Object>>();

      if (type == 2) {
        fileName = "student_statistics_" + loginUser.getCode() + "_" + TimeUtil.getDateTimeStr() + ".xls";
        columnNameArray = new String[]{"学号", "姓名", "答对题数量", "答题数量", "提交次数", "总题目数"};

        // 获取班级统计情况数据
        List<SclassCoverage> sclassCoverageList = new ArrayList<SclassCoverage>();
        if (isFuzzyQuery == 0 || searchValue.trim().equals("")) {
          sclassCoverageList = progressMapper.getSclassCoverage(sclassId);
        } else {
          sclassCoverageList = progressMapper.getSclassCoverageByNameAndCode(sclassId, searchValue.trim());
        }

        // 转换为map的list，方便写入文件时每个字段的循环以及公共方法参数一致
        for (int i = 0; i < sclassCoverageList.size(); i++) {
          SclassCoverage sclassCoverage = sclassCoverageList.get(i);

          Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
          dataMap.put(0, sclassCoverage.getCode());
          dataMap.put(1, sclassCoverage.getUserName());
          dataMap.put(2, sclassCoverage.getCorrectCount());
          dataMap.put(3, sclassCoverage.getAnswerCount());
          dataMap.put(4, sclassCoverage.getSubmitAnswerCount());
          dataMap.put(5, sclassCoverage.getExerciseCount());
          mapList.add(dataMap);
        }
      } else {
        fileName = "class_statistics_" + loginUser.getCode() + "_" + TimeUtil.getDateTimeStr() + ".xls";
        columnNameArray = new String[]{"习题编号", "习题名称", "答对人数", "已回答人数", "全班学生"};

        // 获取班级统计情况数据
        List<SclassCorrect> sclassCorrectList = progressMapper.getSclassCorrect(sclassId);

        // 转换为map的list，方便写入文件时每个字段的循环以及公共方法参数一致
        for (int i = 0; i < sclassCorrectList.size(); i++) {
          SclassCorrect sclassCorrect = sclassCorrectList.get(i);

          Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
          dataMap.put(0, sclassCorrect.getId());
          dataMap.put(1, sclassCorrect.getExerciseName());
          dataMap.put(2, sclassCorrect.getCorrectCount());
          dataMap.put(3, sclassCorrect.getAnswerCount());
          dataMap.put(4, sclassCorrect.getStuCount());
          mapList.add(dataMap);
        }
      }

      // 调用写入文件方法
      ExcelUtil.writeXLS(fileName, columnNameArray, mapList);

      // 返回文件名，用于页面文件的下载
      return ResultTO.OK(fileName);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultTO.FAILURE(e.getMessage());
    }
  }


  /**
   * @description: 学生练习答题情况重置
   * @author:
   * @date: 2023/2/20 14:44
   * @param: [request, courseId 课程id]
   * @return: java.lang.Integer
   **/
  @Override
  public boolean exerciseReset(HttpServletRequest request, Integer courseId) {
    boolean removeByIds = true;
    //当前登录人员
    UserInfo loginUser = Authentication.getCurrentUser(request);
    //查询当前登录人答题情况的id集合
    List<Integer> ids = exerciseMapper.findExerciseScoreInfos(loginUser.getUserId(), courseId);
    //重置学生练习答题情况
    if (ids != null && !ids.isEmpty()) {
      removeByIds = scoreService.removeByIds(ids);
      BusinessResponseEnum.DELFAIL.assertIsTrue(removeByIds);
    }
    return removeByIds;
  }

  /**
   * @description: 题库习题设置为练习/取消练习
   * @author:
   * @date: 2023/2/17 16:03
   * @param: [request, param]
   * @return: com.highgo.opendbt.exercise.domain.entity.TNewExercise
   **/
  @Override
  public Integer publishExercise(HttpServletRequest request, PublishExercise param) {
    //查询需要设置的习题
    List<TNewExercise> exercises = exerciseService.listByIds(param.getIds());
    //需要更新的题目
    List<TNewExercise> publicise = new ArrayList<>();
    //判空
    BusinessResponseEnum.UNEXERCISE.assertIsNotEmpty(exercises, Arrays.toString(param.getIds().toArray()));
    //获取需要更新的题目
    childExercises(exercises, publicise);
    //设置习题是否练习题属性并保存
    Integer saveNum = null;
    if (!publicise.isEmpty()) {
      List<TNewExercise> exerciseList = publicise.stream()
        .map(item -> item.setExerciseStatus(param.getExerciseStatus())
          .setShowAnswer(param.getShowAnswer()))
        .collect(Collectors.toList());
      //保存
      saveNum = exerciseMapper.pgInsertOrUpdateBatch(exerciseList);
    }
    //更新题目
    return saveNum;
  }

  @Override
  public SubmitResult startVerifyAnswerThread(HttpServletRequest request, Score score, int exerciseSource, boolean isSaveSubmitData, int entranceType) {
    UserInfo loginUser = Authentication.getCurrentUser(request);
    CompletableFuture<SubmitResult> future = asyncSubmitExerciseAnswer.submitAnswer(loginUser, score, isSaveSubmitData);
    try {
      return future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new APIException("提交失败");
    } catch (ExecutionException e) {
      e.printStackTrace();
      if (e.getCause() instanceof BusinessException) {
        e.printStackTrace();
        throw (BusinessException) e.getCause();
      } else {
        throw new APIException("提交失败");
      }
    }
  }

  @Override
  public SubmitResult testStudentAnswer(HttpServletRequest request, Score score) throws Throwable {
    UserInfo loginUser = Authentication.getCurrentUser(request);
    CompletableFuture<SubmitResult> future = asyncSubmitExerciseAnswer.testSubmitAnswer(loginUser, score);

    try {
      return future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new APIException("运行失败");
    } catch (ExecutionException e) {
      e.printStackTrace();
      throw ExceptionUtils.getInnermostException(e);
    }
  }

  /**
   * @description: 共享习题
   * @author:
   * @date: 2023/7/31 14:42
   * @param: [request, param]
   * @return: java.lang.Integer
   **/
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer sharedExercise(HttpServletRequest request, SharedExercise param) {
    //获取文件夹下的所有习题
    List<TNewExercise> publicise = gettNewExercises(param);
    //设置习题共享属性并保存
    Integer saveNum = null;
    //需要更新的场景
    List<TScene> tScenes = new ArrayList<>();
    if (!publicise.isEmpty()) {
      //更新习题共享属性
      saveNum = updateExerciseAuthType(param, publicise);

      //筛选出同时需要更新的场景id
      List<Integer> sceneIds = publicise.stream()
        .map(item -> item.getSceneId())
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
      //查询所有场景信息
      long a=System.currentTimeMillis();
      List<TScene> sceneList = sceneService.list();
      long b=System.currentTimeMillis();
     logger.info("查询场景用时="+(b-a));
      //设置为共享，直接设置
      if (param.getAuthType() == 2) {
        for (int sceneId : sceneIds) {
          updateSceneAuthType(sceneId, 2, tScenes,sceneList);
        }
      } else {
        //设置为私有 先查询有无其他题目使用该场景且为共享状态，是：不做设置，否：设置为私有
        for (int sceneId : sceneIds) {
          // 查询是否有其他题目使用了该场景且为共享状态
          boolean match = isMatch(publicise, sceneId);

          if (!match) {
            updateSceneAuthType(sceneId, 1, tScenes, sceneList);
          }
        }
      }
      if (tScenes.size() > 0) {
        sceneMapper.pgInsertOrUpdateBatch(tScenes);
      }
    }
    //更新题目
    return saveNum;
  }

  private Integer updateExerciseAuthType(SharedExercise param, List<TNewExercise> publicise) {
    Integer saveNum;
    List<TNewExercise> exerciseList = publicise.stream()
      .map(item -> item.setAuthType(param.getAuthType()))
      .collect(Collectors.toList());
    //保存
    saveNum = exerciseMapper.pgInsertOrUpdateBatch(exerciseList);
    return saveNum;
  }

  private List<TNewExercise> gettNewExercises(SharedExercise param) {
    //查询需要设置的习题
    List<TNewExercise> exercises_all = exerciseService.listByIds(param.getIds());
    List<TNewExercise> exercises = exercises_all.stream()
      .filter(exercise -> exercise.getCourseId() == param.getCourseId())
      .collect(Collectors.toList());

    //需要更新的题目
    List<TNewExercise> publicise = new ArrayList<>();
    //判空
    BusinessResponseEnum.UNEXERCISE.assertIsNotEmpty(exercises, Arrays.toString(param.getIds().toArray()));
    //获取需要更新的题目
    childExercises(exercises, publicise);
    return publicise;
  }

  // 查询是否有其他题目使用了该场景且为共享状态
  private boolean isMatch(List<TNewExercise> publicise, Integer sceneId) {
    //当前使用需要变更共享状态的题目
    List<TNewExercise> currentExercises = publicise.stream()
      .filter(exercise -> sceneId.equals(exercise.getSceneId())).collect(Collectors.toList());
    //所有当前场景下的题目
    List<TNewExercise> list = exerciseService.list(new QueryWrapper<TNewExercise>()
      .eq("scene_id", sceneId).eq("delete_flag", 0));
    //去掉当前需要变更的题目
    if (list != null && list.size() > 0) {
      list.removeAll(currentExercises);
    }
    //判断是否还有当前场景的私有共享状态的题目
    return list.stream().anyMatch(ex -> ex.getSceneId() == sceneId && ex.getAuthType() == 2);
  }

  /**
   * @description: 更新场景的共享状态
   * @author:
   * @date: 2023/8/1 10:35
   * @param: [sceneId, authType, scenes 需要更新的场景, tScenes 所有的场景]
   * @return: void
   **/
  private void updateSceneAuthType(int sceneId, int authType, List<TScene> scenes, List<TScene> tScenes) {
    TScene scene = tScenes.stream().filter(item -> item.getSceneId() == sceneId).findFirst()
      .orElse(null);
    if (scene != null) {
      scene.setAuthType(authType);
      scenes.add(scene);
    }
  }

  private void childExercises(List<TNewExercise> exercises, List<TNewExercise> publicise) {
    //筛选出文件夹
    List<TNewExercise> exercisesPackage = exercises.stream().filter(item -> item.getElementType() == 1).collect(Collectors.toList());
    //筛选出题目
    List<TNewExercise> exerciseList = exercises.stream().filter(item -> item.getElementType() == 0).collect(Collectors.toList());
    if (!exerciseList.isEmpty()) {
      publicise.addAll(exerciseList);
    }
    if (!exercisesPackage.isEmpty()) {
      for (TNewExercise exercise : exercisesPackage) {
        //查询子目录
        List<TNewExercise> childExerciseList = exerciseService.list(new QueryWrapper<TNewExercise>()
          .eq("parent_id", exercise.getId())
          .eq("delete_flag", 0));
        if (!childExerciseList.isEmpty()) {
          //查询子项目
          childExercises(childExerciseList, publicise);
        }
      }
    }
  }
}
