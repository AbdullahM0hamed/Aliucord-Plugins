package com.aliucord.plugins.settings

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.ToolbarButton
import com.discord.utilities.color.ColorCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

class ItemCard(
    val ctx: Context
) : MaterialCardView(ctx) {

    public var icon: SimpleDraweeView
    public var name: TextView
    public var edit: Button
    public var clear: ToolbarButton

    init {
        setRadius(DimenUtils.getDefaultCardRadius().toFloat())
        setCardBackgroundColor(
            ColorCompat.getThemedColor(
                ctx,
                R.b.colorBackgroundSecondary
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

        icon = SimpleDraweeView(ctx)
        icon.layoutParams = 
            LayoutParams(
                DimenUtils.dpToPx(38),
                DimenUtils.dpToPx(38)
            )

        root.addView(icon)

        name = TextView(ctx)
        val params = LinearLayout.LayoutParams(
            DimenUtils.dpToPx(110),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(p, 0, 0, 0)
        name.layoutParams = params
        name.textSize = 16f

        name.setTextColor(
            ColorCompat.getThemedColor(
                ctx,
                R.b.colorInteractiveNormal
            )
        )

        name.setSingleLine(false)

        root.addView(name)

        val buttons = LinearLayout(ctx)
        buttons.orientation = LinearLayout.HORIZONTAL
        buttons.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        buttons.setHorizontalGravity(Gravity.END)
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL)

        edit = Button(ctx)
        edit.setText("Edit")
        val editParams = LinearLayout.LayoutParams(
            DimenUtils.dpToPx(103),
            DimenUtils.dpToPx(48)
        )

        val p2 = p / 2
        editParams.setMargins(0, 0, p2, 0)
        edit.layoutParams = editParams
        buttons.addView(edit)

        clear = ToolbarButton(ctx);
        clear.setPadding(p2, p2, p2, p2)
        val clearParams = LinearLayout.LayoutParams(
            DimenUtils.dpToPx(40), 
            DimenUtils.dpToPx(40)
        )
        clear.layoutParams = clearParams
        val clearIcon = ContextCompat.getDrawable(
            ctx, 
            R.d.ic_clear_24dp
        )
        clearIcon?.setTint(0xFFED4245.toInt())
        clear.setImageDrawable(clearIcon)
        buttons.addView(clear)

        root.addView(buttons)
        addView(root)
    }
}
