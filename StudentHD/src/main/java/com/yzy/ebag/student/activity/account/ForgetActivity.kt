package com.yzy.ebag.student.activity.account

import android.content.Intent
import ebag.hd.base.Constants
import ebag.hd.ui.activity.account.BForgetActivity

/**
 * Created by YZY on 2017/12/20.
 */
class ForgetActivity : BForgetActivity(){

    override fun resetSuccess() {
        startActivity(

                Intent(this, LoginActivity::class.java)
                        .putExtra(Constants.KEY_TO_MAIN, true)
        )
    }

}