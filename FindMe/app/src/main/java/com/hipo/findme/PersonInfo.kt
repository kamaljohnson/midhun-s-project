package com.hipo.findme

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_person_info.*
import java.util.*


class PersonInfo : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mainHandler: Handler
    private var messageFromPerson: String = ""
    private val readLimit = 1

    val googleMap = MutableLiveData<GoogleMap>()
    private var personLocationMarker: Marker? = null

    val personLocation = MutableLiveData<Location>()

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

        setupMap()

        googleMap.observe(this, Observer { googleMap ->
            if(googleMap != null){
                personLocation.observe(this, Observer { location ->
                    if(location != null && status.text.toString().toUpperCase(Locale.ROOT) != "INACTIVE") {
                        updatePersonLocationToMap(location, googleMap)
                    }
                })
            }
        })
    }

    private fun setupMap() {
        val mapFragment: SupportMapFragment? = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun updatePersonLocationToMap(location: Location, googleMap: GoogleMap) {
        val googleCamera: CameraPosition = CameraPosition.builder()
            .target(LatLng(location.Latitude, location.Longitude))
            .zoom(15F)
            .bearing(0F)
            .tilt(0F)
            .build()
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition (googleCamera),
            1000,
            null
        )

        personLocationMarker?.remove()

        //removing last user location marker if not null
        personLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.Latitude, location.Longitude))
        )

        personLocationMarker?.showInfoWindow()

        Log.i("Debug", "location is updated")
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
            personLocation.value = Location(msgArray[2].toDouble(), msgArray[3].toDouble())
            status.text = msgArray[4]

            if(status.text.toString().toUpperCase() == "INACTIVE") {
                offline_tag.visibility = View.VISIBLE
                loc.visibility = View.GONE
                loc_text.visibility = View.GONE

            } else {
                offline_tag.visibility = View.INVISIBLE
            }

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

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.clear()
        this.googleMap.value = googleMap
        Log.i("Debug", "here")
    }

}

class Location(
    var Longitude: Double,

    var Latitude: Double
)
