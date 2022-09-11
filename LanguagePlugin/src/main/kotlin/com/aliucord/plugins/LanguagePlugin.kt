package com.aliucord.plugins

import android.content.Context
import android.content.res.Resources
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.instead
import com.discord.widgets.settings.WidgetSettingsLanguage
import com.discord.widgets.settings.WidgetSettingsLanguageSelect

@AliucordPlugin
class LanguagePlugin : Plugin() {

    init {
        needsResources = true
    }

    override fun start(context: Context) {
        patcher.before<Resources>("getString", Int::class.javaObjectType) {
           // val name = context.resources.getResourceEntryName(this.args[0] as Int)
           it.result = "I'm the string now"
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
