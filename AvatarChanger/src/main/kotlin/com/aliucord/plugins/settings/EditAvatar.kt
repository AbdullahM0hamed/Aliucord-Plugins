package com.aliucord.plugins.settings

import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.view.ViewCoroutineScopeKt
import kotlinx.coroutines.launchIn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
            setOnClickListener { downloadAvatar(view) }

            buttons.addView(this)
        }

        Button(view.context).apply {
            text = "Upload New Avatar"

            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }

    private fun downloadAvatar(view: View) {
        ViewCoroutineScopeKt.getCoroutineScope(view).launchIn {
            val url = guild?.icon ?: user!!.avatar
            val file = File(
                guild?.name ?: user!!.username + ".png",
                view.context.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS
                ).absolutePath ?: "/storage/emulated/0/Download"
            )

            if (file.exists()) {
                Utils.showToast(
                    view.context,
                    "${guild?.name ?: user!!.username}.png already downloaded"
                )
            } else {
                try {
                    val res = Http.Request(url).execute()
                    FileOutputStream(file).use { out ->
                        res.pipe(out)
                        Utils.showToast(
                            view.context,
                            "${file.absolutePath} downloaded"
                        )
                    }
                } catch (e: IOException) {
                    Utils.showToast(view.context, "Something went wrong")

                    if (file.exists()) {
                        file.delete()
                    } else {
                        // ABSOLUTE PAIN BECAUSE OF COMPILER
                    }
                } 
            }
        }
    }
}
