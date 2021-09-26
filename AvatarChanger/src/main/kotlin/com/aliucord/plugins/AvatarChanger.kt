package com.aliucord.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.settings.AvatarChangerSettings
import com.discord.utilities.icon.IconUtils
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
            IconUtils::class.java.getDeclaredMethod(
                "getForGuild",
                Long::class.javaObjectType,
                String::class.javaObjectType,
                String::class.javaObjectType,
                Boolean::class.java,
                Int::class.javaObjectType
            ),
            PinePatchFn { callFrame ->
                //Definitely shown
                //Utils.showToast(context, "is shown L43")
                val guildIds = mSettings.getObject(
                    "guilds",
                    mutableListOf<String>()
                )

                //Shown
                //Utils.showToast(context, "is shown L50")
                val id = callFrame.args[0] as Long

                //Shown
                //Utils.showToast(context, "is shown L54")

                //Is an ID as expected
                //Utils.showToast(context, id.toString())

                //True when it should be
                //False positives after a true?
                //Utils.showToast(context, (id.toString() in guildIds).toString())
                if (id.toString() in guildIds) {
                    //shown
                    //Utils.showToast(context, "is shown L64")
                    val icon = settings.getString(
                        "AC_AvatarChanger_${id}",
                        callFrame.result as String
                    )

                    //Icon url not being taken from shared prefs???
                    //Utils.showToast(context, "Icon URL: $icon")

                    callFrame.result = icon
                } else {
                    callFrame.invokeOriginalMethod()
                }
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
