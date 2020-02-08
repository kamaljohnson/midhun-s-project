package com.hipo.findme

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_person_info.*


class PersonInfo : AppCompatActivity() {

    lateinit var mainHandler: Handler
    private var messageFromPerson: String = ""
    private val readLimit = 1

    private val updateTextTask = object : Runnable {
        override fun run() {
            val messageReceived= readSms()
            if(messageReceived) {
                return
            }
            mainHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        getPersonInfo(intent.getStringExtra("PhoneNumber")!!)
        mainHandler = Handler(Looper.getMainLooper())
        updateTextTask.run()
    }

    private fun getPersonInfo(phoneNo: String) {
        sendSMS("HELLO FROM FIND ME", phoneNo)
    }

    @SuppressLint("Recycle")
    private fun readSms(): Boolean {
        val cursor: Cursor? = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        var count = 0
        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                count += 1
                val msgData: String = cursor.getString(12)
                if(msgData.contains("FINDME", ignoreCase = false)) {
                    Log.i(TAG, "Msg: $msgData")
                    messageFromPerson = msgData.replace("FINDME ", "")
                    extractDataFromMsg(messageFromPerson)
                    return true
                }
                // use msgData
            } while (cursor.moveToNext() && count < readLimit)
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun extractDataFromMsg(msg: String) {
        try {
            val msgArray = msg.split(" ")
            Log.i("Debug", msgArray.toString())
            name.text = msgArray[0]
            dept.text = msgArray[1]
            loc.text = msgArray[2] + ", " + msgArray[3]
            status.text = msgArray[4]
        } catch (e:java.lang.Exception) {
            Toast.makeText(this, "Message format wrong", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendSMS(msg: String, phoneNumber:String) {
        val smsManager = SmsManager.getDefault() as SmsManager
        try {
            smsManager.sendTextMessage("+91$phoneNumber", "1",  msg, null, null)
        } catch (error: Exception) {
            Log.i(TAG, "msg error: $error")
        }
    }

}