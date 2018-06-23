/*
 * Created by mkhaufillah on 6/21/18 10:44 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/21/18 10:44 PM
 */

package id.fishco.fishco.adapter.transactions

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import id.fishco.fishco.R
import mehdi.sakout.fancybuttons.FancyButton

class RvTransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvTransactionBackground = view.findViewById<TextView?>(R.id.tv_transaction_background)
    val lnTransactionForeground = view.findViewById<LinearLayout?>(R.id.ln_transaction_foreground)
    val transactionTitle = view.findViewById<TextView?>(R.id.tv_transaction_title)
    val transactionExp = view.findViewById<TextView?>(R.id.tv_transaction_exp)
    val transactionQty = view.findViewById<TextView?>(R.id.tv_transaction_qty)
    val transactionDesc = view.findViewById<TextView?>(R.id.tv_transaction_desc)
    val transactionAddress = view.findViewById<TextView?>(R.id.tv_transaction_address)
    val transactionShipping = view.findViewById<TextView?>(R.id.tv_transaction_shipping)
    val transactionPay = view.findViewById<TextView?>(R.id.tv_transaction_pay)
    val transactionStatus = view.findViewById<TextView?>(R.id.tv_transaction_status)
    val transactionConfirm = view.findViewById<FancyButton?>(R.id.btn_transaction_confirm)
    val transactionUpload = view.findViewById<FancyButton?>(R.id.btn_transaction_upload)
    val transactionUploadReceipt = view.findViewById<ImageView?>(R.id.iv_transaction_upload_receipt)
    val transactionTrack = view.findViewById<FancyButton?>(R.id.btn_transaction_track)
    val transactionQuestion = view.findViewById<FancyButton?>(R.id.btn_transaction_question)
    val transactionDeliverConfirm = view.findViewById<FancyButton?>(R.id.btn_transaction_delivered_confirm)
    val transactionComplain = view.findViewById<FancyButton?>(R.id.btn_transaction_complain)
}