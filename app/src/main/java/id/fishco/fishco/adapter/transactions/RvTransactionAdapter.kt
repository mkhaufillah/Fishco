/*
 * Created by mkhaufillah on 6/21/18 10:44 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/21/18 10:44 PM
 */

package id.fishco.fishco.adapter.transactions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.transactions.ImageUploadContainerActivity
import id.fishco.fishco.adapter.helper.TimerCustom
import id.fishco.fishco.adapter.loading.RvLoadingViewHolder
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Tag
import id.fishco.fishco.model.Payment
import id.fishco.fishco.model.ProductContainer
import id.fishco.fishco.model.TransactionContainer
import id.fishco.fishco.payment.PaymentActivity
import id.fishco.fishco.transactions.TransactionsFragment
import java.text.NumberFormat
import java.util.*

class RvTransactionAdapter(private val fragment: TransactionsFragment, private val activity: Activity, private val context: Context, private val transactions: ArrayList<TransactionContainer>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val db = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private val collectionP = db.collection("products")
    private val collectionT = db.collection("transactions")
    private val localeID = Locale("in", "ID")
    private val priceID = NumberFormat.getCurrencyInstance(localeID)

    private val listenerConfirm = View.OnClickListener {
        val vHolder = it.tag as RvTransactionViewHolder

        val refBank = db.collection("payment")
        refBank.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document.isEmpty) {
                    Log.d(Tag.TAG_INFORMATION, "No such document")
                    Toasty.error(context, context.getString(R.string.no_document)).show()
                } else {
                    val payment = ArrayList<Payment>()
                    for (doc in document) {
                        val paymentObj = doc.toObject(Payment::class.java)
                        paymentObj.id = doc.id
                        payment.add(paymentObj)
                    }
                    val adapter = ADPayMethod(context, payment)
                    LovelyChoiceDialog(context)
                            .setTopTitle(R.string.pay_method)
                            .setTopColorRes(R.color.colorPrimary)
                            .setItems(adapter, { _, item ->
                                val date = Date()

                                val totalCost = transactions[vHolder.adapterPosition].totalCost
                                val idTransaction = transactions[vHolder.adapterPosition].id
                                val intent = Intent(context, PaymentActivity::class.java)
                                intent.putExtra(Credential.KEY_SHARE ,item.id)
                                intent.putExtra(Credential.KEY_SHARE_ALT1, totalCost)
                                intent.putExtra(Credential.KEY_SHARE_ALT2, date.time+3600000)
                                context.startActivity(intent)

                                collectionT.document(idTransaction!!)
                                        .update("timestamp", date,
                                                "exp", Date(date.time+3600000),
                                                "status", 0,
                                                "payMethod", item.id)

                                val timer = TimerCustom(
                                        transactions[vHolder.adapterPosition].exp!!.time - Date().time,
                                        1000,
                                        vHolder.transactionExp!!,
                                        activity)
                                timer.start()

                                vHolder.transactionStatus?.text = context.getString(R.string.t01)
                                vHolder.transactionConfirm?.visibility = View.GONE
                                vHolder.transactionUpload?.visibility = View.VISIBLE
                                vHolder.transactionTrack?.visibility = View.GONE
                                vHolder.transactionQuestion?.visibility = View.VISIBLE
                                vHolder.transactionDeliverConfirm?.visibility = View.GONE
                                vHolder.transactionComplain?.visibility = View.GONE
                            }).show()
                }
            } else {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                Toasty.error(context, context.getString(R.string.error)).show()
            }
        }
    }

    private val listenerUpload = View.OnClickListener {
        val vHolder = it.tag as RvTransactionViewHolder

        val intent = Intent(context, ImageUploadContainerActivity::class.java)
        intent.putExtra(Credential.KEY_SHARE, transactions[vHolder.adapterPosition].id)
        fragment.startActivityForResult(intent, Credential.CODE_UPLOAD)
    }

    init {
        db.firestoreSettings = settings
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < transactions.size) {
            1
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.layout_transaction, parent, false)
        if (viewType == 0) {
            view = inflater.inflate(R.layout.layout_loading, parent, false)
            return RvLoadingViewHolder(view)
        }
        return RvTransactionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val holder1 = holder as RvTransactionViewHolder
            val docP = collectionP.document(transactions[position].product!!)
            val docT = collectionT.document(transactions[position].id!!)

            docP.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result.exists()) {
                                val product = it.result.toObject(ProductContainer::class.java)
                                product!!.id = it.result.id

                                holder1.transactionTitle?.text = product.name.toString()

                            } else {
                                Log.d(Tag.TAG_INFORMATION, "No such document")
                            }
                        } else {
                            Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                        }
                    }

            holder1.transactionQty?.text = transactions[position].qty.toString()
            holder1.transactionDesc?.text = if (transactions[position].desc == "") context.getString(R.string.no_desc) else transactions[position].desc.toString()
            val address = transactions[position].address!!.receiver + ", " +
                    transactions[position].address!!.street + ", " +
                    transactions[position].address!!.village + ", " +
                    transactions[position].address!!.subdistrict + ", " +
                    transactions[position].address!!.city + ", " +
                    transactions[position].address!!.province + ", " +
                    transactions[position].address!!.regional + ", " +
                    transactions[position].address!!.postal
            holder1.transactionAddress?.text = address
            holder1.transactionShipping?.text = transactions[position].courierType.toString()
            holder1.transactionPay?.text = priceID.format(transactions[position].totalCost)

            val zero = "00 Hari 00:00:00"

            if (transactions[position].exp!!.time - Date().time <= 0 || transactions[position].status!! < 0) {
                holder1.transactionExp?.text = zero
                holder1.transactionConfirm?.visibility = View.GONE
                holder1.transactionUpload?.visibility = View.GONE
                holder1.transactionUploadReceipt?.visibility = View.GONE
                holder1.transactionTrack?.visibility = View.GONE
                holder1.transactionQuestion?.visibility = View.GONE
                holder1.transactionDeliverConfirm?.visibility = View.GONE
                holder1.transactionComplain?.visibility = View.GONE
                when (transactions[position].status) {
                    6 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t6)
                    }
                    -2 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.tmin2)
                    }
                    -3 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.tmin3)
                        holder1.transactionConfirm?.visibility = View.VISIBLE
                        holder1.transactionConfirm?.setOnClickListener(listenerConfirm)
                        holder1.transactionConfirm?.tag = holder1
                    }
                    else -> {
                        docT.update("status", -1)
                        holder1.transactionStatus?.text = context.getString(R.string.tmin1)
                    }
                }
            } else {
                val timer = TimerCustom(
                        transactions[position].exp!!.time - Date().time,
                        1000,
                        holder1.transactionExp!!,
                        activity)
                timer.start()
                when (transactions[position].status) {
                    0 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t01)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.VISIBLE
                        holder1.transactionUploadReceipt?.visibility = View.GONE
                        holder1.transactionTrack?.visibility = View.GONE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.GONE
                        holder1.transactionUpload?.setOnClickListener(listenerUpload)
                        holder1.transactionUpload?.tag = holder1
                    }
                    1 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t01)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.GONE
                        holder1.transactionUploadReceipt?.visibility = View.VISIBLE
                        holder1.transactionTrack?.visibility = View.GONE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.GONE
                        Picasso.get()
                                .load(transactions[position].imageConfirmReceipt.toString())
                                .placeholder(R.color.colorPrimaryDark)
                                .error(R.color.colorPrimaryDark)
                                .into(holder1.transactionUploadReceipt)
                    }
                    2 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t2)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.GONE
                        holder1.transactionUploadReceipt?.visibility = View.GONE
                        holder1.transactionTrack?.visibility = View.GONE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.GONE
                    }
                    3 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t3)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.GONE
                        holder1.transactionUploadReceipt?.visibility = View.GONE
                        holder1.transactionTrack?.visibility = View.GONE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.GONE
                    }
                    4 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t4)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.GONE
                        holder1.transactionUploadReceipt?.visibility = View.GONE
                        holder1.transactionTrack?.visibility = View.VISIBLE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.GONE
                    }
                    5 -> {
                        holder1.transactionStatus?.text = context.getString(R.string.t5)
                        holder1.transactionConfirm?.visibility = View.GONE
                        holder1.transactionUpload?.visibility = View.GONE
                        holder1.transactionUploadReceipt?.visibility = View.GONE
                        holder1.transactionTrack?.visibility = View.GONE
                        holder1.transactionQuestion?.visibility = View.VISIBLE
                        holder1.transactionDeliverConfirm?.visibility = View.GONE
                        holder1.transactionComplain?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}