/*
 * Created by mkhaufillah on 6/4/18 11:35 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 11:35 AM
 */

package id.fishco.fishco.product

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.fishco.fishco.R
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Credential
import ss.com.bannerslider.Slider
import id.fishco.fishco.adapter.banners.PicassoImageLoadingServiceBanner
import mehdi.sakout.fancybuttons.FancyButton
import android.util.Log
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestoreSettings
import es.dmoral.toasty.Toasty
import id.fishco.fishco.data.Tag
import id.fishco.fishco.adapter.banners.SliderBannersAdapter
import id.fishco.fishco.model.BannerContainer
import id.fishco.fishco.model.ProductContainer
import id.fishco.fishco.data.Convert
import id.fishco.fishco.adapter.products.RvProductAdapter
import id.fishco.fishco.product.discussion.DiscussionActivity
import id.fishco.fishco.product.review.ReviewActivity
import id.fishco.fishco.product.seller.SellerActivity
import id.fishco.fishco.product.buy.BuyActivity

class ProductActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var id: String
    private lateinit var refresh: SwipeRefreshLayout
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        val tb = findViewById<Toolbar>(R.id.product_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        mainToolbar?.title = getString(R.string.product)
        mainToolbar?.setDisplayShowHomeEnabled(true)
        mainToolbar?.setDisplayHomeAsUpEnabled(true)
        db.firestoreSettings = settings

        id = intent.getStringExtra(Credential.KEY_SHARE)
        refresh = findViewById(R.id.srl_product)

        refresh.setOnRefreshListener {
            checkCommand()
        }

        checkCommand()
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

    private fun checkCommand() {
        loadData()
        val type = intent.getIntExtra("type", 0)
        if (type == 0) return
    }

    private fun loadData() {
        refresh.isRefreshing = true
        val servicePicasso = PicassoImageLoadingServiceBanner(this)
        val slider = findViewById<Slider>(R.id.product_slider)
        val nameProduct = findViewById<TextView>(R.id.tv_name_product)
        val priceProduct = findViewById<TextView>(R.id.tv_price_product)
        val product = findViewById<RatingBar>(R.id.rb_product)
        val discussionProduct = findViewById<FancyButton>(R.id.btn_discussion_product)
        val reviewProduct = findViewById<FancyButton>(R.id.btn_review_product)
        val sellerProduct = findViewById<FancyButton>(R.id.btn_seller_product)
        val buyProduct = findViewById<FancyButton>(R.id.btn_buy_product)
        val soldProduct = findViewById<TextView>(R.id.tv_sold_product)
        val seeProduct = findViewById<TextView>(R.id.tv_see_product)
        val qtyProduct = findViewById<TextView>(R.id.tv_qty_product)
        val weightProduct = findViewById<TextView>(R.id.tv_weight_product)
        val categoryProduct = findViewById<TextView>(R.id.tv_category_product)
        val locationProduct = findViewById<TextView>(R.id.tv_location_product)
        val descriptionProduct = findViewById<TextView>(R.id.tv_description_product)
        val recomendedProduct = findViewById<RecyclerView>(R.id.rv_recomended_product)

        val docRef = db.collection("products").document(id)

        docRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document.exists()) {
                    val productListener = document.toObject(ProductContainer::class.java)
                    productListener?.id = document.id

                    val price = Convert.compressIDR(productListener?.price!!, this)

                    if (productListener.photo != null) {
                        val banners = ArrayList<BannerContainer>()

                        for (a in productListener.photo!!) {
                            val banner = BannerContainer()
                            banner.url = a
                            banners.add(banner)
                        }

                        Slider.init(servicePicasso)
                        slider.setAdapter(SliderBannersAdapter(banners))
                    }

                    mainToolbar?.title = productListener.name.toString()
                    nameProduct.text = productListener.name.toString()
                    priceProduct.text = price
                    product.rating = if (productListener.avgStar != null) productListener.avgStar!! else 0f
                    discussionProduct.setOnClickListener {
                        val intent = Intent(this, DiscussionActivity::class.java)
                        intent.putExtra(Credential.KEY_SHARE, id)
                        startActivity(intent)
                    }
                    reviewProduct.setOnClickListener {
                        val intent = Intent(this, ReviewActivity::class.java)
                        intent.putExtra(Credential.KEY_SHARE, id)
                        startActivity(intent)
                    }
                    sellerProduct.setOnClickListener {
                        val intent = Intent(this, SellerActivity::class.java)
                        intent.putExtra(Credential.KEY_SHARE, id)
                        startActivity(intent)
                    }
                    buyProduct.setOnClickListener {
                        val intent = Intent(this, BuyActivity::class.java)
                        intent.putExtra(Credential.KEY_SHARE, id)
                        startActivity(intent)
                    }
                    soldProduct.text = productListener.sold.toString()
                    seeProduct.text = productListener.seeing.toString()
                    qtyProduct.text = productListener.qty.toString()
                    val weight = productListener.weight.toString() + "g"
                    weightProduct.text = weight
                    categoryProduct.text = filterCat(productListener.category!!)
                    locationProduct.text = productListener.address?.city.toString()
                    if (productListener.desc != null)
                        descriptionProduct.text = productListener.desc.toString()
                    else
                        descriptionProduct.text = getString(R.string.no_desc)

                    val products = ArrayList<ProductContainer>()
                    val adapter = RvProductAdapter(this, products)
                    recomendedProduct.adapter = adapter
                    recomendedProduct.setHasFixedSize(false)
                    val linearLayoutManager = LinearLayoutManager(this)
                    linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    recomendedProduct.layoutManager = linearLayoutManager
                    val key = productListener.name.toString().split(" ")[0]
                    val query = db.collection("products").orderBy("name")
                            .startAt(key)
                            .endAt(key+'\uf8ff')
                            .limit(12)
                    query.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val doc = it.result
                            if (doc.isEmpty) {
                                Log.d(Tag.TAG_INFORMATION, "No such document")
                            } else {
                                for (a in it.result) {
                                    val p = a.toObject(ProductContainer::class.java)
                                    p.id = a.id
                                    if (p.id != id) products.add(p)
                                }
                                adapter.notifyDataSetChanged()
                                Log.d(Tag.TAG_INFORMATION, "Load top products")
                            }
                        } else {
                            Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                        }
                    }

                } else {
                    Log.d(Tag.TAG_INFORMATION, "No such document")
                    Toasty.error(this, getString(R.string.no_document)).show()
                }
            } else {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                Toasty.error(this, getString(R.string.error)).show()
            }
            refresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater!!.inflate(R.menu.product_menu, menu)
        return true
    }

    private fun filterCat(cat: Int): String {
        if (cat == 0) return getString(R.string.fish)
        if (cat == 1) return getString(R.string.handmade)
        if (cat == 2)return getString(R.string.snack)
        return getString(R.string.more)
    }
}
