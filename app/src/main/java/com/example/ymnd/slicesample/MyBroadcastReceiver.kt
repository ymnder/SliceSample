package com.example.ymnd.slicesample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_INCREMENT, false)) {
            currentCount++
            context.contentResolver.notifyChange(sliceUri, null)
        } else {
            intent.getStringExtra(EXTRA_DEBUG_TOAST)?.let {
                Toast.makeText(context, "tap on $it", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(context, "This Slice has no extra increment flag", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        var currentCount = 0
        val sliceUri = Uri.parse("content://com.example.ymnd.slicesample/increment")
        const val EXTRA_INCREMENT = "increment_flag"
        const val EXTRA_DEBUG_TOAST = "debug_toast_flag"
    }
}