package ebag.core.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ebag.core.R

/**
 * 图片加载工具类
 * Created by YZY on 2017/11/9.
 */

object ImageViewUtils {
    val requestOptions = RequestOptions()
}

/**
 * 加载头像
 */
fun ImageView.loadHead(url: String?){
    ImageViewUtils.requestOptions
            .placeholder(R.drawable.head_default)
            .error(R.drawable.head_default)
            .circleCrop()
            .let {
                Glide
                    .with(this)
                    .load(url)
                    .apply(it)
                    .into(this)
            }
}

/**
 * 加载图片 java中调用
 */
fun ImageView.loadImage(url: String?) {
    ImageViewUtils.requestOptions
            .placeholder(R.drawable.replace_img)
            .error(R.drawable.replace_img)
            .centerCrop()
            .let {
                Glide
                    .with(this)
                    .load(url)
                    .apply(it)
                    .into(this)
            }
}
fun ImageView.loadPhoto(url: String?) {
    ImageViewUtils.requestOptions
            .placeholder(R.drawable.replace_img)
            .error(R.drawable.replace_img)
            .fitCenter()
            .let {
                Glide
                    .with(this)
                    .load(url)
                    .apply(it)
                    .into(this)
            }
}

/**
 * 加载图片（可自定义加载中和加载失败图片）
 */
fun ImageView.loadImage(url: String?, loadImg: Int = R.drawable.replace_img, errorImg: Int = R.drawable.replace_img) {
    ImageViewUtils.requestOptions
            .placeholder(loadImg)
            .error(errorImg)
            .centerCrop()
            .let {
                Glide
                    .with(this)
                    .load(url)
                    .apply(it)
                    .into(this)
            }
}

/**
 * 加载图片为圆形图片
 */
fun ImageView.loadImageToCircle(url: String?, loadImg: Int = R.drawable.replace_round_img, errorImg: Int = R.drawable.replace_round_img) {
    ImageViewUtils.requestOptions
            .placeholder(loadImg)
            .error(errorImg)
            .circleCrop()
            .let {
                Glide
                    .with(this)
                    .load(url)
                    .apply(it)
                    .into(this)
            }
}
