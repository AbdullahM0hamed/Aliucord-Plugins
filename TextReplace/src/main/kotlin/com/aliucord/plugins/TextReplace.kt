package com.aliucord.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.plugins.settings.TextReplaceSettings
import com.discord.models.message.Message
import com.discord.stores.StoreMessages

@AliucordPlugin
class TextReplace : Plugin() {

    lateinit var pluginIcon: Drawable
    private val contentField =
        Message::class.java.getDeclaredField("content").apply {
            isAccessible = true
        }

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
        patcher.patch(
            StoreMessages::class.java.getDeclaredMethod(
                "getMessage",
                Long::class.javaObjectType,
                Long::class.javaObjectType
            ),
            Hook { callFrame ->
                val message = callFrame.result as Mesaage
                var text = contentField.get(message) as String

                val map = TextReplace.mSettings.getObject(
                    "replaceMap",
                    mutableMapOf<String, String>()
                )

                map.toList().forEach { (old, new) ->
                    text = text.replace(Regex(old), new)
                }

                contentField.set(message, text)
                callFram.result = message
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
