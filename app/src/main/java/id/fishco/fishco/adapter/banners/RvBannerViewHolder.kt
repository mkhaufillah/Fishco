/*
 * Created by mkhaufillah on 6/5/18 9:37 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 10:56 AM
 */

package id.fishco.fishco.adapter.banners

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import id.fishco.fishco.R

class RvBannerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val ivBanner = view.findViewById<ImageView?>(R.id.iv_banner)
    val tvName = view.findViewById<TextView?>(R.id.tv_name)
    val tvExpired = view.findViewById<TextView?>(R.id.tv_expired)
    val tvDesc = view.findViewById<TextView?>(R.id.tv_desc)
}