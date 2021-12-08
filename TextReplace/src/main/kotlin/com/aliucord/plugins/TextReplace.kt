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
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.input.ChatInputViewModel

@AliucordPlugin
class TextReplace : Plugin() {

    lateinit var pluginIcon: Drawable
    private val textContentField =
        MessageContent::class.java.getDeclaredField("textContent").apply {
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
            ChatInputViewModel::class.java.getDeclaredMethod(
                "sendMessage",
                Context::class.java,
                MessageManager::class.java,
                MessageContent::class.java,
                List::class.java,
                Boolean::class.javaPrimitiveType!!,
                Function1::class.java
            ),
            PreHook { callFrame ->
                val messageContent = callFrame.args[2] as MessageContent
                var text = textContent.get(messageContent) as String
                val map = TextReplace.mSettings.getObject(
                    "replaceMap",
                    mutableMapOf<String, String>()
                )

                map.toList().forEach { (old, new) ->
                    text = text.replace(Regex(old), new)
                }

                textContent.set(messageContent, text)
                callFrame.args[2] = messageContent
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
