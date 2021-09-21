package com.aliucord.plugins.settings

import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.lytefast.flexinput.R

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
        buttons.orientation = LineaLayout.VERTICAL

        Button(view.context).apply {
            text = "Download Current Avatar"
            setCompoundDrawableWithIntrinsicBounds {
                R.d.ic_uploads_image_dark,
                0,
                0,
                0
            }
            setOnClickListener { 
                //TODO: Add code here
            }

            buttons.addView(this)
        }

        Button(view.context).apply {
            text = "Upload New Avatar"
            setCompoundDrawableWithIntrinsicBounds {
                R.d.ic_uploads_generic_dark,
                0,
                0,
                0
            }
            setOnClickListener {
                //TODO: Do this as well
            }

            buttons.addView(this)
        }

        linearLayout.addView(buttons)
    }
}
