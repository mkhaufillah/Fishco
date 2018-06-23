/*
 * Created by mkhaufillah on 6/10/18 8:07 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/10/18 7:33 AM
 */

package id.fishco.fishco.adapter.search

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Local
import id.fishco.fishco.model.Suggestion

class RecyclerItemTouchHelperHistory(private val context: Context,
                                     private val activity: Activity,
                                     private val adapter: RvSuggestionAdapter,
                                     dragDirs: Int, swipeDirs: Int,
                                     private val history: ArrayList<String>,
                                     private val suggestions: ArrayList<Suggestion>)
    : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        // backup of removed item for undo purpose
        val deletedItem = suggestions[viewHolder!!.adapterPosition]

        // get the removed item name to display it in snack bar
        val name = "${deletedItem.text} "

        // search in parent data
        for ((i, n) in history.withIndex()) {
            if (n == deletedItem.text) {
                history.removeAt(i)
                break
            }
        }

        Local.saveArrayList(history, Credential.KEY_SHARE, activity)

        for ((i, n) in suggestions.withIndex()) {
            if (n.text == deletedItem.text) {
                suggestions.removeAt(i)
                break
            }
        }

        // remove the item from recycler view
        adapter.notifyDataSetChanged()

        Toasty.success(context, name+context.getString(R.string.success_deleted)).show()
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = (viewHolder as RvSuggestionViewHolder).tvSuggestion
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as RvSuggestionViewHolder).tvSuggestion
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        val foregroundView = (viewHolder as RvSuggestionViewHolder).tvSuggestion
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as RvSuggestionViewHolder).tvSuggestion

        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }
}