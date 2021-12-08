package com.aliucord.plugins.settings

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

class ReplaceAdapter(
    val ctx: Context,
    val replaceMap: MutableMap<String, String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(ItemCard(ctx))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val pair = replaceMap.toList()[position]
        val card = holder.itemView as ItemCard
        card.text.text = Html.fromHtml("<b>Old:</b> ${pair[0]} <b>New:</b> ${pair[1]}")
    }
}

class ViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
