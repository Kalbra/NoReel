package src

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noreel.BuildConfig
import com.example.noreel.R
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch


class UpdateChecker(val context: Context) {
    fun fetchRemote() {
        val latch = CountDownLatch(1)
        val urlString =
            "https://raw.githubusercontent.com/Kalbra/NoReel/master/app/build.gradle"

        Thread {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 1000
                connection.readTimeout = 1000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = BufferedInputStream(connection.inputStream)

                    reader(inputStream)
                } else {
                    Log.e("InjectionBuilder", "Error response code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("InjectionBuilder", "Error fetching JavaScript file: ${e.message}")
            } finally {
                latch.countDown()
            }
        }.start()
    }

    private fun reader(inputStream: InputStream) {
        Log.d("Update", "Update - Reader")
        val reader = inputStream.bufferedReader()
        reader.useLines {
            reader.use {
                for (raw_line in it.lines().toArray()) {
                    val line = raw_line.toString().trimStart()
                    val args = line.split(" ").toTypedArray()
                    if(args[0] == "versionCode"){
                        try{
                            compareVersions(args[1].toInt())
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    private fun compareVersions(newestVersionCode: Int) {
        val LocalVersionCode: Int = BuildConfig.VERSION_CODE
        Log.d("Update", "Local version code: ${LocalVersionCode.toString()}; Newest version code: ${newestVersionCode}")
        if(LocalVersionCode < newestVersionCode){ //LocalVersionCode < newestVersionCode
            val webpageUrl = "https://github.com/Kalbra/NoReel/releases/latest/download/NoReel.apk"
            val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webpageUrl))
            val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) //Problem
            val builder = NotificationCompat.Builder(context, "Update")
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("Update available")
                .setContentText("Click here to download APK")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                notify(1, builder.build())
            }
        }
    }
}