package com.aliucord.plugins.settings

import android.content.Context
import android.text.Html
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ReplaceAdapter(
    val ctx: Context,
    val replaceMap: MutableMap<String, String>,
    val reRender: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(ItemCard(ctx, reRender))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val pair = replaceMap.toList()[position]
        val card = holder.itemView as ItemCard
        card.position = position
        card.text.text = Html.fromHtml("<b>Old:</b> ${pair.first} <b>New:</b> ${pair.second}")
    }

    override fun getItemCount() = replaceMap.size
}

class ViewHolder(
    val card: ItemCard
) : RecyclerView.ViewHolder(card)
