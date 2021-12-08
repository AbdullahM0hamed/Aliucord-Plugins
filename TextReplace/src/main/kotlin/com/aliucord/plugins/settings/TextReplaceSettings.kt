package com.aliucord.plugins.settings

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.TextReplace
import com.aliucord.views.Button

class TextReplaceSettings : SettingsPage() {

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("Text Replace")

        val replaceMap = TextReplace.mSettings.getObject(
            "replaceMap",
            mutableMapOf<String, String>()
        )

        Button(view.context).apply {
            text = "Add Text To Replace (Regex Supported)"
            setOnClickListener {
                val textDialog = InputDialog()
                    .setTitle("Add Text To Replace (Regex Supported)")
                    .setDescription("Enter Text To Replace")
                    .setPlaceholderText("Annoying Text")

                var toReplace = ""

                textDialog.setOnOkListener {
                    val text = textDialog.input

                    if (!text.isEmpty()) {
                        toReplace = text
                    }

                    if (!toReplace.isEmpty()) {
                        val replaceDialog = InputDialog()
                            .setTitle("Add Text To Replace With")
                            .setDescription("Add New Text")
                            .setPlaceholderText("Not Annoying Text")

                        replaceDialog.setOnOkListener {
                            val text = replaceDialog.input

                            replaceMap.put(toReplace, text)
                            TextReplace.mSettings.setObject(
                                "replaceMap",
                                replaceMap
                            )

                            reRender()
                            replaceDialog.dismiss()
                        }

                        replaceDialog.show(
                            parentFragmentManager,
                            "replace"
                        )
                    } else {
                        Utils.showToast(
                            view.context,
                            "No text to replace"
                        )
                    }

                    textDialog.dismiss()
                }

                textDialog.show(parentFragmentManager, "text")
            }
            
            linearLayout.addView(this)
        }

        val recycler = RecyclerView(view.context)
        recycler.layoutManager = LinearLayoutManager(view.context)
        recycler.adapter = ReplaceAdapter(view.context, replaceMap)

        linearLayout.addView(recycler)
    }

    override fun onResume() {
        super.onResume()
        reRender()
    }

}
