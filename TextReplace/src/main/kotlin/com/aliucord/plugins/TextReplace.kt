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
import com.discord.stores.StoreStream
import com.aliucord.utils.ReflectUtils
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.chat.list.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entry.MessageEntry

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
        patcher.patch(
            WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod(
                "processMessageText",
                SimpleDraweeSpanTextView::class.java,
                MessageEntry::class.java
            ),
            Hook { callFrame ->
                val msg = (callFrame.args[1] as MessageEntry).message
                val text = ReflectUtils.getField(msg, "content") as String
                val map = TextReplace.mSettings.getObject(
                    "replaceMap",
                    mutableMapOf<String, String>()
                )

                map.toList().forEach { (old, new) ->
                    text = text.replace(Regex(old), new)
                }

                ReflectUtils.setField(msg, "content", text)
                StoreStream.messages.handleMessageUpdate(
                    msg.synthesizeApiMessage()
                )
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
