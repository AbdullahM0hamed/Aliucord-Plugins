package com.aliucord.plugins.settings

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AvatarChanger
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.views.Button
import com.discord.models.user.User
import com.discord.stores.StoreStream
import rx.Observable

class AvatarChangerSettings : SettingsPage() {

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("Avatar Changer")

        Button(view.context).apply {
            text = "Add User/Server"
            setOnClickListener {
                val dialog = InputDialog()
                    .setTitle("Add User/Server")
                    .setDescription("Enter the User or Server's ID")
                    .setPlaceholderText("User/Server ID")

                dialog.setOnOkListener {
                    val text = dialog.input
                    var page: EditAvatar? = null

                    if (!text.isEmpty()) {
                        val id = text.toLongOrNull() ?: 0L
                        val guilds = StoreStream.getGuilds().getGuilds()

                        for (guild in guilds) {
                            if (guild.value.id == id) {
                                page = EditAvatar(guild = guild.value)
                                Utils.openPageWithProxy(
                                    view.context,
                                    page
                                )

                                dialog.dismiss()
                            }
                        }

                        if (page == null) {
                            val userList = mutableListOf(id)
                            StoreStream.getUsers().fetchUsers(userList)
                            StoreStream
                                .getUsers()
                                .observeUser(id)
                                .subscribe(
                                    createActionSubscriber({ user ->
                                        if (user != null) {
                                            page = EditAvatar(
                                                user = user
                                            )

                                            Utils.openPageWithProxy(
                                                view.context,
                                                page
                                            )
                                        }

                                        dialog.dismiss()
                                    })
                                )
                        }
                    }
                }

                dialog.show(parentFragmentManager, "addID")
            }

            linearLayout.addView(this)
        }

        val recycler = RecyclerView(view.context)
        recycler.layoutManager = LinearLayoutManager(view.context)

        val guildIds = AvatarChanger.mSettings.getObject(
            "guilds",
            mutableListOf<Long>()
        )

        //Populated when it needs to be
        Utils.showToast(view.context, guildIds.toString())

        val userIds = AvatarChanger.mSettings.getObject(
            "users",
            mutableListOf<Long>()
        )

        val guildList = StoreStream.getGuilds().getGuilds().entries
            .filter { it.key in guildIds }
            .map { it.value }
            .toMutableList()

        //Empty on restart
        //Utils.showToast(view.context, StoreStream.getGuilds().getGuilds().entries.filter { it.key in guildIds }.toString())

        //HAS THE NECESSARY KEYS
        //Utils.showToast(view.context, StoreStream.getGuilds().getGuilds().keys.toString())

        //empty on restart in any case
        //populated if you save but come back
        //and have not exited the app
        //Utils.showToast(view.context, guildList.toString())

        val userList = mutableListOf<User>()
        recycler.adapter = UserAdapter(
            view.context,
            guildList,
            userList
        )

        StoreStream.getUsers().fetchUsers(userIds)
        StoreStream.getUsers().observeUsers(userIds).subscribe(
            createActionSubscriber({ users ->
                recycler.adapter = UserAdapter(
                    view.context,
                    guildList,
                    users.values.asSequence().toMutableList()
                )
            })
        )

        linearLayout.addView(recycler)
    }
}
