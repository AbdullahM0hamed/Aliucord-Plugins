package com.aliucord.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PreHook
import com.aliucord.plugins.settings.TextReplaceSettings
import com.discord.widgets.chat.MessageContent

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
        mSettings = settings
        patcher.patch(MessageContent::class.java.getDeclaredMethod("sendMessage"),
        PreHook { callFrame ->
            val textContent = MessageContent::class.java.getDeclaredField("textContent").apply {
                isAccessible = true
            }
            var text = textContent.get(callFrame.thisObject) as String
            val map = TextReplace.mSettings.getObject(
                "replaceMap",
                mutableMapOf<String, String>()
            )

            map.toList().forEach { old, new ->
                text = text.replace(old, new)
            }

            callFrame.result = text
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
