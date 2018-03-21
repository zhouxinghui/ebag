package ebag.hd.activity

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import ebag.core.base.BaseActivity
import ebag.core.http.network.MsgException
import ebag.core.http.network.RequestCallBack
import ebag.core.http.network.handleThrowable
import ebag.core.util.SerializableUtils
import ebag.hd.R
import ebag.hd.base.Constants
import ebag.hd.bean.ReportBean
import ebag.hd.bean.response.UserEntity
import ebag.hd.homework.DoHomeworkActivity
import ebag.hd.http.EBagApi
import kotlinx.android.synthetic.main.activity_report_test.*

/**
 * @author caoyu
 * @date 2018/1/24
 * @description
 */
class ReportTestActivity: BaseActivity() {
    companion object {
        fun jump(context: Context, homeworkId: String, workType: String, studentId: String = ""){
            context.startActivity(
                    Intent(context, ReportTestActivity::class.java)
                            .putExtra("homeworkId", homeworkId)
                            .putExtra("studentId", studentId)
                            .putExtra("workType", workType)
            )
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_report_test
    }

    private lateinit var homeworkId: String
    private var workType = ""
    private var studentId = ""
    override fun initViews() {
        homeworkId = intent.getStringExtra("homeworkId") ?: ""
        studentId = intent.getStringExtra("studentId") ?: ""
        workType = intent.getStringExtra("workType") ?: ""
        showData()
    }

    private fun  showData() {
        val userEntity = SerializableUtils.getSerializable<UserEntity>(Constants.STUDENT_USER_ENTITY)
        if(userEntity != null){
            titleView.setTitle(userEntity.name)
            tvName.visibility = View.VISIBLE
            tvName.text = userEntity.name
        }else{
            tvName.visibility = View.INVISIBLE
            titleView.setTitle("作业报告")
        }
        titleView.setRightText("作业详情"){
            DoHomeworkActivity.jump(this, homeworkId, Constants.REPORT_TYPE, workType, studentId)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        stateView.setOnRetryClickListener {
            getReport()
        }

        getReport()
    }

    private val adapter = Adapter()
    private val reportRequest = object : RequestCallBack<ReportBean>(){

        override fun onStart() {
            stateView.showLoading()
        }

        override fun onSuccess(entity: ReportBean?) {
            if(entity == null){
                stateView.showEmpty("暂未生成报告，请稍后重试！")
            }else{
                stateView.showContent()
                adapter.setNewData(entity.homeWorkRepDetailVos)

                scoreRound.progress = entity.totalScore
                var spannableString = SpannableString("总分\n${entity.totalScore}")
                spannableString.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.x34))
                        , 3, 3 + "${entity.totalScore}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                scoreTv.text = spannableString

                heightRound.progress = entity.maxScore
                spannableString = SpannableString("最高分\n${entity.maxScore}")
                spannableString.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.x34))
                        , 4, 4 + "${entity.maxScore}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                heightTv.text = spannableString

                errorRound.progress = entity.errorNum
                spannableString = SpannableString("错题\n${entity.errorNum}")
                spannableString.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.x34))
                        , 3, 3 + "${entity.errorNum}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                errorTv.text = spannableString
            }

        }

        override fun onError(exception: Throwable) {
            if(exception is MsgException){
                exception.handleThrowable(this@ReportTestActivity)
                stateView.showError(exception.message)
            }else{
                stateView.showError()
            }

        }

    }

    private fun getReport(){
        EBagApi.homeworkReport(homeworkId, studentId, reportRequest)
    }
    inner class Adapter: BaseQuickAdapter<ReportBean.ReportDetailBean, BaseViewHolder>(R.layout.item_activity_report_test){

        override fun convert(helper: BaseViewHolder, item: ReportBean.ReportDetailBean?) {
            helper.setText(R.id.questionType, item?.questionTypeName)
                    .setText(R.id.count, "${item?.questionNum}")
                    .setText(R.id.errorCount, "${item?.errorCount}")
                    .setBackgroundRes(R.id.layout,if(helper.adapterPosition % 2 == 0) R.color.light_blue else R.color.white)
        }

    }
}