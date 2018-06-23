/*
 * Created by mkhaufillah on 5/29/18 10:33 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/8/18 12:03 PM
 */

package id.fishco.fishco.auth

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import id.fishco.fishco.data.Tag
import es.dmoral.toasty.Toasty
import id.fishco.fishco.data.Credential

class AuthGoogle(private val mAuth: FirebaseAuth, private val context: Context, private val messageError: String) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Credential.WEB_CLIENT_ID)
            .requestEmail()
            .build()

    fun getIntent(activity: Activity):Intent {
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        return mGoogleSignInClient.signInIntent
    }

    fun authWithGoogle(acct: GoogleSignInAccount) {
        Log.d(Tag.TAG_GOOGLE_SIGN_IN, "authWithGoogle:" + acct.id)

        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(Tag.TAG_GOOGLE_SIGN_IN, "signInWithCredential:success")
                        val user: FirebaseUser? = mAuth.currentUser
                        val controllerActivity = AuthController(context, user)
                        controllerActivity.redirect()
                    } else {
                        Log.w(Tag.TAG_GOOGLE_SIGN_IN, "signInWithCredential:failure", it.exception)
                        Toasty.error(context, messageError, Toast.LENGTH_SHORT)
                        val controllerActivity = AuthController(context, null)
                        controllerActivity.redirect()
                    }
                }
    }
}