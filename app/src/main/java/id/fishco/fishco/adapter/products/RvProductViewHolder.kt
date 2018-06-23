/*
 * Created by mkhaufillah on 6/4/18 9:49 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 9:49 AM
 */

package id.fishco.fishco.adapter.products

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import id.fishco.fishco.R

class RvProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val lnProduct = view.findViewById<LinearLayout?>(R.id.ln_product)
    val ivProduct = view.findViewById<ImageView?>(R.id.iv_product)
    val tvNameProduct = view.findViewById<TextView?>(R.id.tv_name_product)
    val tvPriceProduct = view.findViewById<TextView?>(R.id.tv_price_product)
    val tvLocationProduct = view.findViewById<TextView?>(R.id.tv_location_product)
}