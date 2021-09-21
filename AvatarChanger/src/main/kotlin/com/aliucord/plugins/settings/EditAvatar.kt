package com.aliucord.plugins.settings

import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.file.DownloadUtils

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
        setActionBarSubtitle(guild?.name ?: user!!.username)
        linearLayout.addView(ProfileWidget(
            ctx=view.context, 
            guild=guild, 
            user=user
        ))

        val buttons = LinearLayout(view.context)
        buttons.orientation = LinearLayout.VERTICAL

        Button(view.context).apply {
            text = "Download Current Avatar"
            setOnClickListener {
                DownloadUtils.downloadFile(
                    view.context,
                    guild?.icon ?: user!!.avatar,
                    guild?.name ?: user!!.username + '.png',
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                )
            }

            buttons.addView(this)
        }

        Button(view.context).apply {
            text = "Upload New Avatar"

            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }
}
