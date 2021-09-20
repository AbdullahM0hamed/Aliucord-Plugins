package com.aliucord.plugins.settings

import android.view.View
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
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
                    
                    if (!text.isEmpty()) {
                        val id = text.toLong()
                        val guilds = StoreStream.getGuilds().getGuilds()

                        for (guild in guilds) {
                            if (guild.id == id) {
                                EditAvatar(guild=guild)
                                dialog.dismiss()
                            }
                        }

                        val users = StoreStream.getUsers().fetchUsers(mutableListOf(id))
                        if (users.size > 0) {
                            EditAvatar(user=users[0])
                        }
                    }
                }
                
                dialog.show(parentFragmentManager, "addID")
            }

            linearLayout.addView(this)
        }
    }
}

