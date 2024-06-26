package com.dev.localnotificationexample

import android.app.*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import java.util.*
import com.dev.localnotificationexample.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "prompting", Toast.LENGTH_SHORT).show()

                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

            }
            else {
                Toast.makeText(this, "already permission granted notification", Toast.LENGTH_SHORT).show()
            }
        }

        binding.submitButton.setOnClickListener { scheduleNotification() }
    }

    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = binding.titleET.text.toString()
        val message = binding.messageET.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val time = getTime()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            alarmManager.canScheduleExactAlarms()
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
            showAlert(time, title, message)

        }
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long
    {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year
        val second = 0
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, second)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}