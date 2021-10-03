package com.aliucord.plugins.settings

import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.TextReplace
import com.aliucord.views.Button

class TextReplaceSettings : SettingsPage() {

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("Text Replace")

        Button(view.context).apply {
            text = "Add Text To Replace (Regex Supported)"

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

                textDialog.dismiss()
            }

            textDialog.show(parentFragmentManager, "text")

            textDialog.setOnDismissListener {
                if (!toReplace.isEmpty()) {
                    val replaceDialog = InputDialog()
                        .setTitle("Add Text To Replace With")
                        .setDescription("Add New Text")
                        .setPlaceholderText("Not Annoying Text")

                    replaceDialog.setOnOkListener {
                        val text = replaceDialog.input

                        val replaceMap = TextReplace.mSettings
                            .getObject(
                                "replaceMap",
                                mutableMapOf<String, String>()
                            )

                        replaceMap.put(toReplace, text)
                        TextReplace.mSettings.setObject(
                            "replaceMap",
                            replaceMap
                        )

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

                linearLayout.addView(this)
            }
        }
    }
}
