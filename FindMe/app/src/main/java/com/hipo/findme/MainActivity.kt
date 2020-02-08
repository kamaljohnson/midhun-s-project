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

        person_1.setOnClickListener {
            proceedToPersonInfo("8921816527")
        }

        person_2.setOnClickListener {
            proceedToPersonInfo("7012162235")
        }

        person_3.setOnClickListener {
            proceedToPersonInfo("7593809715")
        }
    }

    private fun proceedToPersonInfo(phoneNumber:String) {
        val intent = Intent(this, PersonInfo::class.java)
        intent.putExtra("PhoneNumber", phoneNumber)
        startActivity(intent)
    }
}
