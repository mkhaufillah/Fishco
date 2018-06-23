/*
 * Created by mkhaufillah on 6/23/18 11:35 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/23/18 11:35 AM
 */

package id.fishco.fishco.adapter.helper

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ImageUploadContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
    }
}
