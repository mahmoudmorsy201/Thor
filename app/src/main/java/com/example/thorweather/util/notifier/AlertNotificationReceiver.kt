package com.example.thorweather.util.notifier

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.thorweather.NotificationCenter
import com.example.thorweather.R
import com.example.thorweather.activity.MainActivity
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import com.example.thorweather.util.SharedPreferencesFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlertNotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: RepositoryInterface
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d("AlertNotificationReceiver", "onReceive: Done")
        var weatherDto: WeatherResponseDto
        val NOTIFICATION_ID = Random(System.currentTimeMillis()).nextInt(120)
        val alarmsound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.applicationContext.packageName + "/" + R.raw.messagetone)
        val intent1 = Intent(context, MainActivity::class.java)
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addParentStack(MainActivity::class.java)
        taskStackBuilder.addNextIntent(intent1)
        val intent2: PendingIntent =
            taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)

        val sharedPreferencesFactory = SharedPreferencesFactory(context)

        CoroutineScope(Dispatchers.IO).launch {
            weatherDto = repository.getWeatherForAlert()
            Log.d("TAG", "onReceive:Weather ${weatherDto.timezone} ")

            if (sharedPreferencesFactory.getNotificationEnabledOrDisabled())
                NotificationCenter.showAlertNotification(context, intent2, weatherDto)

            if (sharedPreferencesFactory.getAlertEnabledOrDisabled())
                CoroutineScope(Dispatchers.Main).launch {
                    showDialog(context, weatherDto)
                }
        }

    }

    private fun showDialog(context: Context, weatherResponseDto: WeatherResponseDto) {
        val notificationDialogOverApp = NotificationDialogOverApp(context, weatherResponseDto)
        notificationDialogOverApp.open()
    }


}