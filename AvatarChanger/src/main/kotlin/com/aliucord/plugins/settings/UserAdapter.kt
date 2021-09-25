package com.aliucord.plugins.settings

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.plugins.AvatarChanger
import com.discord.models.guild.Guild
import com.discord.models.user.CoreUser
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils
import com.google.gson.reflect.TypeToken

class UserAdapter(
    val ctx: Context
) : RecyclerView.Adapter<UserViewHolder>() {

    var guilds: MutableMap<Long, Guild>
    var users: MutableMap<Long, User>

    init {
        guilds = AvatarChanger.mSettings.getObject(
            "guilds",
            mutableMapOf<Long, Guild>(),
        )

        users = AvatarChanger.mSettings.getObject(
            "users",
            mutableMapOf<Long, User>(),
            TypeToken.getParameterized(
                Map::class.java, 
                Long::class.javaObjectType, 
                CoreUser::class.java
            ).getType()
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
            guild = guilds.values.asSequence().toList()
                .get(position)

            IconUtils.setIcon(holder.card.icon, guild)
        } else {
            user = users.values.asSequence().toList().get(
                    position - guilds.size
                )

            IconUtils.setIcon(holder.card.icon, user)
        }

        holder.card.name.text =
            guild?.name ?: "${user!!.username}#${user.discriminator}"

        holder.card.edit.setOnClickListener {
            Utils.openPageWithProxy(ctx, EditAvatar(guild, user))
        }

        holder.card.clear.setOnClickListener {
            val confirm = ConfirmDialog()
                .setTitle("Revert Avatar")
                .setDescription(
                    "This will revert to the original avatar. Continue?"
                )

            confirm.setOnOkListener {
                if (guild != null) {
                    guilds.remove(
                        guilds.keys.asSequence().toList()
                            .get(position)
                    )
                }

                if (user != null) {
                    users.remove(
                        users.keys.asSequence().toList()
                            .get(position)
                    )
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
