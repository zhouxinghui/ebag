package com.yzy.ebag.parents.ui.widget

import android.content.Context
import com.yzy.ebag.parents.R
import ebag.core.base.BaseDialog
import ebag.core.util.EditTextLimitUtil
import ebag.core.util.StringUtils
import ebag.core.util.T
import kotlinx.android.synthetic.main.dialog_modify_info.*

/**
 * Created by YZY on 2018/2/24.
 */
class ModifyInfoDialog(context: Context): BaseDialog(context) {
    override fun getLayoutRes(): Int {
        return R.layout.dialog_modify_info
    }
    override fun setWidth(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.x200)
    }
    override fun setHeight(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.y300)
    }

    init {
        EditTextLimitUtil.setEditMaxLength(countEdit, 40)
    }

    var onConfirmClickListener: ((count: String) -> Unit)? = null
    init {
        confirmBtn.setOnClickListener {
            val content = countEdit.text.toString()
            if (StringUtils.isEmpty(content)){
                T.show(context, "输入内容不能为空")
                return@setOnClickListener
            }

            onConfirmClickListener?.invoke(content)
        }
    }

    fun show(hint: String) {
        countEdit.setText("")
        countEdit.hint = hint
        super.show()
    }
}