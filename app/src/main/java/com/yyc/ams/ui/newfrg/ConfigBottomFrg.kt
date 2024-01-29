package com.yyc.ams.ui.newfrg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.StringUtils
import com.flyco.roundview.RoundTextView
import com.yyc.ams.R
import com.yyc.ams.base.BaseFullBottomSheetFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.ext.showToast
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.viewmodel.RfidModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale


/**
 * User: Nike
 *  2023/12/19 16:14
 */
class ConfigBottomFrg() : BaseFullBottomSheetFragment(), OnClickListener {

    val sp_session_data = arrayOf("S0", "S1", "S2", "S3")
    val sp_flag_data = arrayOf("A", "B", "AB")
    val sp_language_data = arrayOf("中文", "繁体", "English")

    var powerSelected: Int = 0
    var sessionSelected: Int = 0
    var flagSelected: Int = 0
    var languageSelected: Int = 0

    lateinit var etRssi: AppCompatEditText
    lateinit var etEaf: AppCompatEditText
    lateinit var etRfidCode: AppCompatEditText
    lateinit var etRecipient: AppCompatEditText
    lateinit var etStrength: AppCompatEditText

    val configDao = AppRoomDataBase.get().getConfigDao()

    val rfidModel: RfidModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.b_cofig, container, false)
        rootView.findViewById<ImageButton>(R.id.iv_cancel).setOnClickListener(this)
        rootView.findViewById<RoundTextView>(R.id.btn_commit).setOnClickListener(this)
        val spPower = rootView.findViewById<Spinner>(R.id.sp_power)
        var sp_power_data = ArrayList<Int>()
        for (i in 0..32) {
            sp_power_data.add(i)
        }
        val powerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sp_power_data)
        spPower.adapter = powerAdapter
        spPower.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                // 当选择项发生变化时触发的逻辑
                powerSelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 当没有选择项时触发的逻辑
            }
        }

        val sp_session_data = arrayOf(getString(R.string.single_label), getString(R.string.double_label))
        val spSession = rootView.findViewById<Spinner>(R.id.sp_session)
        val sessionAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sp_session_data)
        spSession.adapter = sessionAdapter
        spSession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                // 当选择项发生变化时触发的逻辑
                sessionSelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 当没有选择项时触发的逻辑
            }
        }

        /*val spFlag = rootView.findViewById<Spinner>(R.id.sp_flag)
        val flagAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sp_flag_data)
        spFlag.adapter = flagAdapter
        spFlag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                // 当选择项发生变化时触发的逻辑
                flagSelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 当没有选择项时触发的逻辑
            }
        }*/

        val spLanguage = rootView.findViewById<Spinner>(R.id.sp_language)
        val languageAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sp_language_data)
        spLanguage.adapter = languageAdapter
        spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 当选择项发生变化时触发的逻辑
                languageSelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 当没有选择项时触发的逻辑
            }
        }

        etRssi = rootView.findViewById(R.id.et_rssi)
        etEaf = rootView.findViewById(R.id.et_eaf)
        etRfidCode = rootView.findViewById(R.id.et_rfid_code)
        etRecipient = rootView.findViewById(R.id.et_recipient)
        etStrength = rootView.findViewById(R.id.et_strength)

        Flowable.fromCallable {
            var bean = configDao.findFirst()
            bean
        }.subscribeOn(Schedulers.io()) //给上面分配了异步线程
            .observeOn(AndroidSchedulers.mainThread()) //给下面分配了UI线程
            .subscribe({ bean ->
                spPower.setSelection(bean.power)
                spSession.setSelection(bean.session)
//                spFlag.setSelection(bean.flag)
                etRssi.setText("${bean.rssi}")
                etEaf.setText("${bean.eaf}")
                etRfidCode.setText("${bean.rfidCode}")
                etRecipient.setText("${bean.recipient}")
                etStrength.setText("${bean.strength}")
                spLanguage.setSelection(CacheUtil.getLanguage())
            }, { error ->
                // 处理错误
                LogUtils.e(error)
            })

        return rootView
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_cancel -> {
                dismiss()
            }

            R.id.btn_commit -> {
                Flowable.fromCallable {
                    var bean = configDao.findFirst()
                    bean.power = powerSelected
                    bean.session = sessionSelected
//                    bean.flag = flagSelected
                    bean.eaf = etEaf.text.toString().toDouble()
                    bean.rssi = etRssi.text.toString().toInt()
                    bean.rfidCode = etRfidCode.text.toString()
                    bean.recipient = etRecipient.text.toString()
                    bean.strength = etStrength.text.toString().toInt()
                    if (!StringUtils.isEmpty(bean.recipient) && !RegexUtils.isEmail(bean.recipient)){
                        showToast(getString(R.string.email_format_error))
                        return@fromCallable
                    }
                    configDao.add(bean)
                    bean
                }.subscribeOn(Schedulers.io()) //给上面分配了异步线程
                    .observeOn(AndroidSchedulers.mainThread()) // 切换到主线程
                    .subscribe({ bean ->
                        if (languageSelected != CacheUtil.getLanguage()){
                            rfidModel.language.value = languageSelected
                        }


//                        showToast("save successful")
                        dismiss()
                    }, { error ->
                        // 处理错误
                        LogUtils.e(error)
                    })
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        rfidModel.language.value = -1
    }

}


