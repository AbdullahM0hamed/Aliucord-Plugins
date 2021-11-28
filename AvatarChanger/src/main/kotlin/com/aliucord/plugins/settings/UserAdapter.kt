package com.aliucord.plugins.settings

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.plugins.AvatarChanger
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.icon.IconUtils

class UserAdapter(
    val ctx: Context,
    val manager: FragmentManager,
    val guilds: MutableList<Guild>,
    val users: MutableList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(ItemCard(ctx))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (position < guilds.size) {
            populateView(holder, guilds.get(position), null)
        } else {
            populateView(
                holder,
                null,
                users.get(position - guilds.size)
            )
        }
    }

    override fun getItemCount() = guilds.size + users.size

    private fun populateView(
        holder: RecyclerView.ViewHolder,
        guild: Guild?,
        user: User?
    ) {
        val card = holder.itemView as ItemCard

        if (guild != null) {
            IconUtils.setIcon(card.icon, guild)
        }

        if (user != null) {
            IconUtils.setIcon(card.icon, user)
        }

        card.name.text =
            guild?.name ?: "${user!!.username}#${user.discriminator}"

        card.edit.setOnClickListener {
            Utils.openPageWithProxy(ctx, EditAvatar(guild, user))
        }

        card.clear.setOnClickListener { removeDialog(guild, user, manager, this) }
    }

    companion object {
        public fun removeDialog(
            guild: Guild?,
            user: User?,
            manager: FragmentManager,
            adapter: RecyclerView.Adapter<*>? = null
        ) {
            val confirm = ConfirmDialog()
                .setTitle("Revert Avatar")
                .setDescription(
                    "This will revert to the original avatar. Continue?"
                )

            val guilds = AvatarChangerSettings.getEditedGuilds()
            val userIds = AvatarChangerSettings.getUserIds()

            confirm.setOnOkListener {
                if (guild != null) {
                    guilds.remove(guild)
                }

                if (user != null) {
                    userIds.remove(user.id.toString())
                }

                AvatarChanger.mSettings.setObject(
                    "guilds",
                    guilds.map { it.id.toString() }
                )
                AvatarChanger.mSettings.setObject(
                    "users",
                    userIds
                )

                val prefs = Utils.appContext.getSharedPreferences(
                        "aliucord",
                        Context.MODE_PRIVATE
                    )

                prefs.edit().remove(
                    "AC_AvatarChanger_${guild?.id ?: user!!.id}"
                ).apply()

                adapter?.notifyDataSetChanged()
                confirm.dismiss()
            }

            confirm.show(manager, "confirm")
        }
    }
}

class ViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
