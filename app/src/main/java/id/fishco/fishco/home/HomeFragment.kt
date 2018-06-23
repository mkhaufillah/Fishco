/*
 * Created by mkhaufillah on 5/29/18 10:00 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 1:05 AM
 */

package id.fishco.fishco.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import id.fishco.fishco.R
import id.fishco.fishco.home.favorites.FavoritesActivity
import id.fishco.fishco.home.notifications.NotificationsActivity
import ss.com.bannerslider.Slider
import id.fishco.fishco.adapter.banners.SliderBannersAdapter
import id.fishco.fishco.model.BannerContainer
import id.fishco.fishco.data.Local
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import id.fishco.fishco.data.Tag
import id.fishco.fishco.model.ProductContainer
import id.fishco.fishco.home.banner.BannerActivity
import id.fishco.fishco.data.Credential
import id.fishco.fishco.adapter.products.RvProductAdapter
import id.fishco.fishco.product.searchProduct.SearchProductActivity
import id.fishco.fishco.adapter.banners.PicassoImageLoadingServiceBanner

class HomeFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var refresh: SwipeRefreshLayout
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        refresh = view.findViewById(R.id.srl_home)
        db.firestoreSettings = settings

        refresh.setOnRefreshListener {
            Local.resetBannerAndTopProduct()
            banner(view)
            topProducts(view)
        }

        banner(view)
        categories(view)
        topProducts(view)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.notifications_home -> {
                val intent = Intent(context, NotificationsActivity::class.java)
                context?.startActivity(intent)
                return true
            }
            R.id.favorites_home -> {
                val intent = Intent(context, FavoritesActivity::class.java)
                context?.startActivity(intent)
                return true
            }
        }

        return false
    }

    private fun banner(view: View) {
        val ivBannerMenu = view.findViewById<ImageView>(R.id.iv_banner_menu)

        ivBannerMenu.setOnClickListener {
            val intent = Intent(context, BannerActivity::class.java)
            context?.startActivity(intent)
        }

        val servicePicasso = PicassoImageLoadingServiceBanner(context!!)
        val slider = view.findViewById<Slider>(R.id.banner_main)

        Slider.init(servicePicasso)
        slider.setAdapter(SliderBannersAdapter(Local.banners))

        if (Local.banners.size == 0) {
            refresh.isRefreshing = true
            val dbCollections = db.collection("banners")
            dbCollections.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.isEmpty) {
                        Log.d(Tag.TAG_INFORMATION, "No such document")
                        Toasty.error(context!!, getString(R.string.no_document)).show()
                    } else {
                        for (doc in it.result) {
                            val banner = doc.toObject(BannerContainer::class.java)
                            banner.id = doc.id
                            Local.addBanner(banner)
                        }
                        slider.setAdapter(SliderBannersAdapter(Local.banners))
                    }
                } else {
                    Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                    Toasty.error(context!!, getString(R.string.error)).show()
                }
                refresh.isRefreshing = false
            }
        }
    }

    private fun categories(view: View) {
        val ivCategoriesMenu = view.findViewById<ImageView>(R.id.iv_categories_menu)

        ivCategoriesMenu.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 4)
            context?.startActivity(intent)
        }

        val ivFishCat = view.findViewById<ImageView>(R.id.iv_fish_cat)
        val ivHandmadeCat = view.findViewById<ImageView>(R.id.iv_handmade_cat)
        val ivSnackCat = view.findViewById<ImageView>(R.id.iv_snack_cat)
        val ivMoreCat = view.findViewById<ImageView>(R.id.iv_more_cat)

        ivFishCat.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 1)
            intent.putExtra(Credential.KEY_SHARE, 0)
            context?.startActivity(intent)
        }

        ivHandmadeCat.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 1)
            intent.putExtra(Credential.KEY_SHARE, 1)
            context?.startActivity(intent)
        }

        ivSnackCat.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 1)
            intent.putExtra(Credential.KEY_SHARE, 2)
            context?.startActivity(intent)
        }

        ivMoreCat.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 1)
            intent.putExtra(Credential.KEY_SHARE, 3)
            context?.startActivity(intent)
        }

        ivFishCat.layoutParams.width = Local.getDeviceWidth(context!!)/4
        ivFishCat.layoutParams.height = Local.getDeviceWidth(context!!)/4
        ivFishCat.requestLayout()
        ivHandmadeCat.layoutParams.width = Local.getDeviceWidth(context!!)/4
        ivHandmadeCat.layoutParams.height = Local.getDeviceWidth(context!!)/4
        ivHandmadeCat.requestLayout()
        ivSnackCat.layoutParams.width = Local.getDeviceWidth(context!!)/4
        ivSnackCat.layoutParams.height = Local.getDeviceWidth(context!!)/4
        ivSnackCat.requestLayout()
        ivMoreCat.layoutParams.width = Local.getDeviceWidth(context!!)/4
        ivMoreCat.layoutParams.height = Local.getDeviceWidth(context!!)/4
        ivMoreCat.requestLayout()
    }

    private fun topProducts(view: View) {
        val ivTopProductsMenu = view.findViewById<ImageView>(R.id.iv_top_products_menu)

        ivTopProductsMenu.setOnClickListener {
            val intent = Intent(context, SearchProductActivity::class.java)
            intent.putExtra("type", 3)
            context?.startActivity(intent)
        }

        val ref = db.collection("products")
        val query = ref.orderBy("avgStar", Query.Direction.DESCENDING)
                .orderBy("sold", Query.Direction.DESCENDING)
                .orderBy("seeing", Query.Direction.DESCENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)

        val rvTopProduct = view.findViewById<RecyclerView>(R.id.rv_top_product)
        val adapter = RvProductAdapter(view.context, Local.topProducts)
        rvTopProduct.adapter = adapter
        rvTopProduct.setHasFixedSize(false)
        val linearLayoutManager = LinearLayoutManager(view.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvTopProduct.layoutManager = linearLayoutManager

        if (Local.topProducts.size == 0) {
            refresh.isRefreshing = true
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.isEmpty) {
                        Log.d(Tag.TAG_INFORMATION, "No such document")
                        Toasty.error(context!!, getString(R.string.no_document)).show()
                    } else {
                        for (doc in it.result) {
                            val product = doc.toObject(ProductContainer::class.java)
                            product.id = doc.id
                            Local.addTopProduct(product)
                        }
                        adapter.notifyDataSetChanged()
                        Log.d(Tag.TAG_INFORMATION, "Load top products")
                    }
                } else {
                    Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                    Toasty.error(context!!, getString(R.string.error)).show()
                }
                refresh.isRefreshing = false
            }
        }
    }
}
