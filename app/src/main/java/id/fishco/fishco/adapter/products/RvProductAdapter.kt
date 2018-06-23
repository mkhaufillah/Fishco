/*
 * Created by mkhaufillah on 6/4/18 9:49 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 9:49 AM
 */

package id.fishco.fishco.adapter.products

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import id.fishco.fishco.R
import id.fishco.fishco.model.ProductContainer
import id.fishco.fishco.adapter.loading.RvLoadingViewHolder
import id.fishco.fishco.data.Convert
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Local
import id.fishco.fishco.product.ProductActivity

class RvProductAdapter(private val context: Context, private val products: ArrayList<ProductContainer>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val listenerClick = View.OnClickListener {
        val vHolder = it.tag as RvProductViewHolder
        val idForum = "${products[vHolder.adapterPosition].id}"
        val intent = Intent(context, ProductActivity::class.java)
        intent.putExtra("type", 0)
        intent.putExtra(Credential.KEY_SHARE, idForum)
        context.startActivity(intent)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) {
            1
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.layout_product, parent, false)
        if (viewType == 0) {
            view = inflater.inflate(R.layout.layout_loading, parent, false)
            return RvLoadingViewHolder(view)
        }
        return RvProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val holder1 = holder as RvProductViewHolder
            val layoutParams1 = holder1.lnProduct?.layoutParams
            layoutParams1?.width = Local.getDeviceWidth(context)/3
            layoutParams1?.height = Local.getDeviceWidth(context) / 2
            holder1.lnProduct?.layoutParams = layoutParams1

            var name = products[position].name.toString()
            if (name.length > 12) {
                name = name.substring(0, 10) + ".."
            }

            holder1.lnProduct?.setOnClickListener(listenerClick)
            holder1.lnProduct?.tag = holder1
            holder1.tvNameProduct?.text = name
            holder1.tvPriceProduct?.text = Convert.compressIDR(products[position].price!!, context)
            holder1.tvLocationProduct?.text = products[position].address?.city.toString()
            if (products[position].photo != null) {
                for (photo in products[position].photo!!) {
                    Picasso.get()
                            .load(photo)
                            .placeholder(R.color.colorPrimaryDark)
                            .error(R.color.colorPrimaryDark)
                            .into(holder1.ivProduct)
                }
            }
        }
    }

}