package com.aliucord.plugins.settings

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.plugins.TextReplace
import com.aliucord.views.ToolbarButton
import com.aliucord.utils.DimenUtils
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView

class ItemCard(
    val ctx: Context,
    val reRender: () -> Unit
) : MaterialCardView(ctx) {

    public var text: TextView
    public var edit: ToolbarButton
    public var clear: ToolbarButton
    public var position = 0

    init {
        setRadius(DimenUtils.defaultCardRadius.toFloat())
        setCardBackgroundColor(
            ColorCompat.getThemedColor(
                ctx,
                Utils.getResId("colorBackgroundSecondary", "attr")
            )
        )

        val rootParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val p = DimenUtils.dpToPx(16)
        rootParams.setMargins(0, p / 2, 0, 0)
        layoutParams = rootParams

        val root = LinearLayout(ctx)
        root.orientation = LinearLayout.HORIZONTAL
        root.layoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        root.gravity = Gravity.CENTER_VERTICAL
        root.setPadding(p, p, p, p)

        text = TextView(ctx)
        val params = LinearLayout.LayoutParams(
            DimenUtils.dpToPx(110),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(p, 0, 0, 0)
        text.layoutParams = params
        text.textSize = 16f

        text.setTextColor(
            ColorCompat.getThemedColor(
                ctx,
                Utils.getResId("colorInteractiveNormal", "attr")
            )
        )

        text.setSingleLine(false)
        root.addView(text)

        val buttons = LinearLayout(ctx)
        buttons.orientation = LinearLayout.HORIZONTAL
        val btnParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btnParams.setMarginEnd(DimenUtils.dpToPx(16))
        buttons.layoutParams = btnParams

        buttons.setHorizontalGravity(Gravity.END)
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL)

        val p2 = p / 2

        edit = ToolbarButton(ctx).apply {
            setPadding(p2, p2, p2, p2)
            val editParams = LinearLayout.LayoutParams(
                DimenUtils.dpToPx(40),
                DimenUtils.dpToPx(40)
            )

            setLayoutParams(editParams)
            setOnClickListener {
                val replaceMap = TextReplace.mSettings.getObject(
                    "replaceMap",
                    mutableMapOf<String, String>()
                )

                val textDialog = InputDialog()
                    .setTitle("Add Text To Replace With")
                    .setDescription("Enter Text To Replace With")
                    .setPlaceholderText("New Text")
                    .setOnOkListener {
                        if (!input.isEmpty()) {
                            replaceMap[replaceMap.toList()[position].first] = input
                            TextReplace.mSettings.setObject(
                                "replaceMap",
                                replaceMap
                            )
                        }
                        reRender()
                        dismiss()
                    }
            }

            val editIcon = ContextCompat.getDrawable(
                ctx,
                Utils.getResId("ic_edit_24dp", "drawable")
            )?.mutate()?.let {
                Utils.tintToTheme(it)
                setImageDrawable(it)
            }

            buttons.addView(this)
        }

        clear = ToolbarButton(ctx).apply {
            setPadding(p2, p2, p2, p2)
            val clearParams = LinearLayout.LayoutParams(
                DimenUtils.dpToPx(40), 
                DimenUtils.dpToPx(40)
            )
            setLayoutParams(clearParams)
            setOnClickListener {
                val replaceMap = TextReplace.mSettings.getObject(
                    "replaceMap",
                    mutableMapOf<String, String>()
                )

                replaceMap.remove(replaceMap.toList()[position].first)

                TextReplace.mSettings.setObject(
                    "replaceMap",
                    replaceMap
                 )

                 reRender()
            }
            val clearIcon = ContextCompat.getDrawable(
                ctx, 
                Utils.getResId("ic_clear_24dp", "drawable")
            )?.mutate()?.let {
                Utils.tintToTheme(it)
                setImageDrawable(it)
            }
            
            buttons.addView(this)
        }

        root.addView(buttons)
        addView(root)
    }
}
