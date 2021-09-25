package com.aliucord.plugins.settings

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.plugins.AvatarChanger
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.guild.Guild
import com.discord.models.user.CoreUser
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils

class UserAdapter(
    val ctx: Context
) : RecyclerView.Adapter<UserViewHolder>() {

    var guilds: MutableList<Long>
    var users: MutableList<Long>

    init {
        guilds = AvatarChanger.mSettings.getObject(
            "guilds",
            mutableListOf<Long>()
        )

        users = AvatarChanger.mSettings.getObject(
            "users",
            mutableListOf<Long>()
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
        if (position < guilds.size) {
            val guildList = StoreStream.getGuilds().getGuilds()
            guildList.map { guild ->
                if (guild.value.id == guilds.get(position)) {
                    populateView(position, holder, guild.value, null)
                }
            }
        } else {
            val id = users.get(position - guilds.size)
            StoreStream.getUsers().fetchUsers(listOf(id))
            StoreStream.getUsers().observeUser(id).subscribe(
                createActionSubscriber({ user ->
                    if (user != null) {
                        populateView(position, holder, null, user)
                    }
                })
            )
        }
    }

    override fun getItemCount() = guilds.size + users.size

    private fun populateView(
        position: Int,
        holder: UserViewHolder,
        guild: Guild?, 
        user: User?
    ) {
        if (guild != null) {
            IconUtils.setIcon(holder.card.icon, guild)
        }

        if (user != null) {
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
                    guilds.removeAt(position)
                }

                if (user != null) {
                    users.removeAt(position - guilds.size)
                }

                AvatarChanger.mSettings.setObject("guilds", guilds)
                AvatarChanger.mSettings.setObject("users", users)

                val prefs = Utils.getAppContext()
                    .getSharedPreferences(
                        "aliucord",
                        Context.MODE_PRIVATE
                    )

                prefs.edit().remove(
                    "AC_AvatarChanger_${guild?.id ?: user!!.id}"                      ).apply()
                confirm.dismiss()
            }
        }
    }
}

class UserViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
