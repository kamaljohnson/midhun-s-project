package com.hipo.findme

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readSms()
        person_1.setOnClickListener {
            sendSMS("Hello from Find Me")
        }
        person_2.setOnClickListener {
            sendSMS("Message sending is working")
        }
        person_3.setOnClickListener {
            sendSMS("Cool")
        }
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

    private fun sendSMS(msg: String) {
        val smsManager = SmsManager.getDefault() as SmsManager
        try {
            smsManager.sendTextMessage("+91" + phone_number_text.text.toString(), "1",  msg, null, null)
        } catch (error: Exception) {
            Log.i(TAG, "msg error: $error")
        }
    }
}
