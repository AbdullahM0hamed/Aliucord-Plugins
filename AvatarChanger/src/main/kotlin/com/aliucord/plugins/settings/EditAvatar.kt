package com.aliucord.plugins.settings

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AvatarChanger
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils

data class EditAvatar(
    val guild: Guild? = null,
    val user: User? = null
) : SettingsPage() {

    lateinit var ctx: Context

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        ctx = view.context

        if (guild == null && user == null) {
            Utils.showToast(ctx, "Invalid User/Server")
            activity?.onBackPressed()
            return
        }

        setActionBarTitle("Edit Avatar")

        val name =
            guild?.name ?: "${user!!.username}#${user.discriminator}"
        setActionBarSubtitle(name)
        linearLayout.addView(
            ProfileWidget(
                ctx = ctx,
                guild = guild,
                user = user
            )
        )

        val buttons = LinearLayout(ctx)
        buttons.orientation = LinearLayout.VERTICAL

        Button(ctx).apply {
            text = "Download Current Avatar"
            setOnClickListener { downloadAvatar(ctx, guild, user) }
            buttons.addView(this)
        }

        Button(ctx).apply {
            text = "Change Avatar"
            setOnClickListener {
                setAvatar(ctx, manager, guild, user)
            }
            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }

    companion object {
        public fun downloadAvatar(
            ctx: Context,
            guild: Guild?,
            user: User?
        ) {
            var url = ""

            if (guild != null) {
                url = IconUtils.getForGuild(guild)
            }

            if (user != null) {
                url = IconUtils.getForUser(user)
            }

            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            val name = (guild?.name ?: user!!.username) + ".png"

            request.setTitle(name)
            request.setDescription("Download complete.")
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                name
            )

            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
            )

            val manager = ctx.getSystemService(
                Context.DOWNLOAD_SERVICE
            ) as DownloadManager?

            manager?.enqueue(request)
        }

        public fun setAvatar(
            ctx: Context,
            manager: FragmentManager,
            guild: Guild?,
            user: User?
        ) {
            val dialog = InputDialog()
                .setTitle("Set Avatar URL")
                .setDescription("Link to new image to use for avatar")
                .setPlaceholderText("http://site.com/image.png")

            dialog.setOnOkListener {
                val url = dialog.input

                if (!Patterns.WEB_URL.matcher(url).matches()) {
                    Utils.showToast(ctx, "Invalid URL")
                } else {
                    AvatarChanger.mSettings.setString(
                        (guild?.id ?: user!!.id).toString(),
                        url
                    )

                    if (guild != null) {
                        val guilds = AvatarChanger.mSettings.getObject(
                            "guilds",
                            mutableListOf<String>()
                        )

                        val guildFound = guilds.find {
                            it.toLong() == guild.id
                        }

                        if (guildFound == null) {
                            guilds.add(guild.id.toString())
                            AvatarChanger.mSettings.setObject(
                                "guilds",
                                guilds
                            )
                        }
                    } else if (user != null) {
                        val users = AvatarChanger.mSettings.getObject(
                            "users",
                            mutableListOf<String>()
                        )

                        val userFound = users.find {
                            it.toLong() == user.id
                        }

                        if (userFound == null) {
                            users.add(user.id.toString())
                            AvatarChanger.mSettings.setObject(
                                "users",
                                users
                            )
                        }
                    }
                }

                dialog.dismiss()
            }

            dialog.show(manager, "setAvatar")
        }
    }
}
