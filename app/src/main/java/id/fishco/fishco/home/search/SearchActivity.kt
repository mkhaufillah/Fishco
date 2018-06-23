/*
 * Created by mkhaufillah on 6/10/18 7:19 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/10/18 7:12 AM
 */

package id.fishco.fishco.home.search

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.KeyEvent
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import id.fishco.fishco.R
import id.fishco.fishco.adapter.search.RecyclerItemTouchHelperHistory
import id.fishco.fishco.adapter.search.RvSuggestionAdapter
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Local
import id.fishco.fishco.model.Suggestion
import id.fishco.fishco.product.searchProduct.SearchProductActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var suggestions = ArrayList<Suggestion>()
    private var history = ArrayList<String>()
    private lateinit var adapter: RvSuggestionAdapter
    private lateinit var rvSuggestions: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)

        setRecycler()

        val etSearch = findViewById<EditText>(R.id.et_search)
        etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP || event.action == KeyEvent.FLAG_SOFT_KEYBOARD) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER,
                    KeyEvent.FLAG_EDITOR_ACTION,
                    KeyEvent.KEYCODE_SEARCH -> {
                        val value = etSearch.text.toString()
                        if (value == "") return@setOnKeyListener true

                        history.add(value)
                        Local.saveArrayList(history, Credential.KEY_SHARE, this)

                        val intent = Intent(applicationContext, SearchProductActivity::class.java)
                        intent.putExtra("type", 0)
                        intent.putExtra(Credential.KEY_SHARE, value)
                        startActivity(intent)
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()
        authController.auth()
    }

    override fun onResume() {
        super.onResume()
        getData()

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        val itemTouchHelperCallback = RecyclerItemTouchHelperHistory(
                this,
                this,
                adapter,
                0,
                ItemTouchHelper.LEFT,
                history,
                suggestions)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvSuggestions)
    }

    private fun getData() {
        suggestions.clear()
        if (Local.getArrayList(Credential.KEY_SHARE, this) == null)
            Local.saveArrayList(history, Credential.KEY_SHARE, this)
        else {
            history = Local.getArrayList(Credential.KEY_SHARE, this)!!
            for (a in history) {
                val suggestion = Suggestion()
                suggestion.text = a
                suggestions.add(suggestion)
            }
            suggestions.reverse()
            adapter.notifyDataSetChanged()
        }
    }

    private fun setRecycler() {
        rvSuggestions = findViewById(R.id.rv_suggestions)
        adapter = RvSuggestionAdapter(this, suggestions)
        rvSuggestions.adapter = adapter
        rvSuggestions.setHasFixedSize(false)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvSuggestions.layoutManager = linearLayoutManager
    }
}
