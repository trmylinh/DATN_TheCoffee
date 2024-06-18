package com.example.thecoffee.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.thecoffee.MainActivity
import com.example.thecoffee.MyApplication.Companion.CHANNEL_ID
import com.example.thecoffee.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Date

class MyFirebaseMessagingService: FirebaseMessagingService()  {
    // generate the notification
    // attach the notification created with the custom layout
    // show the notification

    // ncl de nhan tbao thi van set token cua admin - token cua user =)) duma -> lam sau
    // lam spham yeu thich z
    companion object{
        var FCM_TOKEN = "" // dien token device cho admin va user
    }

    override fun onMessageReceived(reomoteMessage: RemoteMessage) {
        super.onMessageReceived(reomoteMessage)
//        val notification: RemoteMessage.Notification? = reomoteMessage.notification
//        if(notification != null){
//            val title = notification.title
//            val message = notification.body
//            sendNotification(title!!, message!!)
//        }

        // data message
        val title = reomoteMessage.data["title"]
        val message = reomoteMessage.data["body"]
        if(title != null && message != null){
            sendNotification(title, message)
        }
    }

    private fun sendNotification(title: String, message: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // pending intent de mo app khi nhap vao thong bao
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_splash)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContent(getRemoteView(title, message))

        val notification = notificationBuilder.build()

        val notificationManager  = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(getNotificationId(), notification)
    }

    private fun getNotificationId(): Int {
        return Date().time.toInt()
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.example.thecoffee", R.layout.layout_notification)

        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.app_logo, R.drawable.logo_splash)

        return  remoteViews
    }

    override fun onNewToken(token: String) {
        // token -> server se xac nhan xem device minh muon push noti la thang nao
        super.onNewToken(token)
        Log.d("token", "--Refreshed token: $token")
        FCM_TOKEN = token
    }
}