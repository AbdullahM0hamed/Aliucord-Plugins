package com.aliucord.plugins.settings

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

    public lateinit var icon: SimpleDraweeView
    public lateinit var name: TextView
    public lateinit var edit: Button
    public lateinit var clear: ToolbarButton

    init {
        setRadius(DimenUtils.getDefaultCardRadius())
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
                Utils.dpToPx(38),
                Utils.dpToPx(38)
            )

        root.addView(icon)

        name = TextView(ctx)
        val params = LinearLayout.LayoutParams(
            Utils.dpToPx(110),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(p, 0, 0, 0)
        name.layoutParams = params
        name.textSize = 16f
        name.typeFace =
            ResourcesCompat.getFont(
                ctx,
                Constants.Fonts.whitney_bold
            )

        name.textColor =
            ColorCompat.getThemedColor(
                ctx,
                R.b.colorInteractiveNormal
            )

        name.singleLine = false

        root.addView(name)

        val buttons = LinearLayout(ctx)
        buttons.orientation = LinearLayout.HORIZONTAL
        buttons.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        buttons.horizontalGravity = Gravity.END
        buttons.verticalGravity = Gravity.CENTER_VERTICAL

        edit = Button(ctx)
        edit.setText("Edit")
        val editParams = LinearLayout.LayoutParams(
            Utils.dpToPx(103),
            Utils.dpToPx(48)
        )
        editParams.setMargins(0, 0, p2, 0)
        edit.layoutParams = editParams
        buttons.addView(edit)

        clear = ToolbarButton(ctx);
        clear.setPadding(p2, p2, p2, p2)
        val clearParams = LinearLayout.LayoutParams(
            Utils.dpToPx(40), 
            Utils.dpToPx(40)
        )
        clear.layoutParams = clearParams
        val clearIcon = ContextCompat.getDrawable(
            ctx, 
            R.d.ic_clear_24dp
        )
        clearIcon.tint = 0xFFED4245
        clear.setImageDrawable(clearIcon)
        buttons.addView(clear)

        root.addView(buttons)
        addView(root)
    }
}
