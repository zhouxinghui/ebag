package com.yzy.ebag.student.activity.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yzy.ebag.student.R
import com.yzy.ebag.student.activity.SettingActivity
import com.yzy.ebag.student.activity.center.PersonalActivity
import com.yzy.ebag.student.activity.homework.ErrorTopicActivity
import com.yzy.ebag.student.activity.homework.HomeworkActivity
import com.yzy.ebag.student.activity.tools.ToolsActivity
import com.yzy.ebag.student.bean.ClassListInfoBean
import com.yzy.ebag.student.bean.ClassesInfoBean
import com.yzy.ebag.student.dialog.ClassesDialog
import ebag.core.base.mvp.MVPActivity
import ebag.core.util.SerializableUtils
import ebag.core.util.StringUtils
import ebag.core.util.T
import ebag.core.util.loadHead
import ebag.hd.base.Constants
import ebag.hd.bean.response.UserEntity
import ebag.hd.ui.activity.BookListActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MVPActivity(), MainView {

    private val mainPresenter = MainPresenter(this,this)
    private val adapter = Adapter()
    private var classId = ""
    private var classesInfo: List<ClassListInfoBean>? = null

    override fun destroyPresenter() {
        mainPresenter.onDestroy()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {

        rvTeacherName.layoutManager = LinearLayoutManager(this)
        rvTeacherName.adapter = adapter

        stateView.setBackgroundRes(R.color.main_bg_color)
        initUserInfo()
        initListener()
        getMainClassInfo()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getMainClassInfo()
    }

    override fun mainInfoStart() {
        stateView.showLoading()
    }

    override fun mainInfoSuccess(classesInfoBean: ClassesInfoBean) {
        showTeachers(classesInfoBean)
        stateView.showContent()

    }

    override fun mainInfoError(exception: Throwable) {
        stateView.showError()
    }

    private fun showTeachers(classesInfoBean: ClassesInfoBean){
        classesInfo = classesInfoBean.resultAllClazzInfoVos
        classId = classesInfoBean.classId
        tvGrade.text = getString(R.string.main_class_name,classesInfoBean.className)
        tvClassTeacher.text = getString(R.string.main_teacher_name, classesInfoBean.teacherName)
        tvTeachersTip.text = getString(R.string.main_teachers_tip)

        adapter.setNewData(classesInfoBean.resultClazzInfoVos)
    }

    private fun getMainClassInfo(){
        mainPresenter.mainInfo(classId)
    }

    private fun initUserInfo(){

        val userEntity = SerializableUtils.getSerializable<UserEntity>(Constants.STUDENT_USER_ENTITY)
        if(userEntity != null){
            tvName.text = userEntity.name
            tvId.text = userEntity.ysbCode
            ivHead.loadHead(userEntity.headUrl)
        }
    }

    private fun initListener(){

        val click = View.OnClickListener{
            startActivity(Intent(this, PersonalActivity::class.java))
        }
        //名字
        tvName.setOnClickListener(click)
        //id
        tvId.setOnClickListener(click)
        //头像
        ivHead.setOnClickListener(click)

        //课后作业
        tvKHZY.setOnClickListener{
            if(StringUtils.isEmpty(classId)) {
                T.show(this, "请点击加载左侧班级数据！")
            }else{
                HomeworkActivity.jump(this,com.yzy.ebag.student.base.Constants.KHZY_TYPE,classId)
            }
        }
        //随堂作业
        tvSTZY.setOnClickListener{
            if(StringUtils.isEmpty(classId)) {
                T.show(this, "请点击加载左侧班级数据！")
            }else{
                HomeworkActivity.jump(this,com.yzy.ebag.student.base.Constants.STZY_TYPE,classId)
            }
        }

        //考试试卷
        tvKSSJ.setOnClickListener{
            HomeworkActivity.jump(this, com.yzy.ebag.student.base.Constants.KSSJ_TYPE, classId)
        }
        //学习课本点击事件
        tvXXKB.setOnClickListener{
            startActivity(Intent(this, BookListActivity::class.java))
        }
        //课程表
        tvKCB.setOnClickListener{

        }
        //设置
        tvSetup.setOnClickListener{
            SettingActivity.jump(this)
        }
        //定位
        tvPosition.setOnClickListener{

        }
        //学习小组
        llLearnGroup.setOnClickListener{

        }
        //学习园地
        llLearnPlace.setOnClickListener{

        }

        //我的错题
        btnMyError.setOnClickListener{
            ErrorTopicActivity.jump(this, classId)
        }
        //我的同学
        btnMyClassmate.setOnClickListener{

        }
        //我的书库
        btnMyBook.setOnClickListener{

        }
        //学习工具
        btnDailyPractice.setOnClickListener{
            ToolsActivity.jump(this, classId)
        }

        //点击班级
        tvGradeLayout.setOnClickListener {
            showClasses()
        }


        stateView.setOnRetryClickListener {
            getMainClassInfo()
        }
        tvMoreAnnounce.setOnClickListener {
            if(StringUtils.isEmpty(classId)) {
                T.show(this, "请点击加载左侧班级数据！")
            }else{
                AnnounceActivity.jump(this, classId)
            }
        }

    }

    private val classesDialog by lazy {
        val classes = ClassesDialog.newInstance()
        classes.setOnClassChooseListener{
            classes.dismiss()
            if(it?.classId != classId){
                classId = it?.classId ?: ""
                getMainClassInfo()
            }
        }
        classes
    }

    private fun showClasses(){
        classesDialog.updateData(classesInfo, classId)
        classesDialog.show(supportFragmentManager,"classes")
    }

    class Adapter: BaseQuickAdapter<ClassesInfoBean,BaseViewHolder>(R.layout.item_activity_main_class_info){
        override fun convert(helper: BaseViewHolder?, item: ClassesInfoBean?) {
            helper?.setText(R.id.tv, "${item?.subject} : ${item?.teacherName}")
        }
    }
}
