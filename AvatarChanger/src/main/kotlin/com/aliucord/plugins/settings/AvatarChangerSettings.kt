package com.aliucord.plugins.settings

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.views.Button
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
        recycler.adapter = UserAdapter(view.context)

        val guildIds = AvatarChanger.mSettings.getObject(
            "guilds",
            mutableListOf<Long>()
        )

        val userIds = AvatarChanger.mSettings.getObject(
            "users",
            mutableListOf<Long>()
        )

        val guildList = StoreStream.getGuilds().getGuilds().entries
            .filter { it.key in guilds }
            .map { it.value }

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
                    users.values.asSequence().toList()
                )
            })
        )

        linearLayout.addView(recycler)
    }
}
