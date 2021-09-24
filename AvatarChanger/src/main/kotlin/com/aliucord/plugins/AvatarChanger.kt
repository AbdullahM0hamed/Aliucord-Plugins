package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.plugins.settings.AvatarChangerSettings

@AliucordPlugin
class AvatarChanger : Plugin() {

    init {
        settingsTab = SettingsTab(AvatarChangerSettings::class.java)

    }

    override fun start(context: Context) {
        mSettings = settings
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
