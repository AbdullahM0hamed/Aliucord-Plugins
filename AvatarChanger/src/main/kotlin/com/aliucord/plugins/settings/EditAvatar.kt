package com.aliucord.plugins.settings

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.file.DownloadUtils

import java.io.File

data class EditAvatar(
    val guild: Guild? = null,
    val user: User? = null
) : SettingsPage() {

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        if (guild == null && user == null) {
            Utils.showToast(view.context, "Invalid User/Server")
            activity?.onBackPressed()
            return
        }

        setActionBarTitle("Edit Avatar")
        setActionBarSubtitle(guild?.name ?: user!!.username + user!!.discriminator)
        linearLayout.addView(
            ProfileWidget(
                ctx = view.context,
                guild = guild,
                user = user
            )
        )

        val buttons = LinearLayout(view.context)
        buttons.orientation = LinearLayout.VERTICAL

        Button(view.context).apply {
            text = "Download Current Avatar"
            setOnClickListener { downloadAvatar(view.context) }

            buttons.addView(this)
        }

        Button(view.context).apply {
            text = "Upload New Avatar"

            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }

    private fun downloadAvatar(context: Context) {
        val path = context.getExternalFilesDir(
            Environment.DIRECTORY_DOWNLOADS
        )

        val url = "https://cdn.discordapp.com/avatars/${guild?.id ?: user!!.id}/${guild?.icon ?: user!!.avatar}.png"

        Utils.showToast(context, url)
        DownloadUtils.downloadFile(
            context,
            url,
            guild?.name ?: user!!.username + ".png",
            path
        )   
    }
}
