package app.lawnchair.smartspace.provider

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.content.edit
import app.lawnchair.smartspace.model.SmartspaceAction
import app.lawnchair.smartspace.model.SmartspaceScores
import app.lawnchair.smartspace.model.SmartspaceTarget
import app.lawnchair.util.formatShortElapsedTime
import com.android.launcher3.R
import java.util.Calendar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ScreenTimeProvider(context: Context) :
    SmartspaceDataSource(
        context,
        R.string.smartspace_screen_time,
        { smartspaceScreenTime },
    ) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var accumulatedTimeMs = restoreAccumulatedTime()
    private var screenOnTimestampMs = -1L
    private var lastSentTotalMs = -1L

    fun getCurrentTotalMs(): Long {
        val acc = accumulatedTimeMs
        return if (screenOnTimestampMs >= 0L) {
            acc + (SystemClock.elapsedRealtime() - screenOnTimestampMs)
        } else {
            acc
        }
    }

    override val internalTargets = callbackFlow {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager?.isInteractive == true) {
            screenOnTimestampMs = SystemClock.elapsedRealtime()
        }

        fun sendIfChanged() {
            val total = getCurrentTotalMs()
            if (total != lastSentTotalMs) {
                lastSentTotalMs = total
                trySend(buildTargets(total))
            }
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        screenOnTimestampMs = SystemClock.elapsedRealtime()
                        sendIfChanged()
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        accumulate()
                        persistAccumulatedTime()
                        sendIfChanged()
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(receiver, filter)

        sendIfChanged()

        val tickHandler = Handler(Looper.getMainLooper())
        val tick = object : Runnable {
            override fun run() {
                persistAccumulatedTime()
                sendIfChanged()
                tickHandler.postDelayed(this, TICK_INTERVAL_MS)
            }
        }
        tickHandler.postDelayed(tick, TICK_INTERVAL_MS)

        awaitClose {
            tickHandler.removeCallbacks(tick)
            context.unregisterReceiver(receiver)
        }
    }

    private fun accumulate() {
        if (screenOnTimestampMs >= 0L) {
            accumulatedTimeMs += SystemClock.elapsedRealtime() - screenOnTimestampMs
            screenOnTimestampMs = -1L
        }
    }

    private fun buildTargets(total: Long): List<SmartspaceTarget> {
        if (total <= 0L) return emptyList()

        val formatted = formatShortElapsedTime(context, total)
            ?: context.getString(R.string.screen_time_no_data)

        return listOf(
            SmartspaceTarget(
                id = "screenTime",
                headerAction = SmartspaceAction(
                    id = "screenTimeAction",
                    icon = Icon.createWithResource(context, R.drawable.ic_screen_time),
                    title = context.getString(R.string.screen_time_today),
                    subtitle = formatted,
                ),
                score = SmartspaceScores.SCORE_SCREEN_TIME,
                featureType = SmartspaceTarget.FeatureType.FEATURE_SCREEN_TIME,
            ),
        )
    }

    private fun restoreAccumulatedTime(): Long {
        val savedDate = prefs.getLong(PREF_DATE, -1L)
        val todayStart = getTodayStartMs()
        return if (savedDate == todayStart) {
            prefs.getLong(PREF_ACCUMULATED, 0L)
        } else {
            prefs.edit { putLong(PREF_DATE, todayStart) }
            0L
        }
    }

    private fun persistAccumulatedTime() {
        prefs.edit {
            putLong(PREF_DATE, getTodayStartMs())
            putLong(PREF_ACCUMULATED, accumulatedTimeMs)
        }
    }

    private fun getTodayStartMs(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    companion object {
        private const val PREFS_NAME = "screen_time_prefs"
        private const val PREF_DATE = "today_date"
        private const val PREF_ACCUMULATED = "accumulated_ms"
        private const val TICK_INTERVAL_MS = 1_000L
    }
}
