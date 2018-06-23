/*
 * Created by mkhaufillah on 6/22/18 9:37 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/22/18 9:37 PM
 */

package id.fishco.fishco.adapter.transactions

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.data.Tag
import id.fishco.fishco.model.TransactionContainer

class RecyclerItemTouchHelperTransaction(private val context: Context,
                                         private val adapter: RvTransactionAdapter,
                                         dragDirs: Int, swipeDirs: Int,
                                         private val transactions: ArrayList<TransactionContainer>)
    : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private val db = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

    init {
        db.firestoreSettings = settings
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        // backup of removed item for undo purpose
        val deletedItem = transactions[viewHolder!!.adapterPosition]

        if (deletedItem.status == 6 || deletedItem.status!! < 0) {
            // delete from db
            db.collection("transactions").document(deletedItem.id!!)
                    .delete()
                    .addOnSuccessListener {
                        // remove from array
                        transactions.removeAt(viewHolder.adapterPosition)
                        // remove the item from recycler view
                        adapter.notifyDataSetChanged()
                        Toasty.success(context, context.getString(R.string.success_deleted)).show()
                    }
                    .addOnFailureListener {
                        Log.d(Tag.TAG_INFORMATION, "get failed with ", it)
                        Toasty.success(context, context.getString(R.string.error)).show()
                    }
        } else {
            Toasty.error(context, context.getString(R.string.deleted_deny)).show()
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = (viewHolder as RvTransactionViewHolder).lnTransactionForeground
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as RvTransactionViewHolder).lnTransactionForeground
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        val foregroundView = (viewHolder as RvTransactionViewHolder).lnTransactionForeground
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as RvTransactionViewHolder).lnTransactionForeground

        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }
}