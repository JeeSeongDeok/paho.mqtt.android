package org.eclipse.paho.android.service
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.suspendCancellableCoroutine
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import kotlin.coroutines.resume
class PingWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result =
        suspendCancellableCoroutine { continuation ->
            Log.d("[PingWorker]","Sending Ping at: ${System.currentTimeMillis()}")
            AlarmPingSender.clientComms?.checkForActivity(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("[PingWorker]","Success.")
                    continuation.resume(Result.success())
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("[PingWorker]", "Failure $exception")
                    continuation.resume(Result.failure())
                }
            }) ?: kotlin.run {
                continuation.resume(Result.failure())
            }
        }
}