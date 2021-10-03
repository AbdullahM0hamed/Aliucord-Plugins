package com.aliucord.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.plugins.settings.TextReplaceSettings

@AliucordPlugin
class TextReplace : Plugin() {

    lateinit var pluginIcon: Drawable

    init {
        settingsTab = SettingsTab(TextReplaceSettings::class.java)
    }

    override fun load(context: Context) {
        pluginIcon = ContextCompat.getDrawable(
            context,
            Utils.getResId(
                "drawable_icon_sync_integration",
                "drawable"
            )
        )!!
    }

    override fun start(context: Context) {
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
