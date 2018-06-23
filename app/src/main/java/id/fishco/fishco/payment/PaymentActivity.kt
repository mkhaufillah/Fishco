/*
 * Created by mkhaufillah on 6/23/18 9:31 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/21/18 10:21 PM
 */

package id.fishco.fishco.payment

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.adapter.helper.TimerCustom
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Tag
import id.fishco.fishco.model.Payment
import java.text.NumberFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private val db = FirebaseFirestore.getInstance()
    private val localeID = Locale("in", "ID")
    private val priceID = NumberFormat.getCurrencyInstance(localeID)
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private var timer: TimerCustom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        val tb = findViewById<Toolbar>(R.id.payment_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        mainToolbar?.title = getString(R.string.transfer)
        mainToolbar?.setDisplayShowHomeEnabled(true)
        mainToolbar?.setDisplayHomeAsUpEnabled(true)
        db.firestoreSettings = settings

        val transferId = intent.getStringExtra(Credential.KEY_SHARE)
        val totalPay = intent.getDoubleExtra(Credential.KEY_SHARE_ALT1, 0.0)
        fillInformation(transferId, totalPay)
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

    private fun fillInformation(tfId: String, totalPay: Double) {
        val timeStopwatch = findViewById<TextView>(R.id.tv_time_stopwatch)
        val bankImg = findViewById<ImageView>(R.id.iv_bank)
        val bankTitle = findViewById<TextView>(R.id.tv_bank_title)
        val bankNumb = findViewById<TextView>(R.id.tv_bank_numb)
        val bankReceiver = findViewById<TextView>(R.id.tv_bank_receiver)
        val price = findViewById<TextView>(R.id.tv_price)

        db.collection("payment").document(tfId)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document.exists()) {
                            val payment = document.toObject(Payment::class.java)
                            Picasso.get()
                                    .load(payment?.photo.toString())
                                    .placeholder(R.color.colorPrimaryDark)
                                    .error(R.color.colorPrimaryDark)
                                    .into(bankImg)
                            bankTitle.text = payment?.name
                            bankNumb.text = payment?.rekening
                            val bank ="${payment?.receiver}\n${payment?.location}"
                            bankReceiver.text = bank
                            price.text = priceID.format(totalPay)

                            timer = TimerCustom(intent.getLongExtra(
                                    Credential.KEY_SHARE_ALT2, 0) - Date().time,
                                    1000,
                                    timeStopwatch,
                                    this)
                            timer!!.start()
                        } else {
                            Log.d(Tag.TAG_INFORMATION, "No such document")
                            Toasty.error(this, getString(R.string.no_document)).show()
                        }
                    } else {
                        Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                        Toasty.error(this, getString(R.string.error)).show()
                    }
                }
    }
}
