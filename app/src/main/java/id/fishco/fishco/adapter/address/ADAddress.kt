/*
 * Created by mkhaufillah on 6/14/18 5:19 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/14/18 5:19 PM
 */

package id.fishco.fishco.adapter.address

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import id.fishco.fishco.R
import id.fishco.fishco.model.AddressContainer

class ADAddress(context: Context, data: List<AddressContainer>) :
        ArrayAdapter<AddressContainer>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: VhAddress
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_address, parent, false)
            holder = VhAddress(view)
            view.tag = holder
        } else {
            holder = view.tag as VhAddress
        }
        val partData = getItem(position)
        holder.titleAddress?.text = partData.name
        val address = partData.street + ", " +
                partData.village + ", " +
                partData.subdistrict + ", " +
                partData.city + ", " +
                partData.province + ", " +
                partData.regional + ", " +
                partData.postal

        holder.descAddress?.text = address
        return view!!
    }

}