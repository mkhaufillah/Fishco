/*
 * Created by mkhaufillah on 6/14/18 7:18 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/14/18 7:18 PM
 */

package id.fishco.fishco.adapter.address

import android.view.View
import android.widget.TextView
import id.fishco.fishco.R

class VhAddress(view: View) {
    val titleAddress = view.findViewById<TextView?>(R.id.tv_title_address)
    val descAddress = view.findViewById<TextView?>(R.id.tv_desc_address)
}