package com.aliucord.plugins

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.plugins.settings.AvatarChangerSettings
import com.aliucord.plugins.settings.EditAvatar
import com.aliucord.plugins.settings.UserAdapter
import com.aliucord.utils.DimenUtils
import com.discord.databinding.WidgetGuildProfileSheetBinding
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.models.guild.Guild
import com.discord.models.member.GuildMember
import com.discord.models.user.CoreUser
import com.discord.models.user.User
import com.discord.nullserializable.NullSerializable.a
import com.discord.nullserializable.NullSerializable.b
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages.ChangeDetector
import com.discord.utilities.user.UserUtils
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheetViewModel
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlin.Unit
import kotlin.jvm.functions.Function1

@AliucordPlugin
class AvatarChanger : Plugin() {

    lateinit var pluginIcon: Drawable

    init {
        settingsTab = SettingsTab(AvatarChangerSettings::class.java)
    }

    override fun load(context: Context) {
        pluginIcon = ContextCompat.getDrawable(
            context,
            Utils.getResId("ic_profile_24dp", "drawable")
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
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val guildIds = AvatarChangerSettings.getGuildIds()
                if (id.toString() in guildIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
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
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                User::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = (callFrame.args[0] as User).id

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                User::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = (callFrame.args[0] as User).id

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                User::class.javaObjectType,
                Boolean::class.javaPrimitiveType
            ),
            Hook { callFrame ->
                val id = (callFrame.args[0] as User).id

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                Long::class.javaObjectType,
                String::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                Long::class.javaObjectType,
                String::class.javaObjectType,
                Int::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                Long::class.javaObjectType,
                String::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType
            ),
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser\$default",
                User::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType,
                Int::class.javaObjectType,
                Any::class.java
            ),
            Hook { callFrame ->
                val id = (callFrame.args[0] as User).id

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser\$default",
                Long::class.javaObjectType,
                String::class.javaPrimitiveType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType,
                Int::class.javaObjectType,
                Any::class.java
            ),
            Hook { callFrame ->
                val id = callFrame.args[0] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForGuildMember",
                GuildMember::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType
            ),
            Hook { callFrame ->
                val id = (callFrame.args[0] as GuildMember).userId

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForGuildMember",
                String::class.javaObjectType,
                Long::class.javaObjectType,
                Long::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = callFrame.args[2] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForGuildMember\$default",
                IconUtils::class.javaObjectType,
                GuildMember::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType,
                Any::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = (callFrame.args[1] as GuildMember).userId

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForGuildMember\$default",
                IconUtils::class.javaObjectType,
                String::class.javaObjectType,
                Long::class.javaObjectType,
                Long::class.javaObjectType,
                Int::class.javaObjectType,
                Boolean::class.javaPrimitiveType,
                Int::class.javaObjectType,
                Any::class.javaObjectType
            ),
            Hook { callFrame ->
                val id = callFrame.args[3] as Long

                val userIds = AvatarChangerSettings.getUserIds()
                if (id.toString() in userIds) {
                    val icon = settings.getString(
                        id.toString(),
                        callFrame.result as String
                    )

                    callFrame.result = icon
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            UserUtils::class.java.getDeclaredMethod(
                "synthesizeApiUser",
                User::class.javaObjectType
            ),
            Hook { callFrame ->
                val user = callFrame.args[0] as User
                if (user.id.toString() in AvatarChangerSettings.getUserIds()) {
                    val icon = settings.getString(
                        user.id.toString(),
                        "" as String
                    )

                    callFrame.result = com.discord.api.user.User(
                        user.id,
                        user.username,
                        if (icon != null) b(icon) else a(null, 1),
                        null,
                        user.discriminator.toString(),
                        user.publicFlags.toInt(),
                        user.flags.toInt(),
                        user.isBot(),
                        user.isSystemUser(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        3145736
                    )
                } else {
                    callFrame.getResult()
                }
            }
        )

        patcher.patch(
            IconUtils::class.java.getDeclaredMethod("withSize",
                String::class.javaObjectType,
                Int::class.javaObjectType
            ),
            Hook { callFrame ->
                val userIds = AvatarChangerSettings.getUserIds()
                var custom = false
                for (id in userIds) {
                    if (id in (callFrame.args[0] as String)) {
                        custom = true

                        val icon = settings.getString(
                            id.toString(),
                            callFrame.result as String
                        )
                        callFrame.result = icon
                        break
                    }
                }

                if (!custom) {
                    callFrame.getResult()
                }
            }
        )

        val setIcon = IconUtils::class.java.getDeclaredMethod(
            "setIcon",
            ImageView::class.javaObjectType,
            User::class.javaObjectType,
            Int::class.javaObjectType,
            Function1::class.javaObjectType,
            ChangeDetector::class.javaObjectType,
            GuildMember::class.javaObjectType
        )

        patcher.patch(
            setIcon,
            Hook { callFrame ->
                val user = callFrame.args[0] as User

                if (user.id.toString() in AvatarChangerSettings.getUserIds()) {
                    val icon = settings.getString(
                        user.id.toString(),
                        ""
                    )

                    val avatar = user::class.java.getDeclaredField("avatar")
                    avatar.isAccessible = true
                    avatar.set(user, icon)
                    val iconUtil = callFrame.thisObject as IconUtils
                    setIcon.apply { isAccessible = true }
                    callFrame.result = setIcon(
                        iconUtil,
                        callFrame.args[0] as ImageView,
                        user,
                        callFrame.args[2] as Int,
                        callFrame.args[3] as Function1<ImageRequestBuilder, Unit>,
                        callFrame.args[4] as ChangeDetector,
                        callFrame.args[5] as GuildMember
                    )
                } else {
                    callFrame.getResult()
                }
            }
        )
        //patcher.patch(
            //CoreUser::class.java.getDeclaredMethod(
                //"getAvatar"
            //),
            //Hook { callFrame ->
                //val id = (callFrame.thisObject as CoreUser).id
                //val userIds = AvatarChangerSettings.getUserIds()

                //if (id.toString() in userIds) {
                    //val icon = settings.getString(
                        //id.toString(),
                        //callFrame.result as String
                    //)

                    //callFrame.result = icon
                //} else {
                    //callFrame.getResult()
                //}
            //}
        //)

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
            Hook { callFrame ->
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
            Hook { callFrame ->
                val state = callFrame.args[0] as WidgetUserSheetViewModel.ViewState

                if (state is WidgetUserSheetViewModel.ViewState.Loaded) {
                    val user = state.user

                    val sheet = callFrame.thisObject as WidgetUserSheet
                    val binding = profileBinding.invoke(sheet) as WidgetUserSheetBinding

                    val root = binding.root as NestedScrollView
                    val layout = root.findViewById(
                        Utils.getResId("user_sheet_content", "id")
                    ) as LinearLayout

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
                        it.setPadding(padding, padding, padding, padding)

                        val header = root.findViewById(headerId) as View?

                        if (header == null) {
                            layout.addView(it)
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
                        it.setPadding(padding, padding, padding, padding)
                        it.setCompoundDrawablesWithIntrinsicBounds(
                            Utils.getResId("ic_edit_white_a60_24dp", "drawable"),
                            0,
                            0,
                            0
                        )
                        val view = layout.findViewById(editId) as View?

                        if (view == null) {
                            layout.addView(it)
                        }
                    }

                    val userIds = AvatarChangerSettings.getUserIds()

                    if (user.id.toString() in userIds) {
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
                            id = removeId
                            layoutParams = params
                            setOnClickListener {
                                UserAdapter.removeDialog(
                                    null,
                                    user,
                                    sheet.parentFragmentManager
                                )
                            }
                        }.also {
                            it.setPadding(padding, padding, padding, padding)
                            it.setCompoundDrawablesWithIntrinsicBounds(
                                Utils.getResId("ic_refresh_white_a60_24dp", "drawable"),
                                0,
                                0,
                                0
                            )
                            val view = layout.findViewById(removeId) as View?

                            if (view == null) {
                                layout.addView(it)
                            }
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
        val title = SpannableString("Avatar Changer")
        val download = SpannableString("Download Current Avatar")
        val change = SpannableString("Change Avatar")
        var typedValue = TypedValue()
        ctx.theme.resolveAttribute(
            Utils.getResId("colorInteractiveActive", "attr"),
            typedValue,
            true
        )

        val span = ForegroundColorSpan(typedValue.data)
        title.setSpan(span, 0, title.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        download.setSpan(span, 0, download.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        change.setSpan(span, 0, change.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val dialog = AlertDialog.Builder(
            ContextThemeWrapper(
                ctx,
                Utils.getResId(
                    "AppTheme.Dark.BottomSheet",
                    "style"
                )
            )
        )
            .setTitle(title)
            .setItems(
                arrayOf(download, change),
                { dialog, item ->
                    when (item) {
                        0 -> EditAvatar.downloadAvatar(
                            ctx,
                            guild,
                            user
                        )
                        1 -> EditAvatar.setAvatar(
                            manager,
                            guild,
                            user
                        )
                    }

                    dialog.dismiss()
                }
            )
            .show()

            typedValue = TypedValue()
            ctx.theme.resolveAttribute(
                Utils.getResId("colorBackgroundPrimary", "attr"),
                typedValue,
                true
            )

            dialog.window?.decorView?.setBackgroundColor(
                typedValue.data
            )
    }

    companion object {
        lateinit var mSettings: SettingsAPI
    }
}
