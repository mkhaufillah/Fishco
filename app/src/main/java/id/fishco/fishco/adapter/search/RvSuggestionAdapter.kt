/*
 * Created by mkhaufillah on 6/9/18 1:50 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/9/18 1:50 PM
 */

package id.fishco.fishco.adapter.search

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.fishco.fishco.R
import id.fishco.fishco.data.Credential
import id.fishco.fishco.model.Suggestion
import id.fishco.fishco.product.searchProduct.SearchProductActivity

class RvSuggestionAdapter(private val context: Context, private val suggestions: ArrayList<Suggestion>): RecyclerView.Adapter<RvSuggestionViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val listenerClick = View.OnClickListener {
        val vHolder = it.tag as RvSuggestionViewHolder
        val value = "${suggestions[vHolder.adapterPosition].text}"
        val intent = Intent(context, SearchProductActivity::class.java)
        intent.putExtra("type", 0)
        intent.putExtra(Credential.KEY_SHARE, value)
        context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSuggestionViewHolder {
        val view = inflater.inflate(R.layout.layout_suggestion, parent, false)
        return RvSuggestionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun onBindViewHolder(holder: RvSuggestionViewHolder, position: Int) {
        holder.tvSuggestion?.text = suggestions[position].text
        holder.tvSuggestion?.setOnClickListener(listenerClick)
        holder.tvSuggestion?.tag = holder
    }

}