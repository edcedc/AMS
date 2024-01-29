package com.yyc.ams.ui.act

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.yyc.ams.R
import com.yyc.ams.databinding.AZxingBinding
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.yyc.ams.base.BaseActivity
import com.yyc.ams.bean.DataBean
import com.yyc.ams.mar.eventViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * @Author nike
 * @Date 2023/6/19 17:35
 * @Description 二维码
 */
class ZxingAct  : BaseActivity<BaseViewModel, AZxingBinding>(), OnClickListener {

    /**
     * 扫描跳转Activity RequestCode
     */
    val REQUEST_CODE = 111

    /**
     * 选择系统图片Request Code
     */
    val REQUEST_IMAGE = 112

    var isOpen = false

    var type: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (bundle != null){
            type = bundle.getInt("type")
        }

        mDatabind.ivOpen.setOnClickListener(this)
        mDatabind.ivPhoto.setOnClickListener(this)

        val captureFragment = CaptureFragment()
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.f_zxing);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_open ->{
                if (!isOpen) {
                    mDatabind.ivOpen.background = ContextCompat.getDrawable(this, R.mipmap.dl_loc)
                    CodeUtils.isLightEnable(true);
                } else {
                    mDatabind.ivOpen.background = ContextCompat.getDrawable(this, R.mipmap.dl_filters)
                    CodeUtils.isLightEnable(false);
                }
                isOpen = !isOpen
            }
            R.id.iv_photo ->{
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE)
            }
        }
    }

    /**
     * 二维码解析回调函数
     */
    var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
            bundle.putString(CodeUtils.RESULT_STRING, result)
            resultIntent.putExtras(bundle)
            LogUtils.e("onAnalyzeSuccess；" + result)
            eventViewModel.zkingType.value = DataBean(type = type, text = result)
            finish()
        }

        override fun onAnalyzeFailed() {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED)
            bundle.putString(CodeUtils.RESULT_STRING, "")
            resultIntent.putExtras(bundle)
            LogUtils.e("onAnalyzeFailed")
//            this@SecondActivity.setResult(RESULT_OK, resultIntent)
//            this@SecondActivity.finish()
        }
    }

}