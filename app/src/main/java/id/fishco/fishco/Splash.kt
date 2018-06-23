/*
 * Created by mkhaufillah on 5/29/18 10:01 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/29/18 9:57 PM
 */

package id.fishco.fishco

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}