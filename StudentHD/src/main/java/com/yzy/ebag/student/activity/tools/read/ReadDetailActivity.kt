package com.yzy.ebag.student.activity.tools.read

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ProgressBar
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayerStandard
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yzy.ebag.student.R
import com.yzy.ebag.student.bean.ReadDetailBean
import com.yzy.ebag.student.bean.ReadOutBean
import com.yzy.ebag.student.bean.RecordHistory
import com.yzy.ebag.student.http.StudentApi
import ebag.core.base.BaseActivity
import ebag.core.http.network.MsgException
import ebag.core.http.network.RequestCallBack
import ebag.core.http.network.handleThrowable
import ebag.core.util.*
import ebag.hd.bean.response.UserEntity
import kotlinx.android.synthetic.main.activity_read_detail.*
import java.io.File


/**
 * @author caoyu
 * @date 2018/2/7
 * @description
 */
class ReadDetailActivity: BaseActivity() {

    companion object {
        fun jump(context: Context,classId: String, readBean: ReadOutBean.OralLanguageBean?){
            context.startActivity(
                    Intent(context, ReadDetailActivity::class.java)
                            .putExtra("classId", classId)
                            .putExtra("oralLanguageBean", readBean)
            )
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_read_detail
    }

    private var readBean: ReadOutBean.OralLanguageBean? = null
    private val voicePlayer by lazy {
        val voicePlayer = VoicePlayerOnline(this)

        voicePlayer.setOnPlayChangeListener(object : VoicePlayerOnline.OnPlayChangeListener{
            override fun onProgressChange(progress: Int) {
                progressBar?.progress = progress
            }
            override fun onCompletePlay() {
                playingUrl = null
                anim?.stop()
                anim?.selectDrawable(0)
                anim = null
                progressBar?.progress = 0
                progressBar = null
            }
        })
        voicePlayer
    }
    private var playingUrl: String? = null
    private var anim : AnimationDrawable? = null
    private var progressBar : ProgressBar? = null
    private lateinit var basePath: String
    private var readDetailBean: ReadDetailBean? = null
    private lateinit var userId: String
    private lateinit var classId: String

    private val uploadDialog by lazy {
        ProgressDialog(this)
    }
    override fun initViews() {
        readBean = intent.getSerializableExtra("oralLanguageBean") as ReadOutBean.OralLanguageBean?
        classId = intent.getStringExtra("classId") ?: ""
        val userEntity = SerializableUtils.getSerializable<UserEntity>(ebag.hd.base.Constants.STUDENT_USER_ENTITY)
        userId = userEntity?.uid ?: "userId"
        basePath = "${FileUtil.getRecorderPath()}/$userId/read"
        val file = File(basePath)
        if(!file.exists()){
            file.mkdirs()
        }

        if(readBean == null){
            T.show(this,"出了一些错误，请稍后再试...")
            return
        }else{
            videoPlayer.setUp(readBean!!.languageUrl
                    , JZVideoPlayerStandard.SCREEN_STATE_ON, readBean!!.fileName)
            if(readBean!!.type == "video"){
                videoPlayer.thumbImageView.loadImage(readBean!!.languageUrl)
            }else{
                videoPlayer.thumbImageView.loadImage(readBean!!.coveUrl)
            }


            titleBar.setTitle(readBean!!.fileName)
            contentRecycler.layoutManager = LinearLayoutManager(this)
            contentRecycler.adapter = adapter

            historyRecycler.layoutManager = LinearLayoutManager(this)
            historyRecycler.adapter = historyAdapter

            adapter.setOnItemClickListener { _, view, position ->
                if(recorderUtil.isRecording){
                    T.show(this,"请先结束录音")
                }else{
                    adapter.selectedPosition = position
                }
            }

            stateView.setOnRetryClickListener {
                getContent()
            }

            historyStateView.setOnRetryClickListener {
                getHistory()
            }


            adapter.setOnItemChildClickListener { _, view, position ->
                readDetailBean = adapter.getItem(position)
                val recordFile = File(readDetailBean?.localPath)
                when(view.id){
                    R.id.playerBtn -> {
                        if(recorderUtil.isRecording) {
                            T.show(this, "请先结束录音")
                        }else{
                            val urlStr = view.tag as String?
                            if(StringUtils.isEmpty(urlStr)){
                                return@setOnItemChildClickListener
                            }
                            if(urlStr != playingUrl){
                                if(anim != null) {
                                    anim!!.stop()
                                    anim!!.selectDrawable(0)
                                    progressBar?.progress = 0
                                    progressBar = null
                                }
                                anim = view.background as AnimationDrawable
                                voicePlayer.playUrl(urlStr)
                                anim?.start()
                                playingUrl = urlStr
                            }else{
                                if (voicePlayer.isPlaying && !voicePlayer.isPause){
                                    voicePlayer.pause()
                                    anim?.stop()
                                }else{
                                    voicePlayer.play()
                                    anim?.start()
                                }
                            }
                        }
                    }

                    // 录音
                    R.id.recordBtn -> {
                        // 这次点击的和上次点击的 是同一个
                        if(tempPosition == position){//
                            // 正在录， 停止录音，保存音频文件
                            if(recorderUtil.isRecording){
                                if(recorderAnim != null){
                                    recorderAnim?.stop()
                                }
                                recorderUtil.pauseRecord()
                                recorderUtil.stopPlayRecord()
                                recorderUtil.finishRecord()
                                tempPosition = -1
                                adapter.notifyItemChanged(position)
                            }
                        }else{//
                            recorderAnim = view.background as AnimationDrawable
                            // 本地不存在录音
                            if(!recordFile.exists()){
                                recorderUtil.setFinalFileName(recordFile.absolutePath)
                                recorderUtil.startRecord()
                                recorderAnim?.start()
                            }else{// 本地存在录音文件
                                dialogExists.show()
                            }
                            tempPosition = position
                        }
                    }

                    // 播放自己的音频
                    R.id.playSelf -> {
                        if(recorderUtil.isRecording) {
                            T.show(this, "请先结束录音")
                        }else {
                            // 本地不存在录音
                            if(!recordFile.exists()){
                                T.show(this,"暂未找到您的本地录音文件，请重新录制")
                                adapter.notifyItemChanged(position)
                            }else{// 本地存在录音文件
                                if(playingUrl != recordFile.absolutePath){
                                    if(anim != null) {
                                        anim!!.stop()
                                        anim!!.selectDrawable(0)
                                        progressBar?.progress = 0
                                        progressBar = null
                                    }
                                    voicePlayer.playUrl(recordFile.absolutePath)
                                    playingUrl = recordFile.absolutePath
                                }else{

                                    if (voicePlayer.isPlaying && !voicePlayer.isPause){
                                        voicePlayer.pause()
                                        anim?.stop()
                                    }else{
                                        voicePlayer.play()
                                        anim?.start()
                                    }
                                }
                            }
                        }
                    }

                    // 提交录音
                    R.id.submit -> {
                        if(recorderUtil.isRecording) {
                            T.show(this, "请先结束录音")
                        }else {
                            // 服务器存在录音
                            if(readDetailBean?.checkLanguage == "Y"){
                                dialogNetExist.show()
                            }else{
                                upload()
                            }

                        }
                    }
                }
            }

            historyAdapter.setOnItemChildClickListener { _, view, position ->
                if(view.id == R.id.play_id){
                    val recordHistory = historyAdapter.getItem(position)

                    if(recordHistory?.myAudioUrl == null){
                        T.show(this,"对不起文件丢失，请重新录制上传")
                    }else{
                        if(playingUrl != recordHistory.myAudioUrl){
                            if(anim != null) {
                                anim!!.stop()
                                anim!!.selectDrawable(0)
                                progressBar?.progress = 0
                                progressBar = null
                            }
                            anim = (view.getTag(R.id.image_id) as View).background as AnimationDrawable
                            progressBar = view.getTag(R.id.progress_id) as ProgressBar
                            anim?.start()
                            progressBar?.progress = 0
                            voicePlayer.playUrl(recordHistory.myAudioUrl)
                            playingUrl = recordHistory.myAudioUrl
                        }else{
                            if (voicePlayer.isPlaying && !voicePlayer.isPause){
                                voicePlayer.pause()
                                anim?.stop()
                            }else{
                                voicePlayer.play()
                                anim?.start()
                            }
                        }
                    }
                }
            }

            getHistory()
            getContent()
        }


    }

    /**
     * 上传文件
     */
    private fun upload(){

        val recordFile = File(readDetailBean?.localPath)
        // 本地不存在录音
        if(!recordFile.exists()){
            T.show(this,"暂未找到您的本地录音文件，请重新录制")
            adapter.notifyItemChanged(readDetailBean?.position ?: 0)
        }else{// 本地存在录音文件
            uploadFile()
        }
    }

    private val adapter = Adapter()
    private var request = object :RequestCallBack<List<ReadDetailBean>>(){
        override fun onStart() {
            stateView.showLoading()
        }
        override fun onSuccess(entity: List<ReadDetailBean>?) {
            if(entity == null || entity.isEmpty())
                stateView.showEmpty()
            else
                stateView.showContent()
            adapter.setNewData(entity)

        }

        override fun onError(exception: Throwable) {
            stateView.showError()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(voicePlayer.isPlaying){
            voicePlayer.stop()
        }

        if(recorderUtil.isRecording){
            recorderUtil.stopPlayRecord()
            recorderUtil.finishRecord()
        }
        request.cancelRequest()
    }



    override fun onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        if(recorderUtil.isRecording){
            recorderUtil.pauseRecord()
        }
        JZVideoPlayer.releaseAllVideos()
    }

    private var recorderAnim: AnimationDrawable? = null
    private val recorderUtil by lazy {
        RecorderUtil(this)
    }
    private var tempPosition: Int = -1

    /**
     * 本地存在录音的提示
     */
    private val dialogExists by lazy {
        AlertDialog.Builder(this)
                .setMessage("录音已存在，是否重新录制？")
                .setCancelable(false)
                .setNegativeButton("取消", { dialog, _ ->
                    dialog.dismiss()
                })
                .setPositiveButton("重新录音", {_, _ ->
                    recorderUtil.setFinalFileName(readDetailBean?.localPath)
                    recorderUtil.startRecord()
                    recorderAnim!!.start()
                })
                .create()
    }

    /**
     * 服务器存在 当前句子录音的提示
     */
    private val dialogNetExist by lazy {
        AlertDialog.Builder(this)
                .setMessage("服务器已存在录音，是否覆盖？")
                .setCancelable(false)
                .setNegativeButton("取消", { dialog, _ ->
                    dialog.dismiss()
                })
                .setPositiveButton("覆盖", {_, _ ->
                    upload()
                })
                .create()
    }

    /**
     * 右侧的adapter
     */
    inner class Adapter: BaseQuickAdapter<ReadDetailBean, BaseViewHolder>(R.layout.item_activity_read_load){

        private var oldPosition = -1
        var selectedPosition = -1
        set(value) {
            if(field != value){
                field = value
                if(oldPosition != -1)
                    notifyItemChanged(oldPosition)
                notifyItemChanged(field)
                oldPosition = value
            }
        }

        override fun convert(helper: BaseViewHolder, item: ReadDetailBean?) {
            val file = File(basePath, "${item?.languageDetailId}.amr")
            item?.localPath = file.absolutePath
            item?.position = helper.adapterPosition
            val isSelected = selectedPosition == helper.adapterPosition
            helper.setText(R.id.tvFirst, item?.languageEn)
                    .setText(R.id.tvSecond, item?.languageCn)
                    .setGone(R.id.playerBtn, isSelected)
                    .setGone(R.id.recordBtn, isSelected)
                    .setGone(R.id.playSelf, file.exists() && isSelected)
                    .setGone(R.id.submit, file.exists() && isSelected)
                    .setTag(R.id.playerBtn, item?.languageUrl)
                    .addOnClickListener(R.id.playerBtn)
                    .addOnClickListener(R.id.recordBtn)
                    .addOnClickListener(R.id.playSelf)
                    .addOnClickListener(R.id.submit)
                    .getView<View>(R.id.layout).isSelected = isSelected
        }

    }

    private fun getContent(){
        StudentApi.getReadDetailList(readBean?.id ?: "", request)
    }
    private fun getHistory(){
        StudentApi.recordHistory(readBean?.id ?: "", historyRequest)
    }

    inner class HistoryAdapter: BaseQuickAdapter<RecordHistory, BaseViewHolder>(R.layout.item_activity_record_history){
        override fun convert(helper: BaseViewHolder, item: RecordHistory?) {
            helper.setText(R.id.tvContent, item?.languageEn)
                    .addOnClickListener(R.id.play_id)
                    .setTag(R.id.play_id, R.id.image_id, helper.getView(R.id.image_id))
                    .setTag(R.id.play_id, R.id.progress_id, helper.getView(R.id.progress_id))
        }
    }


//    private lateinit var systemTime: String
    /**
     * 上传阿里云，如果多条历史记录 用systemTime 上传，单挑用ID上传
     */
    private fun uploadFile(){
        if(classId.isEmpty()){
            T.show(this,"现在不能上传您的录音，请返回首页获取，当前班级")
            return
        }

//        systemTime = System.currentTimeMillis().toString()
        //上传录音
        OSSUploadUtils.getInstance().uploadFileToOss(
                this,
                File(readDetailBean?.localPath),
                "personal/$userId/read",
                "${readDetailBean?.languageDetailId}.amr",
                mHandler)
        uploadDialog.setMessage("正在上传录音...")
        uploadDialog.setCanceledOnTouchOutside(false)
        uploadDialog.show()

    }

    /**
     * 阿里云 上传文件
     */
    private val mHandler = MyHandler(this)
    class MyHandler(activity: ReadDetailActivity): HandlerUtil<ReadDetailActivity>(activity){
        override fun handleMessage(activity: ReadDetailActivity?, msg: Message?) {
            when {
                msg!!.what == Constants.UPLOAD_SUCCESS ->{//上传文件成功

                    val url = "${Constants.OSS_BASE_URL}/personal/${activity!!.userId}/read/${activity.readDetailBean?.languageDetailId}.amr"

                    StudentApi.uploadRecord(activity.classId,activity.readDetailBean?.languageId ?: "", activity.readDetailBean?.languageDetailId ?: ""
                            ,url , activity.uploadRequest)
                }
                msg.what == Constants.UPLOAD_FAIL ->{//上传文件失败
                    activity!!.uploadDialog.dismiss()
                    T.show(activity, "录音上传失败，请重试")
                }
            }
        }
    }

    /**
     * 上传录音url
     */
    private val uploadRequest = object: RequestCallBack<String>(){

        override fun onSuccess(entity: String?) {
            uploadDialog.dismiss()
            getHistory()
            T.show(this@ReadDetailActivity, entity ?: "我的录音上传成功")
        }

        override fun onError(exception: Throwable) {
            uploadDialog.dismiss()
            if(exception is MsgException){
                T.show(this@ReadDetailActivity, exception.message ?: "录音上传失败，请重试")
            }else{
                T.show(this@ReadDetailActivity, "录音上传失败，请重试")
            }
        }
    }

    private val historyAdapter = HistoryAdapter()
    private val historyRequest = object :RequestCallBack<List<RecordHistory>>(){

        override fun onStart() {
            emptyLayout.visibility = View.VISIBLE
            historyStateView.showLoading()
        }

        override fun onSuccess(entity: List<RecordHistory>?) {
            historyAdapter.setNewData(entity)
            if(entity != null && entity.isNotEmpty()){
                emptyLayout.visibility = View.GONE
                historyStateView.showContent()
            }else{
                emptyLayout.visibility = View.VISIBLE
                historyStateView.showEmpty()
            }
        }

        override fun onError(exception: Throwable) {
            emptyLayout.visibility = View.VISIBLE
            if(exception is MsgException){
                historyStateView.showError(exception.message)
            }else{
                historyStateView.showError()
            }
            exception.handleThrowable(this@ReadDetailActivity)
        }

    }
}