package com.yyc.ams.network.log

import android.text.TextUtils
import android.util.Log
import me.hgj.jetpackmvvm.ext.util.jetpackMvvmLog

/**
 * @Author nike
 * @Date 2023/7/28 17:35
 * @Description
 */
object MyLogUtils {
    private const val DEFAULT_TAG = "JetpackMvvm"
    fun debugInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.e(tag, msg!!)
    }

    fun debugInfo(msg: String?) {
        debugInfo(
            DEFAULT_TAG,
            msg
        )
    }

    fun warnInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.e(tag, msg!!)
    }

    fun warnInfo(msg: String?) {
        warnInfo(
            DEFAULT_TAG,
            msg
        )
    }

    /**
     * 这里使用自己分节的方式来输出足够长度的 message
     *
     * @param tag 标签
     * @param msg 日志内容
     */
    fun debugLongInfo(tag: String?, msg: String) {
        var msg = msg
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        msg = msg.trim { it <= ' ' }
        var index = 0
        val maxLength = 3500
        var sub: String
        while (index < msg.length) {
            sub = if (msg.length <= index + maxLength) {
                msg.substring(index)
            } else {
                msg.substring(index, index + maxLength)
            }
            index += maxLength
            Log.e(tag, sub.trim { it <= ' ' })
        }
    }

    fun debugLongInfo(msg: String) {
        debugLongInfo(
            DEFAULT_TAG,
            msg
        )
    }

    private const val MAX_LOG_LENGTH = 10000 // 最大日志长度

    fun e(tag: String, message: String) {
        if (message.length <= MAX_LOG_LENGTH) {
            Log.e(tag, message)
        } else {
            var startIndex = 0
            while (startIndex < message.length) {
                val endIndex = startIndex + MAX_LOG_LENGTH
                val chunk = message.substring(startIndex, endIndex.coerceAtMost(message.length))
                startIndex = endIndex
                Log.e(tag, chunk)
            }
        }
    }

}