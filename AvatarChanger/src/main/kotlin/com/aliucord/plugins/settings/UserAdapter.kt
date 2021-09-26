package com.aliucord.plugins.settings

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.plugins.AvatarChanger
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.stores.StoreStream
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
            populateView(position, holder, guilds.get(position), null)
        } else {
            populateView(
                position,
                holder,
                null,
                users.get(position - guilds.size)
            )
        }
    }

    override fun getItemCount() = guilds.size + users.size

    private fun populateView(
        position: Int,
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

        card.clear.setOnClickListener {
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

                AvatarChanger.mSettings.setObject(
                    "guilds", 
                    guilds.map { it.id.toString() }
                )
                AvatarChanger.mSettings.setObject(
                    "users", 
                    users.map { it.id.toString() }
                )

                val prefs = Utils.getAppContext()
                    .getSharedPreferences(
                        "aliucord",
                        Context.MODE_PRIVATE
                    )

                prefs.edit().remove(
                    "AC_AvatarChanger_${guild?.id ?: user!!.id}"                      ).apply()
                confirm.dismiss()
            }

            confirm.show(manager, "confirm")
        }
    }
}

class ViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
