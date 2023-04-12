package com.highgo.opendbt.common.exception.enums;

import com.highgo.opendbt.common.exception.assertion.Assert;
import com.highgo.opendbt.common.exception.assertion.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bytedeco.librealsense.error;

/**
 * <p>业务</p>
 */
@Getter
@AllArgsConstructor
public enum BusinessResponseEnum implements BusinessExceptionAssert {

  UNCLASSIFIED(5001, "根据课程id={0}，班级id={1}未查询到班级信息"),
  UNEXERCISEiNFO(5002, "根据习题id={0}未查询到习题详情"),
  UNEXERCISE(5003, "习题id={0}不存在"),
  DUMPLICATEEXERCISE(5004, "习题ID={0}已在删除状态，不可重复删除"),
  CANNOTDELETEEXERCISE(5005, "习题ID={0}正在使用中，不可删除"),
  NOMIGRATIONEXERCISE(5006, "没有需要迁移的历史数据"),
  NOHOMEWORKMODEL(5007, "根据作业模板id={0}，未查询到模板信息"),
  UNEXERCISEiNFOBYMODEL(5008, "根据习题id={0},模板id={1}未查询到习题详情"),
  UNHOMEWORKMODEL(5009, "根据模板id={0}未查询到模板信息"),
  UNMODELEXERCISES(5010, "习题集合{0}中包含模板中已存在的习题"),
  UNMODELEXERCISE(5011, "根据模板id={0}，习题id={1}未查询到模板习题"),
  UNHOMEWORKMODELPACKAGE(5012, "根据id={0}未查询到文件夹信息"),
  EQUALSHOMEWORKMODELPACKAGE(5013, "相同目录无需移动"),
  HOMEWORKMODELUNCOMMIT(5014, "未查询到作业{0}的发布信息"),
  UNHOMEWORK(5015, "根据作业id:{0}未查询到作业信息"),
  UNHOMEWORKDISTRIBUTION(5016, "根据作业id：{0}未查询到作业发放信息"),
  BINDINGHOMEWORK(5017, "作业模板【{0}】绑定了作业无法删除"),
  UNEXERCISERESULT(5018, "习题答案不能为空"),
  UNSTUDENTHOMEWORK(5019, "根据作业id:{0},学生id:{1}未查询到学生作业信息"),
  UNUSERINFO(5020, "根据学生id:{0}未查询到学生信息"),
  UNCLASS(5021, "根据班级id:{0}未查询到班级信息"),
  NOTSAMERES(5022, "同一个目录下不能存在相同的资源id:{0}"),
  UNFINDCLASS(5023, "根据当前登录人{0}，课程id{1}，未查询到所在班级"),
  DUMPLICATECOURSE(5024, "课程名称{0}重复"),
  UNCLASSBYCOURSE(5025, "根据课程id：{0}查询到班级信息，无法删除"),
  UNCOURSE(5026, "根据课程id：{0}，未查询到课程信息"),
  FAILCOPYCOURSE(5027, "复制课程失败"),
  GETKNOWLEDGETREEEXERCISE(5028, "知识树获取异常"),
  KNOWLEDGETREECOURSE(5029, "知识树所属的课程信息获取失败"),
  GETCOPYSCENEFAIL(5030, "复制的场景信息获取失败"),
  SCENEISUSENOTDELETE(5031, "场景已被习题使用，不可删除"),
  FAILPUBLISH(5032, "发布失败"),
  FAILINIT(5033, "新增学生时初始化发布信息失败"),
  KEEPDIRECTORY(5034, "课程目录下至少保留一个目录"),
  UNALLOWDELETE(5035, "目录有子节点不允许删除"),
  FAILMOVE(5036, "目录移动失败"),
  NOTFOUNDDIRECTORY(5037, "未找到目录id为{0}的目录"),
  UNABLETOMOVEDOWN(5038, "目录在最底部无法移动"),
  UNABLETOMOVEUP(5038, "目录在最顶部无法移动"),
  ONLYTODIRECTORY(5039, "只能移动到文件夹"),
  FAILDELETEEXERCISEINFO(5040, "删除选项失败"),
  DUMPLICATEINFO(5041, "根据id{0}查询到多个选项"),
  FAILAPPROVAL(5042, "批阅提交失败"),
  FAILACALLBACK(5043, "打回失败"),
  NOTFOUNDEXERCISETYPE(5044, "未查询到习题类型{0}"),
  USERALREADYEXIST(5045, "学号(工号)为{0}的用户已经存在"),
  USERNOTEXIST(5046, "账号不存在"),
  ERRORPASSWORD(5047, "原密码输入错误"),
  LOGINACCOUNTNOTEXIST(5048, "{0}账号不存在"),
  LOGINPASSWORDERROR(5049, "{0}账号密码错误"),
  ACCOUNTISSTOP(5050, "{0}账号已停用，可联系管理员恢复使用"),
  FAILUPLOAD(5051, "上传失败"),
  NOTFOUNDRESOURCES(5052, "根据资源id{0}未查询到资源信息"),
  STUDENTALREADYCLASS(5053, "学号为【{0}】的学生已在本班级"),
  FAILBINDINGCLASS(5054, "绑定班级失败"),
  ANSWERISPROBLEM(5055, "参考答案出现问题，请联系老师: {0}"),
  EXECUTANSWERGETRESULTFILE(5056, "执行答案后获取结果集异常，请联系老师: {0}"),
  EXAMINFOGETFILE(5057, "作业信息获取失败"),
  FAILSEARCHSTUDYPROCESS(5058, "未查询到学生学习进度"),
  NOTFOUNDTSCENEDETAIL(5059, "未查询到相关场景表信息"),
  TABLECREATIONNOTIMPLEMENTED(5060, "未查询到校验点中的表{0}"),
  TABLEDESCRIPTIONINFORMATIONERROR(5061,"表描述信息错误，校验点设置表描述为{0}，答案中表描述为{1}"),
  TABLENOTDELETED(5062,"校验点表{0}未删除"),
  NOFIELDSFOUND(5063,"未查询到字段{0}"),
  DIFFERENTFIELDTYPES(5064,"{0}字段的校验点字段类型为{1}，答案中类型为{2}，两者不符"),
  FIELDLENGTHISDIFFERENT(5065,"{0}字段的校验点长度为{1}，答案中的长度为{2}，两者不符"),
  FIELDDEFAULTISDIFFERENT(5066,"{0}字段的校验点默认值为{1}，答案中的默认值为{2}，两者不符"),
  WHETHERITISNOTEMPTYISDIFFERENT(5067,"{0}字段的校验点非空约束为{1}，答案中的非空约束为{2}，两者不符"),
  FIELDDESCRIPTIONSDIFFER(5068,"{0}字段的校验点描述为{1}，答案中的描述为{2}，两者不符"),
  WHETHERSELFINCREASINGISDIFFERENT(5069,"{0}字段的校验点是否自增为{1}，答案中的是否自增为{2}，两者不符"),
  DIFFERENTDECIMALPLACES(5070,"{0}字段的校验点小数点位数为{1}，答案中的小数点位数为{2}，两者不符"),
  FIELDNOTDELETED(5071,"校验点字段{0}未删除"),

