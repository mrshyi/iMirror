package dev.imirror.receiver.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.imirror.receiver.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * BootReceiver — starts [MirrorService] on device boot if "Start on boot" is enabled.
 *
 * WHY: Android kills all services on reboot. For a receiver app to work without
 * the user manually reopening the app, we register for BOOT_COMPLETED.
 * The setting is checked asynchronously before starting the service to avoid
 * unnecessary foreground service starts that would show an unexpected notification.
 *
 * BOOT_COMPLETED allows the connectedDevice foreground service to start in the
 * background. The receiver reads the saved preference before starting it.
 *
 * Declared in AndroidManifest.xml with `exported="false"` — only the system
 * can fire BOOT_COMPLETED, so no external app can trigger this receiver.
 *
 * [goAsync] keeps the broadcast pending while DataStore is read off the main thread.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Timber.d("BootReceiver: BOOT_COMPLETED received")

        val pendingResult: PendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SettingsRepository(context.applicationContext).settingsFlow.first()
                if (settings.startOnBoot) {
                    Timber.i("BootReceiver: startOnBoot=true → starting MirrorService")
                    ServiceController.start(context)
                } else {
                    Timber.d("BootReceiver: startOnBoot=false → not starting service")
                }
            } catch (e: Exception) {
                Timber.e(e, "BootReceiver: failed to read settings or start service")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
