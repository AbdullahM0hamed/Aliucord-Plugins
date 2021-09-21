package com.aliucord.plugins

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliucord.Utils
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages

class ProfileWidget(
    val context: Context,
    val guild: Guild? = null,
    val user: User? = null
) : LinearLayout(context) {

    init {
        val inflate = LayoutInflater.from(context).inflate(
            Utils.getResId(
                "user_profile_header_view", 
                "layout", 
                null,
                false
            )
        )

        val constraintLayout = inflate as ConstraintLayout
        constraintLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.WRAP_CONTENT
        )

        setImage(constraintLayout)

        val username = constraintLayout.findViewById(
            Utils.getResId("username_text", "id")
        )

        username.text = guild?.name ?: user!!.username
        addView(constraintLayout)
    }

    private fun setImage(constraintLayout: ConstraintLayout) {
        val imageView = constraintLayout.findViewById(
            Utils.getResId("avatar", "id")
        )

        IconUtils.setIcon(imageView, guild ?: user)
    }
}
