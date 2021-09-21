package com.aliucord.plugins.settings

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliucord.Utils
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages

class ProfileWidget(
    val ctx: Context,
    val guild: Guild? = null,
    val user: User? = null
) : LinearLayout(ctx) {

    init {
        val inflate = LayoutInflater.from(ctx).inflate(
            Utils.getResId(
                "user_profile_header_view", 
                "layout"
            ),
            null,
            false
        )

        val constraintLayout = inflate as ConstraintLayout
        constraintLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        )

        setImage(constraintLayout)

        val username: TextView = constraintLayout.findViewById(
            Utils.getResId("username_text", "id")
        )

        username.text = guild?.name ?: user!!.username
        addView(constraintLayout)
    }

    private fun setImage(constraintLayout: ConstraintLayout) {
        val imageView: ImageView = constraintLayout.findViewById(
            Utils.getResId("avatar", "id")
        )

        if (guild != null) {
            IconUtils.setIcon(imageView, guild)
        } else {
            IconUtils.setIcon(imageView, user)
        }
    }
}
