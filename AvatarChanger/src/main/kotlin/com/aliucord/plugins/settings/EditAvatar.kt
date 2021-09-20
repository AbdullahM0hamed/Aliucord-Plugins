package com.aliucord.plugins.settings

import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.discord.models.guild.Guild
import com.discord.models.user.User

data class EditAvatar(
    val guild: Guild? = null,
    val user: User? = null
) : SettingsPage() {

    override fun onViewBound(view: View) {
        if (guild == null and user == null) {
            Utils.showToast(view.context, "Invalid User/Server")
            activity.onBackPressed()
            return
        }

        setActionBarTitle("Edit Avatar")
        setActionBarSubTitle(guild?.name ?: user!!.userName)
    }
}
