package com.aliucord.plugins.settings

import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User

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

            setCompoundDrawableWithIntrinsicBounds(
                Utils.getResId(
                    "ic_uploads_image_dark",
                    "id"
                ),
                0,
                0,
                0
            )

            buttons.addView(this)
        }

        Button(view.context).apply {
            text = "Upload New Avatar"

            setCompoundDrawableWithIntrinsicBounds(
                Utils.getResId(
                    "ic_uploads_generic_dark",
                    "id"
                ),
                0,
                0,
                0
            )

            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }
}
