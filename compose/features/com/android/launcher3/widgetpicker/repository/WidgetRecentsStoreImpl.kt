/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.widgetpicker.repository

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.UserHandle
import com.android.launcher3.dagger.ApplicationContext
import com.android.launcher3.widgetpicker.data.repository.WidgetRecentsStore
import com.android.launcher3.widgetpicker.shared.model.WidgetId
import javax.inject.Inject

class WidgetRecentsStoreImpl @Inject constructor(
    @ApplicationContext context: Context,
) : WidgetRecentsStore {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun addRecentWidget(widgetId: WidgetId) {
        val key = widgetId.toStorageKey()
        val existing = prefs.getStringSet(KEY_RECENT_WIDGETS, emptySet())!!.toMutableSet()
        existing.remove(key)
        existing.add(key)
        val trimmed = existing.toList().takeLast(MAX_RECENT_WIDGETS).toSet()
        prefs.edit().putStringSet(KEY_RECENT_WIDGETS, trimmed).apply()
    }

    override fun getRecentWidgetIds(): List<WidgetId> {
        val stored = prefs.getStringSet(KEY_RECENT_WIDGETS, emptySet())!!
        return stored.mapNotNull { it.toWidgetId() }.reversed()
    }

    override fun clearRecents() {
        prefs.edit().remove(KEY_RECENT_WIDGETS).apply()
    }

    private companion object {
        private const val PREFS_NAME = "widget_recents_store"
        private const val KEY_RECENT_WIDGETS = "recent_widgets"
        private const val MAX_RECENT_WIDGETS = 12
        private const val SEPARATOR = "|"

        private fun WidgetId.toStorageKey(): String {
            return "${componentName.flattenToString()}$SEPARATOR${userHandle.hashCode()}"
        }

        private fun String.toWidgetId(): WidgetId? {
            val separatorIndex = lastIndexOf(SEPARATOR)
            if (separatorIndex <= 0) return null
            val componentNameStr = substring(0, separatorIndex)
            val userHash = substring(separatorIndex + 1).toIntOrNull() ?: return null
            val componentName = ComponentName.unflattenFromString(componentNameStr) ?: return null
            return WidgetId(
                componentName = componentName,
                userHandle = UserHandle.of(userHash),
            )
        }
    }
}
