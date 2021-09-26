package com.aliucord.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.settings.AvatarChangerSettings
import com.discord.models.guild.Guild
import com.discord.models.user.CoreUser
import com.lytefast.flexinput.R

@AliucordPlugin
class AvatarChanger : Plugin() {

    lateinit var pluginIcon: Drawable

    init {
        settingsTab = SettingsTab(AvatarChangerSettings::class.java)
    }

    override fun load(context: Context) {
        pluginIcon = ContextCompat.getDrawable(
            context, 
            R.d.ic_profile_24dp
        )!!
    }

    override fun start(context: Context) {
        mSettings = settings
        patcher.patch(
            Guild::class.java.getDeclaredMethod("getIcon"),
            PinePatchFn { callFrame ->
                val guild = callFrame.thisObject as Guild
                val guildIds = mSettings.getObject(
                    "guilds",
                    mutableListOf<String>()
                )

                Utils.showToast(context, (guild.id.toString() in guilfIds))
                if (guild.id.toString() in guildIds) {
                    val icon = mSettings.getString(
                        "AC_AvatarChanger_${guild.id}",
                        callFrame.result as String
                    )
                    callFrame.result = icon
                } else {
                    callFrame.result = callFrame.result
                }
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
