package ebag.hd.http
import ebag.core.http.network.RequestCallBack
import ebag.core.http.network.RequestSubscriber
import ebag.hd.bean.response.CodeEntity
import ebag.hd.bean.response.UserEntity
import ebag.hd.http.baseBean.RequestBean
import ebag.hd.http.baseBean.ResponseBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by unicho on 2017/11/9.
 */
object EBagApi {


    /**返回的数据格式是按照我们自己定义的数据格式时调用此方法*/
    private fun <T> request(ob: Observable<ResponseBean<T>>, callback: RequestCallBack<T>){
        ob.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .map(ShelledFunction())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(RequestSubscriber(callback))
    }

    /**返回的数据格式是按照我们自己定义的数据格式时调用此方法*/
    private fun <T> startRequest(ob: Observable<T>, callback: RequestCallBack<T>){
        ob.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(RequestSubscriber(callback))
    }

    private fun <T> getRequestBean(body: T): RequestBean<T> {
        val request = RequestBean<T>()
        request.setBody(body)
        return request
    }

    /**
     * 登录
     */
    fun login(account: String, pwd: String, callback: RequestCallBack<UserEntity>){

    }

    /**
     * 注册
     */
    fun register(name: String, phone: String, code: String, pwd: String, callback: RequestCallBack<UserEntity>){
    }

    /**
     * 获取验证码
     */
    fun getCode(phone: String, callback: RequestCallBack<CodeEntity>){

    }

}