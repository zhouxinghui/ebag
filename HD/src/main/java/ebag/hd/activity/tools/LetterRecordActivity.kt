package ebag.hd.activity.tools

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import ebag.core.http.network.RequestCallBack
import ebag.hd.R
import ebag.hd.adapter.UnitAdapter
import ebag.hd.base.BaseListTabActivity
import ebag.hd.bean.BaseClassesBean
import ebag.hd.bean.EditionBean
import ebag.hd.bean.UnitBean
import ebag.hd.http.EBagApi

/**
 * Created by unicho on 2018/3/13.
 */
class LetterRecordActivity : BaseListTabActivity<EditionBean, MultiItemEntity>() {

    companion object {
        fun jump(context: Context){
            context.startActivity(
                    Intent(context, LetterRecordActivity::class.java)
            )
        }
    }

    private val classesRequest = object : RequestCallBack<List<BaseClassesBean>>(){
        override fun onStart() {
            showLoading()
        }

        override fun onSuccess(entity: List<BaseClassesBean>?) {
            if (entity == null || entity.isEmpty()){
                showError("暂无班级信息失败")
                return
            }
            classId = entity[0].classId
            request()
        }

        override fun onError(exception: Throwable) {
            showError(exception.message.toString())
        }
    }

    private lateinit var tvMaterial: TextView
    private lateinit var classId: String
    private var subCode = "yy"
    override fun loadConfig() {
        enableNetWork(false)
        setTitleContent("学生生字默写")
        setLeftWidth(resources.getDimensionPixelSize(R.dimen.x368))

        val view = layoutInflater.inflate(R.layout.layout_read_header,null)
        tvMaterial = view.findViewById(R.id.text)
        tvMaterial.text = "这是教材名字"

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.check(R.id.rbYy)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbYw -> {// 语文
                    subCode = "yw"
                    request()
                }

                R.id.rbYy -> {// 英语
                    subCode = "yy"
                    request()
                }
            }
        }
        addLeftHeaderView(view)

        EBagApi.getMyClasses(classesRequest)
    }

    override fun requestData(requestCallBack: RequestCallBack<EditionBean>) {
        EBagApi.getUnit(classId, subCode, requestCallBack)
    }

    override fun parentToList(parent: EditionBean?): List<UnitBean>? {
        tvMaterial.text = parent?.bookVersion
        return parent?.resultBookUnitOrCatalogVos
    }

    override fun firstPageDataLoad(result: List<MultiItemEntity>) {
        super.firstPageDataLoad(result)
        if (adapter.itemCount > 0) {
            try {
                val position = (0 until adapter.itemCount).first { adapter.getItem(it) is UnitBean }
                adapter.selectSub = (adapter.getItem(position) as UnitBean).resultBookUnitOrCatalogVos[0]
                adapter.expand(position)
            }catch (e: Exception){

            }
        }
    }

    private lateinit var adapter: UnitAdapter
    override fun getLeftAdapter(): BaseQuickAdapter<MultiItemEntity, BaseViewHolder> {
        adapter = UnitAdapter()
        return adapter
    }

    override fun getLayoutManager(adapter: BaseQuickAdapter<MultiItemEntity, BaseViewHolder>): RecyclerView.LayoutManager? {
        return null
    }

    private lateinit var fragment: LetterRecordFragment
    override fun getFragment(pagerIndex: Int, adapter: BaseQuickAdapter<MultiItemEntity, BaseViewHolder>): Fragment {

        if(adapter.itemCount > 0){
            val item = adapter.getItem(0)
            if(item is UnitBean)
                fragment = LetterRecordFragment.newInstance()
            return fragment
        }
        fragment = LetterRecordFragment.newInstance()
        return fragment

    }

    override fun getViewPagerSize(adapter: BaseQuickAdapter<MultiItemEntity, BaseViewHolder>): Int {
        return 1
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val item = adapter.getItem(position)
        if(item is UnitBean) {
            if (item.isExpanded) {
                adapter.collapse(position)
            } else {
                adapter.expand(position)
            }
        }else{
            item as UnitBean.ChapterBean?
            (adapter as UnitAdapter).selectSub = item

            fragment.update(item?.unitCode)
        }
    }
}