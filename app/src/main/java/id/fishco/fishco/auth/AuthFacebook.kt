/*
 * Created by mkhaufillah on 5/29/18 10:34 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/8/18 3:47 PM
 */

package id.fishco.fishco.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import id.fishco.fishco.data.Tag
import es.dmoral.toasty.Toasty

class AuthFacebook(private val mAuth: FirebaseAuth, private val context: Context, private val messageError: String) {

    var fbLoginManager: LoginManager = LoginManager.getInstance()
    val mCallbackManager: CallbackManager = CallbackManager.Factory.create()

    init {
        // Initialize Facebook Login button
        fbLoginManager.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(Tag.TAG_FACEBOOK_SIGN_IN, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(Tag.TAG_FACEBOOK_SIGN_IN, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(Tag.TAG_FACEBOOK_SIGN_IN, "facebook:onError", error)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(Tag.TAG_FACEBOOK_SIGN_IN, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(Tag.TAG_FACEBOOK_SIGN_IN, "signInWithCredential:success")
                        val user = mAuth.currentUser
                        val controllerAuth = AuthController(context, user)
                        controllerAuth.redirect()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(Tag.TAG_FACEBOOK_SIGN_IN, "signInWithCredential:failure", it.getException())
                        Toasty.error(context, messageError, Toast.LENGTH_SHORT).show()
                        val controllerAuth = AuthController(context, null)
                        controllerAuth.redirect()
                    }
                }
    }

}