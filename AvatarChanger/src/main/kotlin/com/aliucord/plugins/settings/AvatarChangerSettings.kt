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
                                .observeAllUsers()
                                .subscribe(
                                    createActionSubscriber({ users ->
                                        val user = users.get(id)

                                        if (user != null) {
                                            page = EditAvatar(
                                                user = user
                                            )

                                            Utils.openPageWithProxy(
                                                view.context,
                                                page
                                            )
                                        
                                            this.unsubscribe()
                                            dialog.dismiss()
                                        },
                                        {
                                            Utils.showToast(
                                                ctx,
                                                "An error occurred!"
                                            )
                                        },
                                        {
                                            Utils.showToast(
                                                ctx,
                                                "User doesn't exist"
                                            )
                                        }
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
