package com.yzy.ebag.teacher.base

/**
 * Created by YZY on 2018/1/9.
 */
object Constants {
    //java中调用时用getASSIGN_CATEGORY，不要加const，没什么卵用
    val ASSIGN_CATEGORY = "assign_category"
    val ASSIGN_TITLE = "assign_title"
    val ASSIGN_WORK = 1
    val ASSIGN_AFTER = 2
    val ASSIGN_TEST_PAPER = 4
    val WORK_TYPE = "work_type"

    val LEVEL_ONE = 1
    val LEVEL_TWO = 2
    val LEVEL_THREE = 3
    val LEVEL_FOUR = 4

    val PRIMARY_SCHOOL = "1"
    val JUNIOR_HIGH_SCHOOL = "2"
    val HIGH_SCHOOL = "3"

    val CREATE_CLASS_REQUEST = 101
    val CREATE_CLASS_RESULT = 102

    val PUBLISH_REQUEST = 110
    val PUBLISH_RESULT = 111

    val QUESTION_REQUEST = 120
    val QUESTION_RESULT = 121
    val PREVIEW_REQUEST = 130

    //状态 1 未批改 2 已批改 0 未完成 3 老师评语完成 4 家长签名和评语完成
    val CORRECT_UNFINISH =  "0"
    val CORRECT_UNCORRECT =  "1"
    val CORRECT_CORRECTED =  "2"
    val CORRECT_REMARKED = "3"
}