/*
 * Created by mkhaufillah on 5/29/18 10:01 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/29/18 9:57 PM
 */

package id.fishco.fishco

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import id.fishco.fishco.auth.AuthController
import id.fishco.fishco.data.Local
import id.fishco.fishco.data.Tag
import id.fishco.fishco.forums.ForumsFragment
import id.fishco.fishco.home.HomeFragment
import id.fishco.fishco.home.search.SearchActivity
import id.fishco.fishco.model.User
import id.fishco.fishco.model.UserContainer
import id.fishco.fishco.profile.ProfileFragment
import id.fishco.fishco.transactions.TransactionsFragment
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authController: AuthController
    private var mainToolbar: ActionBar? = null
    private val db = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private lateinit var cvSearchProduct: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        db.firestoreSettings = settings
        if (mAuth.currentUser != null)
            initAuth()
        val tb = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(tb)
        mainToolbar = supportActionBar
        cvSearchProduct = findViewById(R.id.cv_search_product)
        cvSearchProduct.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        initBottomBar()
    }

    override fun onStart() {
        super.onStart()
        authController.auth()
    }

    private fun initBottomBar() {
        val bottomNavigationMain = findViewById<AHBottomNavigation>(R.id.bottom_navigation_main)

        // Create items
        val home = AHBottomNavigationItem(R.string.home, R.drawable.ic_home_black_24dp, R.color.colorPrimary)
        val forum = AHBottomNavigationItem(R.string.forum, R.drawable.ic_forum_black_24dp, R.color.colorPrimary)
        val transaction = AHBottomNavigationItem(R.string.transaction, R.drawable.ic_swap_horiz_black_24dp, R.color.colorPrimary)
        val profile = AHBottomNavigationItem(R.string.profile, R.drawable.ic_account_circle_black_24dp, R.color.colorPrimary)

        // Add items
        bottomNavigationMain.addItem(home)
        bottomNavigationMain.addItem(forum)
        bottomNavigationMain.addItem(transaction)
        bottomNavigationMain.addItem(profile)

        // Set background color
        bottomNavigationMain.defaultBackgroundColor = Color.parseColor("#EEEEEE")

        // Change colors
        bottomNavigationMain.accentColor = Color.parseColor("#37474F")
        bottomNavigationMain.inactiveColor = Color.parseColor("#78909C")

        // Manage titles
        bottomNavigationMain.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        // Set listeners
        bottomNavigationMain.setOnTabSelectedListener { position, _ ->
            val trans = supportFragmentManager.beginTransaction()
            val appBar = findViewById<AppBarLayout>(R.id.top_bar_main)
            when (position) {
                0 -> {
                    appBar.visibility = View.VISIBLE
                    cvSearchProduct.visibility = View.VISIBLE
                    trans.replace(R.id.container_main, HomeFragment())
                }
                1 -> {
                    appBar.visibility = View.VISIBLE
                    cvSearchProduct.visibility = View.GONE
                    mainToolbar?.title = getString(R.string.forum)
                    trans.replace(R.id.container_main, ForumsFragment())
                }
                2 -> {
                    appBar.visibility = View.VISIBLE
                    cvSearchProduct.visibility = View.GONE
                    mainToolbar?.title = getString(R.string.transaction)
                    trans.replace(R.id.container_main, TransactionsFragment())
                }
                3 -> {
                    appBar.visibility = View.GONE
                    cvSearchProduct.visibility = View.GONE
                    trans.replace(R.id.container_main, ProfileFragment())
                }
            }
            trans.commit()
            true
        }

        // Set current item programmatically
        bottomNavigationMain.currentItem = 0
    }

    private fun initAuth() {
        val ref = db.collection("users")
        ref.document(mAuth.currentUser!!.uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document.exists()) {
                            Local.user = document.toObject(UserContainer::class.java)!!
                            Local.user.id = document.id
                            Log.d(Tag.TAG_INFORMATION, "DocumentSnapshot data: " + document.data)
                        } else {
                            val user = User()
                            val date = Date()
                            user.name = mAuth.currentUser?.displayName
                            user.email = mAuth.currentUser?.email
                            user.status = 0
                            user.timestamp = date
                            Local.user.id = mAuth.currentUser?.uid
                            Local.user.email = mAuth.currentUser?.email
                            Local.user.status = 0
                            Local.user.timestamp = date

                            ref.document(Local.user.id.toString()).set(user)

                            Log.d(Tag.TAG_INFORMATION, "No such document")
                        }
                    } else {
                        Log.d(Tag.TAG_INFORMATION, "get failed with ", it.exception)
                    }
                }
    }

}
