package com.aliucord.plugins.settings

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User

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
            guild?.name ?: "${user!!.username}#${user!!.discriminator}"
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
            setOnClickListener { downloadAvatar() }
            buttons.addView(this)
        }

        Button(ctx).apply {
            text = "Change Avatar"
            setOnClickListener { setAvatar() }
            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }

    val url = "https://cdn.discordapp.com/avatars/${guild?.id ?: user!!.id}/${guild?.icon ?: user!!.avatar}.png?size=1024"

    private fun downloadAvatar() {
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        val name = (guild?.name ?: user!!.username) + ".png"

        request.setTitle(name)
        request.setDescription("Download complete.")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            name
        )

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
        )

        val manager = ctx.getSystemService(
            Context.DOWNLOAD_SERVICE
        ) as DownloadManager?

        manager?.enqueue(request)
    }

    private fun setAvatar() {
        val dialog = InputDialog()
            .setTitle("Set Avatar URL")
            .setDescription("Link to new image to use for avatar")
            .setPlaceholderText(url)

        dialog.inputLayout.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, 
                    start: Int, 
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?, 
                    start: Int, 
                    before: Int, 
                    count: Int
                ) {}

                override fun afterTextChanged(_editable: Editable?) {
                    if (!validate(dialog)) {
                        dialog.setDescription(
                            "Link to new avatar image [INVALID]"
                        )
                    } 
                }
            }
        )

        dialog.setOnOkListener {
           if (!valdiate(dialog)) {
               Utils.showToast(ctx, "Invalid URL")
           } else {
               Utils.showToast(ctx, "Test toast")
           }
       }

        dialog.show(parentFragmentManager, "setAvatar")
    }

    private fun validate(dialog: InputDialog): Boolean {
        return Patterns.WEB_URL.matcher(dialog.input).matches()
    }
}
