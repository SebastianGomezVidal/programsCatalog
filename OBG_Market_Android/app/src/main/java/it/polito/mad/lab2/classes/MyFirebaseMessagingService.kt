package it.polito.mad.lab2.classes

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.polito.mad.lab2.R
import it.polito.mad.lab2.activities.MainActivity
import kotlinx.coroutines.MainScope


//needed for receiving messages from firebase cloud messaging system
//Since we use notification, when the app is in background, the message is handled directly from the Operating system
//and shown in the status bar. When the app is in foreground, the onMessageReceived method handle it
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object{
        var notificationId = 0
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        //super.onMessageReceived(msg)
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Lab3Debug", "From: ${msg.from}")

        // Check if message contains a data payload.
        //Beware! I use "data" instead of "notification" because of the way in which they are handled:
        //notification  -> if app is in foreground, this method handle it;
        //              -> if the app is in background, the system will handle it automatically
        //data  -> if app is in foreground or in background, this method handle it;
        msg.data.isNotEmpty().let {
            Log.d("Lab3Debug", "Message data payload: " + msg.data)
            /*val handler = Handler(Looper.getMainLooper())
            handler.post{ Toast.makeText(applicationContext,"${msg.data["body"]}",Toast.LENGTH_LONG).show() }*/

            // Create an explicit intent for an Activity in your app
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            var builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(msg.data["title"])
                .setContentText(msg.data["body"])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
                notificationId++
            }
        }

        // Check if message contains a notification payload.
        /*msg.notification?.let {
            Log.d("Lab3Debug", "Message Notification Body: ${it.body}")
            val handler = Handler(Looper.getMainLooper())
            handler.post{ Toast.makeText(applicationContext,"${it.body}",Toast.LENGTH_SHORT).show() }
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}