  INDEXDOESNOTEXIST(5072,"校验点索引{0}在答案中不存在"),
  INDEXNOTDELETED(5073,"校验点索引{0}未删除"),
  INDEXFIELDSAREDIFFERENT(5074,"{0}索引的校验点索引字段为{1}，答案中的索引字段为{2}，两者不符"),
  INDEXTYPESAREDIFFERENT(5075,"{0}索引的校验点索引类型为{1}，答案中的索引类型为{2}，两者不符"),
  ISTHEUNIQUEINDEXDIFFERENT(5076,"{0}索引的校验点是否唯一索引为{1}，答案中的索引是否唯一索引为{2}，两者不符"),
  INDEXDESCRIPTIONISDIFFERENT(5077,"{0}索引的校验点索引描述为{1}，答案中的索引描述为{2}，两者不符"),

  CONSTRAINTDOESNOTEXIST(5078,"校验点约束{0}在答案中不存在"),
  CONSTRAINTNOTDELETED(5079,"校验点约束{0}未删除"),
  CONSTRAINTFIELDSAREDIFFERENT(5080,"{0}约束的校验点约束字段为{1}，答案中的约束字段为{2}，两者不符"),
  CONSTRAINTTYPESAREDIFFERENT(5081,"{0}约束的校验点约束类型为{1}，答案中的约束类型为{2}，两者不符"),
  EXPRESSIONISDIFFERENT(5082,"{0}约束的校验点表达式为{1}，答案中的表达式为{2}，两者不符"),
  EXCLUSIVECONSTRAINTINDEXTYPESAREDIFFERENT(5083,"{0}约束的校验点排他约束索引类型为{1}，答案中的排他约束索引类型为{2}，两者不符"),

