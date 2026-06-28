package com.android.launcher3.icons.cache

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import java.io.File

class IconFileStore(private val context: Context) {

    private val rootDir: File
        get() = File(context.filesDir, DIR_NAME).also { it.mkdirs() }

    private fun userDir(userSerial: Long): File =
        File(rootDir, userSerial.toString()).also { it.mkdirs() }

    private fun pkgDir(userSerial: Long, packageName: String): File =
        File(userDir(userSerial), packageName).also { it.mkdirs() }

    private fun iconFile(componentName: ComponentName, userSerial: Long): File =
        File(pkgDir(userSerial, componentName.packageName), "${componentName.className}.png")

    private fun monoFile(componentName: ComponentName, userSerial: Long): File =
        File(pkgDir(userSerial, componentName.packageName), "${componentName.className}_mono.png")

    fun saveIcon(componentName: ComponentName, userSerial: Long, bitmap: Bitmap) {
        iconFile(componentName, userSerial).outputStream().use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
    }

    fun saveMonoData(componentName: ComponentName, userSerial: Long, data: ByteArray) {
        monoFile(componentName, userSerial).outputStream().use { fos ->
            fos.write(data)
        }
    }

    fun loadIcon(componentName: ComponentName, userSerial: Long): Bitmap? {
        val file = iconFile(componentName, userSerial)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(
            file.absolutePath,
            Options().apply { inPreferredConfig = HARDWARE },
        )
    }

    fun loadMonoData(componentName: ComponentName, userSerial: Long): ByteArray? {
        val file = monoFile(componentName, userSerial)
        if (!file.exists()) return null
        return file.readBytes()
    }

    fun deleteIcon(componentName: ComponentName, userSerial: Long) {
        iconFile(componentName, userSerial).delete()
        monoFile(componentName, userSerial).delete()
    }

    fun deleteAllForPackage(packageName: String, userSerial: Long) {
        File(userDir(userSerial), packageName).deleteRecursively()
    }

    fun deleteAll() {
        rootDir.deleteRecursively()
    }

    companion object {
        private const val DIR_NAME = "icons_v2"
    }
}
