package com.yzy.ebag.teacher.module.account

import ebag.mobile.module.account.BaseLoginSelectActivity

/**
 * Created by YZY on 2018/5/7.
 */
class LoginSelectActivity: BaseLoginSelectActivity() {
    override fun getBindingClass(): Class<*>? = BindingActivity::class.java
}