  FKNOTEXIST(5084,"校验点外键{0}在答案中不存在"),
  FKNOTDELETED(5085,"校验点外键{0}未删除"),
  FKFIELDSAREDIFFERENT(5086,"{0}外键的校验点外键字段为{1}，答案中的外键字段为{2}，两者不符"),
  REFERENCEAREDIFFERENT(5087,"{0}外键的校验点参照表为{1}，答案中的参照表为{2}，两者不符"),
  REFERENCEFIELDSDIFFERENT(5088,"{0}外键的参照表字段为{1}，答案中的参照表字段为{2}，两者不符"),
  UPDATERULEDIFFERENT(5089,"{0}外键的校验点更新规则为{1}，答案中的更新规则为{2}，两者不符"),
  DELETERULEDIFFERENT(5090,"{0}外键的校验点删除规则为{1}，答案中的删除规则为{2}，两者不符"),

  SEQNOTEXIST(5091,"校验点序列{0}在答案中不存在"),
  SEQNOTDELETED(5092,"校验点序列{0}未删除"),
  STEPDIFFERENT(5093,"{0}序列的校验点步长为{1}，答案中的序列步长为{2}，两者不符"),
  MINVALUEDIFFERENT(5094,"{0}序列的校验点最小值为{1}，答案中的序列最小值为{2}，两者不符"),
  MAXVALUEDIFFERENT(5095,"{0}序列的校验点最大值为{1}，答案中的序列最大值为{2}，两者不符"),
  LATESTVALUEDIFFERENT(5096,"{0}序列的校验点最新值为{1}，答案中的序列最新值为{2}，两者不符"),
  CYCLEDIFFERENT(5097,"{0}序列的校验点是否循环为{1}，答案中的序列是否循环为{2}，两者不符"),
  FIELDDIFFERENT(5098,"{0}序列的校验点列拥有为{1}，答案中的序列列拥有为{2}，两者不符"),
  REMARKDIFFERENT(5099,"{0}序列的校验点描述为{1}，答案中的序列描述为{2}，两者不符"),
  CANNOTMODIFYWHILEINUSE(5100,"场景正在使用中不能修改或删除"),
  ISVIEW(5101,"不支持的视图相关语句"),
  NOTFOUNDVIEW(5102,"未查询到相关视图结构"),
  FAILDELETEVIEW(5103,"视图{0}删除失败"),

  SAVEFAIL(6001, "保存失败"),
  UPDATEFAIL(6002, "更新失败"),
  DELFAIL(6003, "删除失败"),
  SAVEORUPDATEFAIL(6004, "保存更新失败"),
  FIELDNOTEMPTY(6005,"新增表时字段不能为空"),
  ;


    /**
   * 返回码
   */
  private int code;
  /**
   * 返回消息
   */
  private String message;


}
