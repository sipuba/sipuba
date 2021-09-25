package com.asri.sipubav2

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var mJarak: TextView
    private lateinit var mStatus: TextView
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
                if(sD == "Awas")
                    sendNotification()
                mStatus.text = "${sD.toString().uppercase()} "
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

    private fun sendNotification(){
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.war1)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.war1)



        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.war1)
            .setContentTitle("AWAS!")
            .setContentText("Terdeteksi Ketinggian Air Melewati Batas")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Segera evakuasi diri, ketinggian air melebihi batas. BERPOTENSI KEBANJIRAN"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
}