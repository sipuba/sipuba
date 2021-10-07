package com.asri.sipubav2

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var mJarak: TextView
    private lateinit var mStatus: TextView
    //private lateinit var mStatusAman: TextView
    //private lateinit var mStatusWaspada: TextView
    //private lateinit var mStatusSiaga: TextView
    //private lateinit var mStatusAwas: TextView

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // createNotificationChannel()
        createNotificationChannel()

        val database = FirebaseDatabase.getInstance()

        val fStatus = database.getReference("Status")
        val fJarak = database.getReference("Ultrasonic")
        mStatus = findViewById(R.id.mStatus)
        //mStatusAman = findViewById(R.id.mStatusAman)
        //mStatusWaspada = findViewById(R.id.mStatusWaspada)
        //mStatusSiaga = findViewById(R.id.mStatusSiaga)
        //mStatusAwas = findViewById(R.id.mStatusAwas)

        mJarak = findViewById(R.id.mJarak)

//Distance
        val sJarakListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n", "DefaultLocale")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sD = dataSnapshot.value
                mJarak.text = "${sD.toString().uppercase()} CM"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        fJarak.addValueEventListener(sJarakListener)

//Status
        val sStatusListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n", "DefaultLocale")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sD = dataSnapshot.value
                mStatus.text = "${sD.toString().uppercase()} "
                if(sD == "Awas") {
                    //mStatusAman.isVisible = false
                    //mStatusWaspada.isVisible = false
                    //mStatusSiaga.isVisible = false
                    //mStatusAwas.isVisible = true
                    //mStatusAwas.text = "${sD.toString().uppercase()} "
                    sendNotification3()
                    val myDialog = DialogWindow()
                    myDialog.show(supportFragmentManager, "first_dialog")
                }else if (sD == "Siaga"){
                    sendNotification2()
//                    mStatusAman.isVisible = false
//                    mStatusWaspada.isVisible = false
//                    mStatusSiaga.isVisible = true
//                    mStatusAwas.isVisible = false
//                    mStatusSiaga.text = "${sD.toString().uppercase()} "
                }
                else if (sD == "Waspada"){
                    sendNotification1()
//                    mStatusAman.isVisible = false
//                    mStatusWaspada.isVisible = true
//                    mStatusSiaga.isVisible = false
//                    mStatusAwas.isVisible = false
//                    mStatusWaspada.text = "${sD.toString().uppercase()} "
                }else if (sD == "Aman"){
//                    mStatusAman.isVisible = true
//                    mStatusWaspada.isVisible = false
//                    mStatusSiaga.isVisible = false
//                    mStatusAwas.isVisible = false
//                    mStatusAman.text = "${sD.toString().uppercase()} "
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        fStatus.addValueEventListener(sStatusListener)

//notif

    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            val name = "Notification Tittle"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification1(){
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.waspada)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.waspada)

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.waspada)
            .setContentTitle("Waspada!")
            .setContentText("Terdeteksi Ketinggian Air Dari Jarak Sensor <=20")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Tetap tenang dan waspada terhadap ketinggian air."))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
    private fun sendNotification2(){
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.siaga)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.siaga)

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.siaga)
            .setContentTitle("Siaga!")
            .setContentText("Terdeteksi Ketinggian Air Dari Jarak Sensor <15")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Tetap tenang dan waspada terhadap ketinggian air. BERPOTENSI BANJIR"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
    private fun sendNotification3(){
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.awas)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.awas)

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.awas)
            .setContentTitle("AWAS!")
            .setContentText("Terdeteksi Ketinggian Air Melewati Batas")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Segera evakuasi diri, ketinggian air melebihi batas. POTENSI BANJIR"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.info -> {
            msgShow("Info")
            val intent = Intent(this, Info::class.java)
            this.startActivity(intent)
            true
        }
        R.id.info -> {
            msgShow("Home")
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun msgShow(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}