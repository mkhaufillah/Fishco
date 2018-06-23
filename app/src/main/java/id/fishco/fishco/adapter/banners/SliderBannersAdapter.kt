/*
 * Created by mkhaufillah on 5/30/18 6:03 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/30/18 6:03 PM
 */

package id.fishco.fishco.adapter.banners

import id.fishco.fishco.model.BannerContainer
import ss.com.bannerslider.viewholder.ImageSlideViewHolder
import ss.com.bannerslider.adapters.SliderAdapter

class SliderBannersAdapter(val data: ArrayList<BannerContainer>) : SliderAdapter() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindImageSlide(position: Int, viewHolder: ImageSlideViewHolder) {
        viewHolder.bindImageSlide(data[position].url)
    }
}