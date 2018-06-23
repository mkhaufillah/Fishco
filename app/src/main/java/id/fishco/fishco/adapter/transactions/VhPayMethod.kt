/*
 * Created by mkhaufillah on 6/17/18 2:25 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/17/18 2:25 PM
 */

package id.fishco.fishco.adapter.transactions

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import id.fishco.fishco.R

class VhPayMethod(view: View) {
    val bank = view.findViewById<ImageView>(R.id.iv_bank)
    val tittleBank = view.findViewById<TextView>(R.id.tv_tittle_bank)
    val descBank = view.findViewById<TextView>(R.id.tv_desc_bank)
}