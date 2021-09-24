package com.aliucord.plugins.settings

import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.views.Button
import com.discord.stores.StoreStream

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
    }
}
