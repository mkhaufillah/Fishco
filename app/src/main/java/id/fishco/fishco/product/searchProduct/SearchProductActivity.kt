/*
 * Created by mkhaufillah on 6/10/18 7:20 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/9/18 5:04 PM
 */

package id.fishco.fishco.product.searchProduct

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.adapter.helper.EndlessScrollRv
import id.fishco.fishco.adapter.products.RvProductAdapter
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Tag
import id.fishco.fishco.home.search.SearchActivity
import id.fishco.fishco.home.favorites.FavoritesActivity
import id.fishco.fishco.home.notifications.NotificationsActivity
import id.fishco.fishco.model.ProductContainer

class SearchProductActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private val db = FirebaseFirestore.getInstance()
    private val products = ArrayList<ProductContainer>()
    private val ref = db.collection("products")
    private lateinit var adapter: RvProductAdapter
    private lateinit var tvNameSearch: TextView
    private lateinit var tvError: TextView
    private lateinit var refresh: SwipeRefreshLayout
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        val tb = findViewById<Toolbar>(R.id.sp_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        mainToolbar?.setDisplayShowHomeEnabled(true)
        mainToolbar?.setDisplayHomeAsUpEnabled(true)
        tvError = findViewById(R.id.tv_error)
        refresh = findViewById(R.id.srl_sp)
        db.firestoreSettings = settings

        val cvSearchProduct = findViewById<CardView>(R.id.cv_search_product)
        cvSearchProduct.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        tvNameSearch = findViewById(R.id.tv_name_search)

        refresh.setOnRefreshListener {
            products.clear()
            checkCommand()
        }

        recycler()
        products.clear()
        checkCommand()
    }

    private fun recycler() {
        val rvSp = findViewById<RecyclerView>(R.id.rv_sp)
        adapter = RvProductAdapter(this, products)
        rvSp.adapter = adapter
        rvSp.setHasFixedSize(false)
        val gridLayoutManager = GridLayoutManager(this, 3, GridLayout.VERTICAL, false)
        rvSp.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessScrollRv (
                gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                checkCommand()
            }

        }
        rvSp.addOnScrollListener(scrollListener)
    }

    override fun onStart() {
        super.onStart()
        authController.auth()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater!!.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.notifications_home -> {
                val intent = Intent(this, NotificationsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.favorites_home -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return false
    }

    private fun checkCommand() {
        val dataType = intent.getIntExtra("type", 0)
        Log.d(Tag.TAG_INFORMATION, "$dataType")
        if (dataType == 0) searchWithKey(intent.getStringExtra(Credential.KEY_SHARE))
        if (dataType == 1) searchWithCategory(intent.getIntExtra(Credential.KEY_SHARE, 0))
        if (dataType == 2) searchWithKeyAndCategory(intent.getStringExtra(Credential.KEY_SHARE), intent.getIntExtra(Credential.KEY_SHARE_ALT1, 0))
        if (dataType == 3) orderTopProduct()
        if (dataType == 4) orderLatestProduct()
    }

    private fun orderLatestProduct() {
        mainToolbar?.title = getString(R.string.latest)
        tvNameSearch.text = getString(R.string.latest)
        val query: Query
        if (products.size == 0)
            query = ref.orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(12)
        else
            query = ref.orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(12)
                    .startAfter(products[products.size-1].timestamp)
        loadData(query)
    }

    private fun orderTopProduct() {
        mainToolbar?.title = getString(R.string.top_product)
        tvNameSearch.text = getString(R.string.top_product)
        val query: Query
        if (products.size == 0)
            query = ref.orderBy("avgStar", Query.Direction.DESCENDING)
                .orderBy("sold", Query.Direction.DESCENDING)
                .orderBy("seeing", Query.Direction.DESCENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(12)
        else
            query = ref.orderBy("avgStar", Query.Direction.DESCENDING)
                    .orderBy("sold", Query.Direction.DESCENDING)
                    .orderBy("seeing", Query.Direction.DESCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(12)
                    .startAfter(products[products.size-1].avgStar,
                            products[products.size-1].sold,
                            products[products.size-1].seeing,
                            products[products.size-1].timestamp)
        loadData(query)
    }

    private fun searchWithKeyAndCategory(key: String, cat: Int) {
        val catString = filterCat(cat)
        val title = "$key ($catString)"
        mainToolbar?.title = title
        tvNameSearch.text = title
        val query: Query
        if (products.size == 0)
            query = ref.whereEqualTo("category", cat)
                .orderBy("name")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAt(key)
                .endAt(key+'\uf8ff')
                .limit(12)
        else
            query = ref.whereEqualTo("category", cat)
                    .orderBy("name")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAt(key)
                    .startAfter(products[products.size-1].name,
                            products[products.size-1].timestamp)
                    .endAt(key+'\uf8ff')
                    .limit(12)
        loadData(query)
    }

    private fun searchWithCategory(cat: Int) {
        val catString = filterCat(cat)
        mainToolbar?.title = catString
        tvNameSearch.text = catString
        val query: Query
        if (products.size == 0)
            query = ref.whereEqualTo("category", cat)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(12)
        else
            query = ref.whereEqualTo("category", cat)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(products[products.size-1].timestamp)
                    .limit(12)
        loadData(query)
    }

    private fun searchWithKey(key: String) {
        mainToolbar?.title = key
        tvNameSearch.text = key
        val query: Query
        if (products.size == 0)
            query = ref.orderBy("name")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAt(key)
                .endAt(key+'\uf8ff')
                .limit(12)
        else
            query = ref.orderBy("name")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAt(key)
                    .startAfter(products[products.size-1].name,
                            products[products.size-1].timestamp)
                    .endAt(key+'\uf8ff')
                    .limit(12)
        loadData(query)
    }

    private fun loadData(query: Query) {
        tvError.visibility = View.GONE
        refresh.isRefreshing = true
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document.isEmpty) {
                    Log.d(Tag.TAG_INFORMATION, "No such document")
                    if (products.size == 0) Toasty.error(this, getString(R.string.no_document)).show()
                    if (products.size == 0) tvError.visibility = View.VISIBLE
                } else {
                    for (doc in it.result) {
                        val product = doc.toObject(ProductContainer::class.java)
                        product.id = doc.id
                        products.add(product)
                    }
                    adapter.notifyDataSetChanged()
                    Log.d(Tag.TAG_INFORMATION, "Load top products")
                    tvError.visibility = View.GONE
                }
            } else {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                Toasty.error(this, getString(R.string.error)).show()
                if (products.size == 0) tvError.visibility = View.VISIBLE
            }
            refresh.isRefreshing = false
        }
    }

    private fun filterCat(cat: Int): String {
        if (cat == 0) return getString(R.string.fish)
        if (cat == 1) return getString(R.string.handmade)
        if (cat == 2)return getString(R.string.snack)
        return getString(R.string.more)
    }

}
