package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.discord.widgets.settings.WidgetSettingsLanguage
import com.discord.widgets.settings.WidgetSettingsLanguageSelect

@AliucordPlugin
class Translate : Plugin() {

    init {
        needsResources = true
    }

    override fun start(context: Context) {
        val ourLocales = mutableListOf<String>()
        ourLocales.add("so")

        patcher.after<WidgetSettingsLanguageSelect.Model>("getLocales") {
            logger.error("BEFORE CAST", null)
            val allLocales = mutableListOf<String>()
            val locales = it.result as List<String>
            logger.error("AFTER CAST", null)
            allLocales.addAll(locales)
            allLocales.addAll(ourLocales)
            logger.error("BEFORE RESULT", null)
            it.result = allLocales
            logger.error("AFTER RESULT", null)
        }

        patcher.before<WidgetSettingsLanguage.Companion>(
            "getAsStringInLocale",
            String::class.javaObjectType
        ) {
            if ((it.args[0] as String) in ourLocales) {
                it.result = "Af-Soomaali"
            }
        }

        patcher.before<WidgetSettingsLanguage.Companion>(
            "getLocaleFlagResId",
            String::class.javaObjectType
        ) {
            if ((it.args[0] as String) in ourLocales) {
                it.result = resources.getIdentifier(
                    "icon_flag_so",
                    "drawable",
                    "com.aliucord.plugins"
                )
            }
        }

        patcher.before<WidgetSettingsLanguage.Companion>(
            "getLocaleResId",
            String::class.javaObjectType
        ) {
            if ((it.args[0] as String) in ourLocales) {
                it.args[0] = null
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
