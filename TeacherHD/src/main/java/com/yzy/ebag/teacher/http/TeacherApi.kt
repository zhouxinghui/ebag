package com.yzy.ebag.teacher.http

import com.yzy.ebag.teacher.bean.*
import ebag.core.http.network.RequestCallBack
import ebag.hd.bean.response.NoticeBean
import ebag.hd.http.EBagApi
import ebag.hd.http.EBagClient
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by caoyu on 2018/1/8.
 */
object TeacherApi {

    private val teacherService: TeacherService by lazy {
        EBagClient.createRetrofitService(TeacherService::class.java)
    }

    /**
     * 首页网络数据
     */
    fun firstPage(callback: RequestCallBack<FirstPageBean>){
        val jsonObject = JSONObject()
        jsonObject.put("roleCode", "teacher")
        EBagApi.request(teacherService.firstPage("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 班级
     */
    fun clazzSpace(callback: RequestCallBack<List<SpaceBean>>){
        EBagApi.request(teacherService.clazzSpace("v1"), callback)
    }

    /**
     * 布置作业页面
     */
    fun assignmentData(type: String, callback: RequestCallBack<AssignmentBean>){
        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        EBagApi.request(teacherService.assignmentData("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 根据班级查询班级下所有的学习小组
     */
    fun studyGroup(classId: String, callback: RequestCallBack<List<GroupBean>>){
        val jsonObject = JSONObject()
        jsonObject.put("classId", classId)
        EBagApi.request(teacherService.studyGroup("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 根据班级查询班级下所有的成员（老师，学生，家长）
     */
    fun clazzMember(classId: String, callback: RequestCallBack<ClassMemberBean>){
        val jsonObject = JSONObject()
        jsonObject.put("classId", classId)
        EBagApi.request(teacherService.clazzMember("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 创建学习小组
     */
    fun createGroup(classId: String, groupName: String, list: List<BaseStudentBean>, callback: RequestCallBack<String>){
        val jsonObject = JSONObject()
        jsonObject.put("classId", classId)
        jsonObject.put("groupName", groupName)
        jsonObject.put("studentCount", list.size)
        val jsonArray = JSONArray()
        list.forEach {
            val jsonObj = JSONObject()
            jsonObj.put("uid", it.uid)
            jsonObj.put("duties", it.duties)
            jsonArray.put(jsonObj)
        }
        jsonObject.put("clazzUserGroupVos", jsonArray)
        EBagApi.request(teacherService.createGroup("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 修改小组
     */
    fun modifyGroup(groupId: String, classId: String, groupName: String, list: List<BaseStudentBean>, callback: RequestCallBack<String>){
        val jsonObject = JSONObject()
        jsonObject.put("classId", classId)
        jsonObject.put("groupName", groupName)
        jsonObject.put("studentCount", list.size)
        jsonObject.put("groupId", groupId)
        val jsonArray = JSONArray()
        list.forEach {
            val jsonObj = JSONObject()
            jsonObj.put("uid", it.uid)
            jsonObj.put("duties", it.duties)
            jsonArray.put(jsonObj)
        }
        jsonObject.put("clazzUserGroupVos", jsonArray)
        EBagApi.request(teacherService.modifyGroup("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 根据年级 获取科目
     */
    fun getSubject(code: String, callback: RequestCallBack<List<BaseSubjectBean>>){
        val jsonObject = JSONObject()
        jsonObject.put("groupCode", "grade_subject")
        jsonObject.put("parentCode", code)
        EBagApi.request(teacherService.getBaseData("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 创建班级
     */
    fun createClass(schoolCode: String?, gradeCode: String?, className: String?, subjectCode: String?, callback: RequestCallBack<String>){
        val jsonObject = JSONObject()
        jsonObject.put("gradeCode", gradeCode)
        jsonObject.put("className", className)
        jsonObject.put("introduce", "")
        jsonObject.put("school", schoolCode)
        jsonObject.put("subCode", subjectCode)
        EBagApi.request(teacherService.createClass("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 根据班级获取教材版本
     */
    fun searchBookVersion(classesId: List<String>, callback: RequestCallBack<List<BookVersionBean>>){
        val jsonObject = JSONObject()
        jsonObject.put("clazzIds", JSONArray(classesId))
        EBagApi.request(teacherService.searchBookVersion("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 根据年级获取单元 和 题型信息
     */
    fun unitAndQuestion(type: String, gradeCode: String, callback: RequestCallBack<AssignmentBean>){
        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        jsonObject.put("gradeCode", gradeCode)
        EBagApi.request(teacherService.assignmentData("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 发布公告
     */
    fun publishNotice(classId: String, content: String, urls: String, callback: RequestCallBack<String>){
        val jsonObject = JSONObject()
        jsonObject.put("classId", classId)
        jsonObject.put("content", content)
        jsonObject.put("photoUrl", urls)
        EBagApi.request(teacherService.publishNotice("v1", EBagApi.createBody(jsonObject)), callback)
    }

    /**
     * 添加老师
     */
    fun addTeacher(classId: String, ysbCode: String, subCode: String, callback: RequestCallBack<String>){
        val jsonObject = JSONObject()
        jsonObject.put("ysbCode",ysbCode)
        jsonObject.put("classId",classId)
        jsonObject.put("subCode",subCode)
        EBagApi.request(teacherService.addTeacher("v1", EBagApi.createBody(jsonObject)), callback)
    }

    fun newestNotice(classId: String, callback: RequestCallBack<NoticeBean>){
        val jsonObject = JSONObject()
        jsonObject.put("classId",classId)
        EBagApi.request(teacherService.newestNotice("v1", EBagApi.createBody(jsonObject)), callback)
    }
}