/*
 * Created by mkhaufillah on 6/17/18 2:25 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/17/18 2:25 PM
 */

package id.fishco.fishco.adapter.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import id.fishco.fishco.R
import id.fishco.fishco.model.Payment

class ADPayMethod(context: Context, data: List<Payment>)
    : ArrayAdapter<Payment>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: VhPayMethod
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_pay_method, parent, false)
            holder = VhPayMethod(view)
            view.tag = holder
        } else {
            holder = view.tag as VhPayMethod
        }
        val partData = getItem(position)
        holder.tittleBank.text = partData.name
        val desc = partData.receiver + "\n" +
                partData.rekening + "\n" +
                partData.location

        holder.descBank.text = desc
        Picasso.get()
                .load(partData.photo.toString())
                .placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark)
                .into(holder.bank)
        return view!!
    }

}