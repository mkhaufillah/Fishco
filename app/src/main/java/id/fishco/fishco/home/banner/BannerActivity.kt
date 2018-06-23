/*
 * Created by mkhaufillah on 6/5/18 5:49 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/5/18 5:49 AM
 */

package id.fishco.fishco.home.banner

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import id.fishco.fishco.R
import id.fishco.fishco.adapter.banners.RvBannerAdapter
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Local

class BannerActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        val tb = findViewById<Toolbar>(R.id.banner_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        mainToolbar?.title = getString(R.string.banners)
        mainToolbar?.setDisplayShowHomeEnabled(true)
        mainToolbar?.setDisplayHomeAsUpEnabled(true)

        refresh = findViewById(R.id.srl_banner)
        tvError = findViewById(R.id.tv_error)

        refresh.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    override fun onStart() {
        super.onStart()
        authController.auth()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater!!.inflate(R.menu.banner_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return false
    }

    private fun loadData() {
        refresh.isRefreshing = true
        val rvTopProduct = findViewById<RecyclerView>(R.id.rv_banner)
        val adapter = RvBannerAdapter(this, Local.banners)
        rvTopProduct.adapter = adapter
        rvTopProduct.setHasFixedSize(false)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvTopProduct.layoutManager = linearLayoutManager
        refresh.isRefreshing = false

        if (Local.banners.size == 0) {
            tvError.visibility = View.VISIBLE
        }
    }

}
