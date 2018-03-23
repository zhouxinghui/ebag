package com.yzy.ebag.teacher.activity.prepare

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yzy.ebag.teacher.R
import com.yzy.ebag.teacher.bean.PrepareFileBean
import com.yzy.ebag.teacher.http.TeacherApi
import ebag.core.base.BaseListFragment
import ebag.core.http.file.DownLoadObserver
import ebag.core.http.file.DownloadInfo
import ebag.core.http.file.DownloadManager
import ebag.core.http.network.RequestCallBack
import ebag.core.http.network.handleThrowable
import ebag.core.util.FileUtil
import ebag.core.util.LoadingDialogUtil
import ebag.core.util.T
import ebag.core.util.loadImage
import ebag.hd.activity.DisplayOfficeFileActivity

/**
 * Created by YZY on 2018/3/2.
 */
class PrepareFragment: BaseListFragment<List<PrepareFileBean>, PrepareFileBean>(), BaseQuickAdapter.OnItemLongClickListener{
    companion object {
        fun newInstance(list: List<PrepareFileBean>?): PrepareFragment {
            val fragment = PrepareFragment()
            val bundle = Bundle()
            bundle.putSerializable("list", list as ArrayList)
            fragment.arguments = bundle
            return fragment
        }
    }
    private val deleteRequest by lazy {
        object : RequestCallBack<String>() {
            override fun onStart() {
                deleteFileDialog.dismiss()
                LoadingDialogUtil.showLoading(mContext, "正在删除...")
            }

            override fun onSuccess(entity: String?) {
                LoadingDialogUtil.closeLoadingDialog()
                T.show(mContext, "删除成功")
                onRetryClick()
            }

            override fun onError(exception: Throwable) {
                LoadingDialogUtil.closeLoadingDialog()
                exception.handleThrowable(mContext)
            }
        }
    }
    private lateinit var list: List<PrepareFileBean>
    private var gradeCode: String = ""
    private var subCode: String = ""
    private var unitId: String = ""
    private var fileId = ""
    private val deleteFileDialog by lazy {
        val dialog = AlertDialog.Builder(mContext)
                .setMessage("删除此文件？")
                .setPositiveButton("删除", { dialog, which ->
                    deleteFile()
                })
                .setNegativeButton("取消", { dialog, which ->
                    dialog.dismiss()
                }).create()
        dialog
    }
    override fun getBundle(bundle: Bundle?) {
        try {
            list = bundle?.getSerializable("list") as ArrayList<PrepareFileBean>
        }catch (e: Exception){

        }
    }

    private fun deleteFile(){
        TeacherApi.deletePrepareFile(fileId, deleteRequest)
    }

    override fun loadConfig() {
        withFirstPageData(list, true)
    }

    fun notifyRequest(gradeCode: String, subCode: String, unitId: String){
        this.gradeCode = gradeCode
        this.subCode = subCode
        this.unitId = unitId
        onRetryClick()
    }

    override fun getPageSize(): Int {
        return 10
    }
    override fun requestData(page: Int, requestCallBack: RequestCallBack<List<PrepareFileBean>>) {
        TeacherApi.prepareList(gradeCode, subCode, unitId, page, getPageSize(), requestCallBack)
    }

    override fun parentToList(isFirstPage: Boolean, parent: List<PrepareFileBean>?): List<PrepareFileBean>? {
        return parent
    }

    override fun getAdapter(): BaseQuickAdapter<PrepareFileBean, BaseViewHolder> {
        val adapter = MyAdapter()
        adapter.onItemLongClickListener = this
        return adapter
    }

    override fun getLayoutManager(adapter: BaseQuickAdapter<PrepareFileBean, BaseViewHolder>): RecyclerView.LayoutManager? {
        return GridLayoutManager(mContext, 4)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter as MyAdapter
        val url = adapter.data[position].fileUrl
        val filePath = "${FileUtil.getPrepareFilePath()}${url.substring(url.lastIndexOf("/") + 1, url.length)}"
        val isFileExit = FileUtil.isFileExists(filePath)
        if (isFileExit){
            DisplayOfficeFileActivity.jump(mContext, filePath)
        }else{
            DownloadManager.getInstance().download(url, FileUtil.getPrepareFilePath(), object : DownLoadObserver(){
                override fun onNext(downloadInfo: DownloadInfo) {
                    super.onNext(downloadInfo)
                    LoadingDialogUtil.showLoading(mContext, "正在下载...${downloadInfo.progress * 100 / downloadInfo.total}%")
                }

                override fun onComplete() {
                    LoadingDialogUtil.closeLoadingDialog()
                    DisplayOfficeFileActivity.jump(mContext, filePath)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    LoadingDialogUtil.closeLoadingDialog()
                    T.show(mContext, "文件下载失败，请稍后重试")
                    DownloadManager.getInstance().cancel(url)
                }
            })
        }

    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        adapter as MyAdapter
        fileId = adapter.data[position].id
        deleteFileDialog.show()
        return true
    }

    inner class MyAdapter: BaseQuickAdapter<PrepareFileBean, BaseViewHolder>(R.layout.item_prepare){
        override fun convert(helper: BaseViewHolder, item: PrepareFileBean) {
            val imageView = helper.getView<ImageView>(R.id.fileImg)
            imageView.loadImage(item.fileUrl)
            helper.setText(R.id.fileName, item.fileName)
        }
    }
}