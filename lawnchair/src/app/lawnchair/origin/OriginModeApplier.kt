package app.lawnchair.origin

import android.content.Context

object OriginModeApplier {

    fun scaleDuration(context: Context, duration: Long): Long {
        val motionScale = OriginModeManager.getInstance(context).currentConfig().motionScale
        return (duration * motionScale).toLong()
    }
}
