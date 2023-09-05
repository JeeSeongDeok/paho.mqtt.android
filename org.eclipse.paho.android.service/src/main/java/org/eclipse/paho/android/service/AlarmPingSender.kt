package org.eclipse.paho.android.service
import android.util.Log
import androidx.work.*
import org.eclipse.paho.client.mqttv3.MqttPingSender
import org.eclipse.paho.client.mqttv3.internal.ClientComms
import java.util.concurrent.TimeUnit


/**
 * Default ping sender implementation on Android. It is based on AlarmManager.
 *
 * This class implements the [MqttPingSender] ping interface
 * allowing applications to send ping packet to server every keep alive interval.
 *
 * @see MqttPingSender
 */
internal class AlarmPingSender(val service: MqttService) : MqttPingSender {

    private val workManager = WorkManager.getInstance(service)

    override fun init(comms: ClientComms) {
        clientComms = comms
    }

    override fun start() {
        schedule(clientComms!!.keepAlive)
    }

    override fun stop() {
        workManager.cancelUniqueWork(PING_JOB)
    }

    override fun schedule(delayInMilliseconds: Long) {
        Log.d("[AlarmPingSender]","Schedule next alarm at ${System.currentTimeMillis() + delayInMilliseconds}")
        workManager.enqueueUniqueWork(
            PING_JOB,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest
                .Builder(PingWorker::class.java)
                .setInitialDelay(delayInMilliseconds, TimeUnit.MILLISECONDS)
                .build()
        )
    }

    companion object {
        private const val PING_JOB = "PING_JOB"
        internal var clientComms: ClientComms? = null
    }

}