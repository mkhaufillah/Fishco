/*
 * Created by mkhaufillah on 5/29/18 10:00 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 12:36 AM
 */

package id.fishco.fishco.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import id.fishco.fishco.MainActivity

class AuthController(private val context: Context, private val user: FirebaseUser?) {

    fun auth() {
        if (user == null) {
            val intent = Intent(context, AuthActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    fun redirect() {
        if (user != null) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

}