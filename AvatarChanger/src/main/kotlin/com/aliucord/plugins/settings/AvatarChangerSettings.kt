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
import com.discord.models.guild.Guild
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

                                            //Pain
                                            val localPage = page
                                            Utils.openPageWithProxy(
                                                view.context,
                                                localPage
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

        val userList = mutableListOf<User>()
        recycler.adapter = UserAdapter(
            view.context,
            parentFragmentManager,
            getEditedGuilds(),
            userList
        )

        val userIds = getUserIds().map { it.toLong() }
        StoreStream.getUsers().fetchUsers(userIds)

        StoreStream.getUsers().observeUsers(userIds).subscribe(
            createActionSubscriber({ users ->
                recycler.adapter = UserAdapter(
                    view.context,
                    parentFragmentManager,
                    getEditedGuilds(),
                    users.values.asSequence().toMutableList()
                )
            })
        )

        linearLayout.addView(recycler)
    }

    override fun onResume() {
        super.onResume()
        reRender()
    }

    companion object {
        public fun getGuildIds(): MutableList<String> {
            val guildIds = AvatarChanger.mSettings.getObject(
                "guilds",
                mutableListOf<String>()
            )

            return guildIds
        }

        public fun getEditedGuilds(): MutableList<Guild> {
            val guilds = StoreStream.getGuilds().getGuilds().entries
                .filter { it.key.toString() in getGuildIds() }
                .map { it.value }
                .toMutableList()

            return guilds
        }

        public fun getUserIds(): MutableList<String> {
            val userIds = AvatarChanger.mSettings.getObject(
                "users",
                mutableListOf<String>()
            )

            return userIds
        }
    }
}
