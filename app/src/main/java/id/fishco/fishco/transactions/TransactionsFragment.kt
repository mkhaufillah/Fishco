/*
 * Created by mkhaufillah on 5/29/18 10:00 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 1:05 AM
 */

package id.fishco.fishco.transactions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import id.fishco.fishco.transactions.filterTransactions.FilterTransactionsActivity
import id.fishco.fishco.transactions.sortTransactions.SortTransactionsActivity
import id.fishco.fishco.R
import id.fishco.fishco.adapter.helper.EndlessScrollRv
import id.fishco.fishco.adapter.transactions.RvTransactionAdapter
import id.fishco.fishco.model.TransactionContainer
import id.fishco.fishco.data.Tag
import id.fishco.fishco.adapter.transactions.RecyclerItemTouchHelperTransaction
import id.fishco.fishco.data.Credential

class TransactionsFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var tvError: TextView
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private lateinit var adapter: RvTransactionAdapter
    private val transactions = ArrayList<TransactionContainer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        refresh = view.findViewById(R.id.srl_transactions)
        tvError = view.findViewById(R.id.tv_error)
        db.firestoreSettings = settings
        recycler(view)
        loadData()

        refresh.setOnRefreshListener {
            transactions.clear()
            adapter.notifyDataSetChanged()
            loadData()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.transactions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.filter_transactions -> {
                val intent = Intent(context, FilterTransactionsActivity::class.java)
                context?.startActivity(intent)
                return true
            }
            R.id.sort_transactions -> {
                val intent = Intent(context, SortTransactionsActivity::class.java)
                context?.startActivity(intent)
                return true
            }
        }

        return false
    }

    private fun recycler(view: View) {
        val recyclerTransactions = view.findViewById<RecyclerView>(R.id.rv_transactions)
        adapter = RvTransactionAdapter(this, activity!!, context!!, transactions)
        recyclerTransactions.adapter = adapter
        recyclerTransactions.setHasFixedSize(false)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerTransactions.layoutManager = linearLayoutManager

        val scrollListener = object : EndlessScrollRv(
                linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadData()
            }

        }
        recyclerTransactions.addOnScrollListener(scrollListener)

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        val itemTouchHelperCallback = RecyclerItemTouchHelperTransaction(
                context!!,
                adapter,
                0,
                ItemTouchHelper.LEFT,
                transactions)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerTransactions)
    }

    private fun loadData() {
        tvError.visibility = View.GONE
        refresh.isRefreshing = true
        val query: Query
        if (transactions.size == 0) {
            query = db.collection("transactions")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3)
        } else {
            query = db.collection("transactions")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3)
                    .startAfter(transactions[transactions.size-1].timestamp)
        }

        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.isEmpty) {
                    Log.d(Tag.TAG_INFORMATION, "No such document")
                    if (transactions.size == 0) Toasty.error(context!!, getString(R.string.no_document)).show()
                    if (transactions.size == 0) tvError.visibility = View.VISIBLE
                } else {
                    for (doc in it.result) {
                        val transaction = doc.toObject(TransactionContainer::class.java)
                        transaction.id = doc.id
                        transactions.add(transaction)
                    }
                    adapter.notifyDataSetChanged()
                    Log.d(Tag.TAG_INFORMATION, "Load top products")
                    tvError.visibility = View.GONE
                }
            } else {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                Toasty.error(context!!, getString(R.string.error)).show()
                if (transactions.size == 0) tvError.visibility = View.VISIBLE
            }
            refresh.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Credential.CODE_UPLOAD && resultCode == Activity.RESULT_OK) {
            transactions.clear()
            adapter.notifyDataSetChanged()
            loadData()
        }
    }
}
