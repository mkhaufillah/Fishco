/*
 * Created by mkhaufillah on 6/9/18 1:51 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/9/18 1:51 PM
 */

package id.fishco.fishco.adapter.search

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import id.fishco.fishco.R

class RvSuggestionViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val tvSuggestion = view.findViewById<TextView?>(R.id.tv_suggestion)
    val tvSuggestionBackground = view.findViewById<TextView?>(R.id.tv_suggestion_background)
}