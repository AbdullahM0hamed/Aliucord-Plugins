package com.aliucord.plugins

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.settings.AvatarChangerSettings
import com.aliucord.plugins.settings.EditAvatar
import com.didcord.models.guild.Guild
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheetViewModel
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.viewbinding.FragmentViewBindingDelegate
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
                val bindingDelegate = sheet::class.java.getDeclaredField("binding\$delegate")
                bindingDelegate.apply { isAccessible = true }
                val d = bindingDelegate.get(sheet) as FragmentViewBindingDelegate
              val binding = (WidgetGuildProfileSheetBinding) d.getValue(sheet as Fragment, sheet.$$delegatedProperties[0])
              val lo = binding.root as NestedScrollView
              val layout = lo.findViewById(sheetId) as LinearLayout
              val actions = lo.findViewById(Utils.getResId(
                  "guild_profile_sheet_secondary_actions",
                  "id"
              )).getChildAt(0)

              TextView(actions.context).apply {
                  text = "Edit Server Icon"
                  setOnClickListener {
                      val guildStore = StoreStream.getGuilds()
                      val guild = guildStore.getGuilds()
                          .get(state.component1())

                      editDialog(context, guild, null)
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


    private fun editDialog(ctx: Context, guild: Guild?, user: User?) {
        AlertDialog.Builder(ctx)
            .setTitle("Avatar Changer")
            .setItems(
                listOf("Download Current Avatar", "Change Avatar"),
                DialogInterface.OnClickListener { dialog, item ->
                    when (item) {
                        0 -> EditAvatar.downloadAvatar(guild, user)
                        1 -> EditAvatar.setAvatar(guild, user)
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
