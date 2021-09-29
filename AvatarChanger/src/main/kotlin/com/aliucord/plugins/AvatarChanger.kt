package com.aliucord.plugins

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import android.view.View
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
import com.aliucord.plugins.settings.UserAdapter
import com.aliucord.utils.DimenUtils
import com.discord.databinding.WidgetGuildProfileSheetBinding
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheetViewModel
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
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
                val id = callFrame.args[0] as Long

                val guildIds = AvatarChangerSettings.getGuildIds()
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
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
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

        val editId = View.generateViewId()
        val removeId = View.generateViewId()

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val padding = DimenUtils.dpToPx(16)

        val actionStyle = Utils.getResId(
            "GuildProfileSheet.Actions.Title",
            "style"
        )

        val getBinding = WidgetGuildProfileSheet::class.java
            .getDeclaredMethod("getBinding")
            .apply { isAccessible = true }

        patcher.patch(
            WidgetGuildProfileSheet::class.java.getDeclaredMethod(
                "configureUI",
                WidgetGuildProfileSheetViewModel.ViewState.Loaded::class.java,
            ),
            PinePatchFn { callFrame ->
                val sheet = callFrame.thisObject as WidgetGuildProfileSheet
                val state = callFrame.args[0] as WidgetGuildProfileSheetViewModel.ViewState.Loaded
                val binding = getBinding.invoke(callFrame.thisObject) as WidgetGuildProfileSheetBinding
                val lo = binding.root as NestedScrollView
                val actions = (
                    lo.findViewById(
                        Utils.getResId(
                            "guild_profile_sheet_secondary_actions",
                            "id"
                        )
                    ) as FrameLayout
                    ).getChildAt(0) as LinearLayout

                val index = actions.indexOfChild(
                    actions.findViewById(
                        Utils.getResId(
                            "guild_profile_sheet_mark_as_read",
                            "id"
                        )
                    )
                ) + 1

                val guildStore = StoreStream.getGuilds()
                val guild = guildStore.getGuilds()
                    .get(state.component1())

                TextView(
                    actions.context,
                    null,
                    0,
                    actionStyle
                ).apply {
                    text = "Edit Server Icon"
                    id = editId
                    layoutParams = params
                    setOnClickListener {
                        editDialog(
                            sheet.activity as Context,
                            sheet.parentFragmentManager,
                            guild,
                            null
                        )
                    }
                }.also {
                    it.setPadding(padding, padding, padding, padding)

                    val view = actions.findViewById(editId) as View?
                    if (view == null) {
                        actions.addView(it, index)
                    }
                }

                val guildIds = AvatarChangerSettings.getGuildIds()
                if (guild!!.id.toString() in guildIds) {
                    TextView(
                        actions.context,
                        null,
                        0,
                        actionStyle
                    ).apply {
                        text = "Revert Server Icon"
                        id = removeId
                        layoutParams = params
                        setOnClickListener {
                            UserAdapter.removeDialog(
                                guild,
                                null,
                                sheet.parentFragmentManager
                            )
                        }
                    }.also {
                        it.setPadding(padding, padding, padding, padding)

                        val view = actions.findViewById(removeId) as View?
                        if (view == null) {
                            actions.addView(it, index + 1)
                        }
                    }
                }
            }
        )

        val profileBinding = WidgetUserSheet::class.java
            .getDeclaredMethod("getBinding")
            .apply { isAccessible = true }

        val headerId = View.generateViewId()
        patcher.patch(
            WidgetUserSheet::class.java.getDeclaredMethod(
                "configureUI",
                WidgetUserSheetViewModel.ViewState::class.java
            ),
            PinePatchFn { callFrame ->
                // Yes
                //Utils.showToast(context, "Correct Method")
                val state = callFrame.args[0] as WidgetUserSheetViewModel.ViewState

                Utils.showToast(context, (state is WidgetUserSheetViewModel.ViewState.Loaded).toString())
                if (state is WidgetUserSheetViewModel.ViewState.Loaded) {
                    val user = state.user
                    val sheet = callFrame.thisObject as WidgetUserSheet
                    val binding = profileBinding.invoke(sheet) as WidgetUserSheetBinding
                    val root = binding.root as NestedScrollView
                    val layout = root.findViewById(
                        Utils.getResId("user_sheet_content", "style")
                    ) as LinearLayout

                    val index = layout.indexOfChild(
                        layout.findViewById(
                            Utils.getResId("about_me_header", "style")
                        )
                    )

                    TextView(
                        sheet.activity as Context,
                        null,
                        0,
                        Utils.getResId(
                            "UserProfile.Section.Header",
                            "style"
                        )
                    ).apply {
                        text = "Avatar Changer"
                        id = headerId
                        layoutParams = params
                    }.also {
                        it.setPadding(padding,padding,padding,padding)

                        val header = root.findViewById(headerId) as View?

                        if (header == null) {
                            layout.addView(it, index)
                        }
                    }

                    TextView(
                        sheet.activity as Context,
                        null,
                        0,
                        Utils.getResId(
                            "UiKit.ListItem.Icon",
                            "style"
                        )
                    ).apply {
                        text = "Edit User Avatar"
                        id = editId
                        layoutParams = params
                        setOnClickListener {
                            editDialog(
                                sheet.activity as Context,
                                sheet.parentFragmentManager,
                                null,
                                user
                            )
                        }
                    }.also {
                        it.setPadding(padding,padding,padding,padding)
                        it.setCompoundDrawablesWithIntrinsicBounds(
                            Utils.getResId("ic_edit_white_a60_24dp", "drawable"),
                            0,
                            0,
                            0
                        )
                        val view = layout.findViewById(editId) as View?

                        if (view == null) {
                            layout.addView(it, index + 1)
                        }
                    }

                    TextView(
                        sheet.activity as Context,
                        null,
                        0,
                        Utils.getResId(
                            "UiKit.ListItem.Icon",
                            "style"
                        )
                    ).apply {
                        text = "Revert User Avatar"
                        id = editId
                        layoutParams = params
                        setOnClickListener {
                            UserAdapter.removeDialog(
                                null,
                                user,
                                sheet.parentFragmentManager
                            )
                        }
                    }.also {
                        it.setPadding(padding,padding,padding,padding)
                        it.setCompoundDrawablesWithIntrinsicBounds(
                            Utils.getResId("ic_refresh_white_a60_24dp", "drawable"),
                            0,
                            0,
                            0
                        )
                        val view = layout.findViewById(removeId) as View?

                        if (view == null) {
                            layout.addView(it, index + 2)
                        }
                    }
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
        AlertDialog.Builder(
            ContextThemeWrapper(
                ctx,
                Utils.getResId(
                    "AppTheme.Dark.BottomSheet",
                    "style"
                )
            )
        )
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
