package com.hipo.findme

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readSms()
    }

    @SuppressLint("Recycle")
    private fun readSms() {
        val cursor: Cursor? = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        val messageArray = arrayListOf<String>()
        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                val msgData: String = cursor.getString(12)

                if(msgData.contains("HELLO", ignoreCase = false)) {
                    Log.i(TAG, "Msg: $msgData")
                    messageArray.add(msgData)
                }
                // use msgData
            } while (cursor.moveToNext())

        } else { // empty box, no SMS
        }
    }
}
