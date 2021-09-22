package com.aliucord.plugins.settings

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User

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

        val name =
            guild?.name ?: "${user!!.username}#${user!!.discriminator}"
        setActionBarSubtitle(name)
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

        Utils.showToast(context, guild?.name ?: user!!.username)
        
        /*Observable.j(ObservableExtensionsKt.restSubscribeOn(
            DownloadUtils.downloadFile(
                context,
                url,
                guild?.name ?: user!!.username + ".png",
                path
            )
        ))*/

        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        val name = (guild?.name ?: user!!.username) + ".png"

        request.title = name
        request.description = "Download complete."
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            name
        )

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
        )

        val manager = context.getSystemService(
            Context.DOWNLOAD_SERVICE
        ) as DownloadManager?

        manager?.enqueue(request)
        Utils.showToast(context, "Done that")
    }
}
