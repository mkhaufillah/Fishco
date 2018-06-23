/*
 * Created by mkhaufillah on 6/5/18 9:36 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/5/18 9:53 AM
 */

package id.fishco.fishco.adapter.banners

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import id.fishco.fishco.R
import id.fishco.fishco.adapter.loading.RvLoadingViewHolder
import id.fishco.fishco.model.BannerContainer

class RvBannerAdapter(private val context: Context, private val banners: ArrayList<BannerContainer>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return if (position < banners.size) {
            1
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.layout_banner, parent, false)
        if (viewType == 0) {
            view = inflater.inflate(R.layout.layout_loading, parent, false)
            return RvLoadingViewHolder(view)
        }
        return RvBannerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return banners.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val holder1 = holder as RvBannerViewHolder

            holder1.tvName?.text = banners[position].name.toString()
            holder1.tvDesc?.text = banners[position].desc.toString()
            holder1.tvExpired?.text = banners[position].expired.toString()
            if (banners[position].url != null) {
                Picasso.get()
                        .load(banners[position].url)
                        .placeholder(R.color.colorPrimaryDark)
                        .error(R.color.colorPrimaryDark)
                        .into(holder1.ivBanner)
            }
        }
    }

}