/*
 * Created by mkhaufillah on 6/10/18 8:03 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 9:48 AM
 */

package id.fishco.fishco.adapter.banners

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ss.com.bannerslider.ImageLoadingService

class PicassoImageLoadingServiceBanner(val context: Context) : ImageLoadingService {

    override fun loadImage(url: String, imageView: ImageView) {
        Picasso.get().load(url).into(imageView)
    }

    override fun loadImage(resource: Int, imageView: ImageView) {
        Picasso.get().load(resource).into(imageView)
    }

    override fun loadImage(url: String, placeHolder: Int, errorDrawable: Int, imageView: ImageView) {
        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView)
    }
}