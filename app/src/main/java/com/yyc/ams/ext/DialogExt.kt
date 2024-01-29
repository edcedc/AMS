package com.yyc.ams.ext

import android.animation.FloatEvaluator
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.yyc.ams.R
import me.hgj.jetpackmvvm.base.appContext

/**
 * User: Nike
 *  2023/12/18 10:47
 */

fun showEditDialog(
    context: FragmentActivity
): XPopup.Builder {
    return XPopup.Builder(context)
        //设置是否给StatusBar添加阴影，目前对Drawer弹窗和全屏弹窗生效生效。
        .hasStatusBarShadow(false)
        //设置是否显示导航栏，默认是显示的。如果你的APP主动隐藏了导航栏，你需要设置为false，不然看起来 会有点不和谐；默认情况下不需要调用
        .hasNavigationBar(true)
        //是否在弹窗消失后就立即释放资源，杜绝内存泄漏，仅仅适用于弹窗只用一次的场景，默认为false。 如果你的弹窗对象需是复用的，千万不要开启这个设置
        .isDestroyOnDismiss(false) //对于只使用一次的弹窗对象，推荐设置这个
        //是否自动打开输入法，当弹窗包含输入框时很有用，默认为false
        .autoOpenSoftInput(true)
        //是否使用暗色主题，默认是false。对所有内置弹窗生效
        .isDarkTheme(false)
}

fun showDialog(): XPopup.Builder {
    return XPopup.Builder(appContext)
        //设置是否给StatusBar添加阴影，目前对Drawer弹窗和全屏弹窗生效生效。
        .hasStatusBarShadow(false)
        //设置是否显示导航栏，默认是显示的。如果你的APP主动隐藏了导航栏，你需要设置为false，不然看起来 会有点不和谐；默认情况下不需要调用
        .hasNavigationBar(false)
        //是否在弹窗消失后就立即释放资源，杜绝内存泄漏，仅仅适用于弹窗只用一次的场景，默认为false。 如果你的弹窗对象需是复用的，千万不要开启这个设置
        .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
        //是否使用暗色主题，默认是false。对所有内置弹窗生效
        .isDarkTheme(true)
}

internal class DemoXPopupListener : SimpleCallback() {
    var fEvaluator = FloatEvaluator()
    var iEvaluator = FloatEvaluator()
    override fun onCreated(pv: BasePopupView) {
        Log.e("tag", "onCreated")
    }

    override fun onShow(popupView: BasePopupView) {
        Log.e("tag", "onShow")
    }

    override fun onDismiss(popupView: BasePopupView) {
        Log.e("tag", "onDismiss")
    }

    override fun beforeDismiss(popupView: BasePopupView) {
        Log.e("tag", "beforeDismiss")
    }

    //如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
    override fun onBackPressed(popupView: BasePopupView): Boolean {
        Log.e("tag", "拦截的返回按键，按返回键XPopup不会关闭了")
        Toast.makeText(
            popupView.context,
            "onBackPressed返回true，拦截了返回按键，按返回键XPopup不会关闭了",
            Toast.LENGTH_SHORT
        ).show()
        return true
    }

    override fun onDrag(popupView: BasePopupView, value: Int, percent: Float, upOrLeft: Boolean) {
        super.onDrag(popupView, value, percent, upOrLeft)
        //            Log.e("tag", "value: " + value + "  percent: " + percent);
//            ((Activity) popupView.getContext()).getWindow().getDecorView().setTranslationX(value);
//            float e = fEvaluator.evaluate(percent, 1.0, 0.8);
//            View decorView = ((Activity) popupView.getContext()).getWindow().getDecorView();
//            decorView.setScaleX(e);
//            decorView.setScaleY(e);
//            FloatEvaluator iEvaluator = new FloatEvaluator();
//            View decorView = ((Activity) popupView.getContext()).getWindow().getDecorView();
//            float t = iEvaluator.evaluate(percent, 0, -popupView.getMeasuredWidth()/2);
//            decorView.setTranslationX(t);
    }

    override fun onKeyBoardStateChanged(popupView: BasePopupView, height: Int) {
        super.onKeyBoardStateChanged(popupView, height)
        Log.e("tag", "onKeyBoardStateChanged height: $height")
    }

    fun onClickOutside(popupView: BasePopupView?) {
        Log.e("tag", "onClickOutside")
    }
}