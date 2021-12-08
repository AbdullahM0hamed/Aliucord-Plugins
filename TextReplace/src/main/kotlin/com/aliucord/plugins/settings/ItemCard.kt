package com.aliucord.plugins.settings

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.views.ToolbarButton
import com.aliucord.utils.DimenUtils
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView

class ItemCard(val ctx: Context) : MaterialCardView(ctx) {

    public var text: TextView
    public var edit: ToolbarButton
    public var clear: ToolbarButton

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

        text.setSingleLine(true)
        root.addView(text)

        val buttons = LinearLayout(ctx)
        buttons.orientation = LinearLayout.HORIZONTAL
        buttons.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

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
