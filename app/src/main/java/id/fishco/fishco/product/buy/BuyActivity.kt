/*
 * Created by mkhaufillah on 6/14/18 11:01 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/14/18 11:01 AM
 */

package id.fishco.fishco.product.buy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.jaredrummler.materialspinner.MaterialSpinner
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Tag
import id.fishco.fishco.data.Credential
import mehdi.sakout.fancybuttons.FancyButton
import java.text.NumberFormat
import id.fishco.fishco.data.Local
import id.fishco.fishco.adapter.address.ADAddress
import id.fishco.fishco.adapter.helper.RetrofitApi
import id.fishco.fishco.model.rajaongkirApi.BaseRo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import id.fishco.fishco.adapter.transactions.ADPayMethod
import id.fishco.fishco.model.*
import kotlin.collections.ArrayList
import id.fishco.fishco.payment.PaymentActivity

class BuyActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private lateinit var refresh: SwipeRefreshLayout
    private val db = FirebaseFirestore.getInstance()
    private lateinit var id: String
    private val localeID = Locale("in", "ID")
    private val priceID = NumberFormat.getCurrencyInstance(localeID)
    private var productCost = 0.0
    private var courierCost = 0.0
    private var totalCost = 0.0
    private var lastQty = 1
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        val tb = findViewById<Toolbar>(R.id.buy_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        mainToolbar?.title = getString(R.string.buy)
        mainToolbar?.setDisplayShowHomeEnabled(true)
        mainToolbar?.setDisplayHomeAsUpEnabled(true)
        db.firestoreSettings = settings

        id = intent.getStringExtra(Credential.KEY_SHARE)
        refresh = findViewById(R.id.srl_buy)

        refresh.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    override fun onStart() {
        super.onStart()
        authController.auth()
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
        val nameBuy = findViewById<TextView>(R.id.tv_name_buy)
        val addressBuy = findViewById<TextView>(R.id.tv_address_buy)
        val changeAddressBuy = findViewById<FancyButton>(R.id.btn_address_buy)
        val infoBuy = findViewById<EditText>(R.id.et_info_buy)
        val qtyBuy = findViewById<MaterialSpinner>(R.id.ms_qty_buy)
        val courierBuy = findViewById<MaterialSpinner>(R.id.ms_courier_buy)
        val courierTypeBuy = findViewById<MaterialSpinner>(R.id.ms_courier_type_buy)
        val priceBuy = findViewById<TextView>(R.id.tv_price_buy)
        val costShippingBuy = findViewById<TextView>(R.id.tv_cost_shipping_buy)
        val totalPriceBuy = findViewById<TextView>(R.id.tv_total_price_buy)
        val confirmBuy = findViewById<FancyButton>(R.id.btn_confirm_buy)
        val save = findViewById<ImageView>(R.id.iv_save)

        var productListener: ProductContainer? = ProductContainer()
        val addresses = ArrayList<AddressContainer>()
        var addressSelected = AddressContainer()

        val docRef = db.collection("products").document(id)
        docRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document.exists()) {
                    productListener = document.toObject(ProductContainer::class.java)
                    productListener?.id = document.id

                    val quantities = ArrayList<Int>()
                    for (i in 1..productListener?.qty!!) {
                        quantities.add(i)
                    }

                    qtyBuy.setItems(quantities)
                    qtyBuy.selectedIndex = 0
                    qtyBuy.setOnItemSelectedListener { _, _, _, item ->
                        productCost /= lastQty
                        lastQty = item as Int
                        productCost *= lastQty
                        totalCost = productCost + courierCost

                        getCourier(totalPriceBuy,
                                costShippingBuy,
                                courierTypeBuy,
                                productListener?.address?.idCity.toString(),
                                addresses[0].idCity.toString(),
                                lastQty.toString(),
                                courierBuy.text.toString())

                        priceBuy.text = priceID.format(productCost)
                        costShippingBuy.text = priceID.format(courierCost)
                        totalPriceBuy.text = priceID.format(totalCost)
                    }

                    productCost = productListener?.price!!.times(Integer.parseInt(qtyBuy.text.toString()))
                    totalCost = productCost + courierCost

                    nameBuy.text = productListener?.name.toString()
                    priceBuy.text = priceID.format(productCost)
                    totalPriceBuy.text = priceID.format(totalCost)

                    courierBuy.setItems(productListener?.listCourier?.toList()!!)
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

        val docRefAddr = db.collection("users")
                .document(Local.user.id!!)
                .collection("addresses")
        docRefAddr.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document.isEmpty) {
                    Log.d(Tag.TAG_INFORMATION, "No such document")
                    Toasty.error(this, getString(R.string.no_document)).show()
                } else {
                    for (doc in it.result) {
                        val addressListener = doc.toObject(AddressContainer::class.java)
                        addressListener.id = doc.id
                        addresses.add(addressListener)
                        if (addressListener.type == 1) {
                            val address = addressListener.receiver + ", " +
                                    addressListener.street + ", " +
                                    addressListener.village + ", " +
                                    addressListener.subdistrict + ", " +
                                    addressListener.city + ", " +
                                    addressListener.province + ", " +
                                    addressListener.regional + ", " +
                                    addressListener.postal
                            addressBuy.text = address
                            addressSelected = addressListener
                        }
                    }

                    changeAddressBuy.setOnClickListener {
                        val adapter = ADAddress(this, addresses)
                        LovelyChoiceDialog(this)
                                .setTopTitle(R.string.address_change)
                                .setTopColorRes(R.color.colorPrimary)
                                .setItems(adapter, { _, item ->
                                    val address = item.street + ", " +
                                            item.village + ", " +
                                            item.subdistrict + ", " +
                                            item.city + ", " +
                                            item.province + ", " +
                                            item.regional + ", " +
                                            item.postal
                                    addressBuy.text = address
                                    addressSelected = item
                                }).show()
                    }

                    courierBuy.setOnItemSelectedListener { _, _, _, _ ->
                        val qty = productListener?.weight?.times(Integer.parseInt(qtyBuy.text.toString()))
                        getCourier(totalPriceBuy,
                                costShippingBuy,
                                courierTypeBuy,
                                productListener?.address?.idCity.toString(),
                                addresses[0].idCity.toString(),
                                qty.toString(),
                                courierBuy.text.toString())
                    }
                }
            } else {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                Toasty.error(this, getString(R.string.error)).show()
            }
            refresh.isRefreshing = false
        }

        save.setOnClickListener {
            saveTransaction(addressSelected,
                    infoBuy.text.toString(),
                    courierTypeBuy.text.toString(),
                    -3,
                    null,
                    Date().time)
        }

        confirmBuy.setOnClickListener {
            if (addressSelected.id == null ||
                    qtyBuy.text.isEmpty() ||
                    productCost == 0.0 ||
                    courierCost == 0.0 ||
                    courierTypeBuy.text.isEmpty()
            ) {
                Toasty.error(this, getString(R.string.fill_blank)).show()
                return@setOnClickListener
            }
            val refBank = db.collection("payment")
            refBank.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.isEmpty) {
                        Log.d(Tag.TAG_INFORMATION, "No such document")
                        Toasty.error(this, getString(R.string.no_document)).show()
                    } else {
                        val payment = ArrayList<Payment>()
                        for (doc in document) {
                            val paymentObj = doc.toObject(Payment::class.java)
                            paymentObj.id = doc.id
                            payment.add(paymentObj)
                        }
                        val adapter = ADPayMethod(this, payment)
                        LovelyChoiceDialog(this)
                                .setTopTitle(R.string.pay_method)
                                .setTopColorRes(R.color.colorPrimary)
                                .setItems(adapter, { _, item ->
                                    val date = Date().time
                                    saveTransaction(addressSelected,
                                            infoBuy.text.toString(),
                                            courierTypeBuy.text.toString(),
                                            0,
                                            item.id,
                                            date)
                                    val intent = Intent(this, PaymentActivity::class.java)
                                    intent.putExtra(Credential.KEY_SHARE ,item.id)
                                    intent.putExtra(Credential.KEY_SHARE_ALT1, totalCost)
                                    intent.putExtra(Credential.KEY_SHARE_ALT2, date+3600000)
                                    startActivity(intent)
                                    finish()
                                }).show()
                    }
                } else {
                    Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                    Toasty.error(this, getString(R.string.error)).show()
                }
            }
        }

    }

    private fun saveTransaction(address: AddressContainer?,
                        desc: String?,
                        courierType: String?,
                        status: Int?,
                        payMethod: String?,
                        timestampNow: Long) {
        if (address?.id == null ||
                productCost == 0.0 ||
                courierCost == 0.0 ||
                courierType == null
        ) {
            Toasty.error(this, getString(R.string.fill_blank)).show()
            return
        }

        val transaction = Transaction()
        transaction.product = id
        transaction.buyer = mAuth.uid.toString()
        transaction.address = address
        transaction.qty = lastQty
        transaction.desc = desc
        transaction.productCost = productCost
        transaction.courierCost = courierCost
        transaction.courierType = courierType
        transaction.status = status
        transaction.totalCost = totalCost
        transaction.payMethod = payMethod
        transaction.exp = Date(timestampNow+3600000)
        transaction.timestamp = Date(timestampNow)

        db.collection("transactions").add(transaction).addOnCompleteListener {
            if (it.isSuccessful) {
                Toasty.success(this, getString(R.string.success_trans_save)).show()
                finish()
            } else {
                Log.e(Tag.TAG_INFORMATION, "Failed save transaction", it.exception)
                Toasty.error(this, getString(R.string.error)).show()
            }
        }
    }

    private fun getCourier(totalPriceBuy: TextView,
                           costShippingBuy: TextView,
                           courierTypeBuy: MaterialSpinner,
                           origin: String,
                           destination: String,
                           weight: String,
                           courier: String) {

        val retrofit = Retrofit.Builder()
                .baseUrl(Credential.BASE_URL_RO)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create<RetrofitApi>(RetrofitApi::class.java)
        val callback = api.getCost(origin, destination, weight, courier, Credential.API_KEY_RO)
        callback.enqueue(object : Callback<BaseRo> {
            override fun onFailure(call: Call<BaseRo>?, t: Throwable?) {
                Log.d(Tag.TAG_INFORMATION, "get failed with ", t)
                Toasty.error(applicationContext, getString(R.string.error)).show()
            }

            override fun onResponse(call: Call<BaseRo>?, response: Response<BaseRo>?) {
                try {
                    if (response?.body()?.rajaongkir?.status?.code.equals("200")) {
                        val costses = response?.body()!!.rajaongkir?.results?.get(0)?.costs
                        val courierTypeArr = ArrayList<String>()

                        for (costs in costses!!) {
                            courierTypeArr.add(costs.service + " - " +
                                    costs.cost?.get(0)?.etd + " ${getString(R.string.day)} - Rp. " +
                                    costs.cost?.get(0)?.value)
                        }

                        courierTypeBuy.setItems(courierTypeArr)
                        courierTypeBuy.setOnItemSelectedListener { _, position, _, _ ->
                            courierCost = Integer.parseInt(costses[position].cost?.get(0)?.value) + 0.0
                            totalCost = courierCost + productCost
                            costShippingBuy.text = priceID.format(courierCost)
                            totalPriceBuy.text = priceID.format(totalCost)
                        }
                    } else {
                        Log.d(Tag.TAG_INFORMATION, "No such document")
                        Toasty.error(applicationContext, getString(R.string.no_document)).show()
                    }
                } catch (e: Exception) {
                    Log.d(Tag.TAG_INFORMATION, "get failed with ", e)
                    Toasty.error(applicationContext, getString(R.string.error)).show()
                }

            }

        })
    }

}
