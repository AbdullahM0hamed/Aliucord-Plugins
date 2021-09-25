package com.aliucord.plugins.settings

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.plugins.AvatarChanger
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils

class UserAdapter(
    val ctx: Context
) : RecyclerView.Adapter<UserViewHolder>() {

    lateinit var guilds: mutableMapOf<Long, Guild>
    lateinit var users: mutableMapOf<Long, User>

    init {
        guilds = AvatarChanger.mSettings.getObject(
            "guilds",
            mutableMapOf<Long, Guild>
        )

        users = AvatarChanger.mSettings.getObject(
            "users",
            mutableMapOf<Long, User>
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int
    ): UserViewHolder {
        return UserViewHolder(ItemCard(ctx))
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        var guild: Guild? = null
        var user: User? = null

        if (position < guilds.size) {
            guild = guilds.get(guilds.keySet().get(position))
            IconUtils.setIcon(holder.card.icon, guild)
        } else {
            user = users.get(
                users.keySet().get(
                    position - guilds.size
                )
            )
            IconUtils.setIcon(holder.card.icon, user)
        }

        holder.card.name.text =
            guild?.name ?: "${user!!.username}#${user.discriminator}"

        holder.card.edit.setOnClickListener {
            Utils.openPageWithProxy(ctx, EditAvatar(user, guild))
        }

        holder.card.clear.setOnClickListener {
            val confirm = ConfirmDialog()
                .setTitle("Revert Avatar")
                .setDescription(
                    "This will revert to the original avatar. Continue?"
                )

            confirm.setOnOkListener {
                if (guild != null) {
                    guilds.remove(guilds.keySet().get(position))
                }

                if (user != null) {
                    users.remove(users.keySet().get(position))
                }

                AvatarChanger.mSettings.setObject("guilds", guilds)
                AvatarChanger.mSettings.setObject("users", users)

                val prefs = Utils.getAppContext()
                    .getSharedPreferences(
                        "aliucord",
                        Context.MODE_PRIVATE
                    )

                prefs.edit().remove(
                    "AC_AvatarChanger_${guild?.id ?: user!!.id}"
                ).apply()
                confirm.dismiss()
            }
        }
    }

    override fun getItemCount() = guilds.size + users.size
}

class UserViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
