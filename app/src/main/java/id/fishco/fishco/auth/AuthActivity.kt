/*
 * Created by mkhaufillah on 5/29/18 9:59 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/29/18 9:58 PM
 */

package id.fishco.fishco.auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import id.fishco.fishco.data.Credential
import id.fishco.fishco.data.Tag
import mehdi.sakout.fancybuttons.FancyButton
import java.util.*

class AuthActivity : AppCompatActivity() {
    private lateinit var authGoogle: AuthGoogle
    private lateinit var authFacebook: AuthFacebook
    private lateinit var authController: AuthController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        mAuth = FirebaseAuth.getInstance()
        authController = AuthController(this, mAuth.currentUser)
        authGoogle = AuthGoogle(mAuth, this, getString(R.string.error_auth))
        authFacebook = AuthFacebook(mAuth, this, getString(R.string.error_auth))
        mCallbackManager = authFacebook.mCallbackManager

        val btnLoginGoogle = findViewById<FancyButton>(R.id.btn_login_google)
        btnLoginGoogle.setOnClickListener {
            startActivityForResult(authGoogle.getIntent(this), Credential.RC_SIGN_IN)
        }

        val btnLoginFb = findViewById<FancyButton>(R.id.btn_login_fb)
        btnLoginFb.setOnClickListener {
            authFacebook.fbLoginManager
                    .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        }
    }

    override fun onStart() {
        super.onStart()
        authController.redirect()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Credential.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                authGoogle.authWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(Tag.TAG_GOOGLE_SIGN_IN, "Google sign in failed", e)
                Toasty.error(this, getString(R.string.error_auth)).show()
                // ...
            }
        }
        // Facebook callback
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
