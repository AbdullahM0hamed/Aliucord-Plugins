package com.aliucord.plugins

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.settings.AvatarChangerSettings
import com.aliucord.plugins.settings.EditAvatar
import com.discord.databinding.WidgetGuildProfileSheetBinding
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheetViewModel
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
                val guildIds = settings.getObject(
                    "guilds",
                    mutableListOf<String>()
                )

                val id = callFrame.args[0] as Long

                if (id.toString() in guildIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.invokeOriginalMethod()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                Long::class.javaObjectType,
                String::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType
            ),
            PinePatchFn { callFrame ->
                val userIds = settings.getObject(
                    "users",
                    mutableListOf<String>()
                )

                val id = callFrame.args[0] as Long

                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.invokeOriginalMethod()
                }
            }
        )

        patcher.patch(
            WidgetGuildProfileSheet::class.java.getDeclaredMethod(
                "configureUI",
                WidgetGuildProfileSheetViewModel.ViewState.Loaded::class.java,
            ),
            PinePatchFn { callFrame ->
                val sheetId = Utils.getResId(
                    "guild_profile_sheet_actions",
                    "id"
                )

                val sheet = callFrame.thisObject as WidgetGuildProfileSheet
                val state = callFrame.args[0] as WidgetGuildProfileSheetViewModel.ViewState.Loaded
                val getBinding = sheet.getDeclaredMethod("getBinding").apply { isAccessible = true }
                val binding = getBinding.invoke(callFrame.thisObject) as WidgetGuildProfileSheetBinding
                val lo = binding.root as NestedScrollView
                val layout = lo.findViewById(sheetId) as LinearLayout
                val actions = (
                    lo.findViewById(
                        Utils.getResId(
                            "guild_profile_sheet_secondary_actions",
                            "id"
                        )
                    ) as FrameLayout
                    ).getChildAt(0) as LinearLayout

                TextView(actions.context).apply {
                    text = "Edit Server Icon"
                    setOnClickListener {
                        val guildStore = StoreStream.getGuilds()
                        val guild = guildStore.getGuilds()
                            .get(state.component1())

                        editDialog(
                            context,
                            sheet.parentFragmentManager,
                            guild,
                            null
                        )
                    }
                }.also {
                    val index = actions.indexOfChild(
                        actions.findViewById(
                            Utils.getResId(
                                "guild_profile_sheet_change_nickname",
                                "id"
                            )
                        )
                    )
                    actions.addView(this, index + 1)
                }
            }
        )
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    private fun editDialog(
        ctx: Context,
        manager: FragmentManager,
        guild: Guild?,
        user: User?
    ) {
        AlertDialog.Builder(ctx)
            .setTitle("Avatar Changer")
            .setItems(
                arrayOf("Download Current Avatar", "Change Avatar"),
                { dialog, item ->
                    when (item) {
                        0 -> EditAvatar.downloadAvatar(
                            ctx,
                            guild,
                            user
                        )
                        1 -> EditAvatar.setAvatar(
                            ctx,
                            manager,
                            guild,
                            user
                        )
                    }

                    dialog.dismiss()
                }
            )
            .show()
    }

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
