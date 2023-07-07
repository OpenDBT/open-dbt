package com.highgo.opendbt.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.highgo.opendbt.course.domain.entity.Exercise;
import com.highgo.opendbt.course.domain.model.ExerciseDisplay;
import com.highgo.opendbt.course.domain.model.ExercisePage;
import com.highgo.opendbt.course.domain.model.ImportExerciseTO;
import com.highgo.opendbt.verificationSetup.domain.model.ResponseModel;
import com.highgo.opendbt.verificationSetup.domain.model.TestRunModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public interface ExerciseService extends IService<Exercise> {

	 List<Exercise> getExerciseList(ExercisePage exercisePage);

	 PageInfo<Exercise> getExercise(ExercisePage exercisePage);

	 Integer updateExercise(HttpServletRequest request, Exercise exercise);

	 Integer deleteExercise(HttpServletRequest request, Long exerciseId);

	 Integer copyExercise(HttpServletRequest request, Long exerciseId);

	 Integer copyExerciseToMyCourse(HttpServletRequest request, Long exerciseId, int courseId);

	 ResponseModel testRunAnswer(HttpServletRequest request, TestRunModel exercise) throws Throwable;

	 List<Exercise> getExerciseInfoList(HttpServletRequest request, int sclassId, int courseId, int knowledgeId);

	 List<Exercise> getExerciseListByCourseId(int courseId);

	 Exercise getExerciseById(Long exerciseId);

	ExerciseDisplay getExerciseInfo(HttpServletRequest request, int sclassId, int courseId, Long exerciseId);

	 String exportExerciseList(HttpServletRequest request, ExercisePage exercisePage) throws Exception;

	 String uploadExerciseListFile(HttpServletRequest request, MultipartFile file);

	 String importExercise(HttpServletRequest request, ImportExerciseTO importExerciseTO);

	 Integer batchDeleteExercise(HttpServletRequest request, int[] exerciseIds);

	 Integer batchBuildScene(int sceneId, int[] exerciseIds);

}
