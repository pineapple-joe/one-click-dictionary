package com.example.oneclickdictionary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    private fun showWordNotification(context: Context, word: Word) {
        val channelId = "word_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Word of the Day", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Word of the Day: ${word.word}")
            .setContentText(word.definition)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }

    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DictionaryDBHelper(context)
        val randomWord = dbHelper.getRandomWord()
        showWordNotification(context, randomWord)

        Log.d("AlarmReceiver", "Random word: $randomWord")
    }
}