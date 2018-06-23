/*
 * Created by mkhaufillah on 6/4/18 10:43 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 9:58 AM
 */

package id.fishco.fishco.adapter.loading

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import id.fishco.fishco.R

class RvLoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val pbLoading = view.findViewById<ProgressBar?>(R.id.pb_loading)
}