/*
 * Created by mkhaufillah on 6/23/18 10:24 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/23/18 10:02 PM
 */

package id.fishco.fishco.transactions

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import id.fishco.fishco.R
import com.google.firebase.storage.FirebaseStorage
import es.dmoral.toasty.Toasty
import id.fishco.fishco.data.Credential
import java.util.*
import kotlin.math.roundToInt

class ImageUploadContainerActivity : AppCompatActivity() {

    private lateinit var text: TextView
    private lateinit var image: ImageView
    val storage = FirebaseStorage.getInstance()
    val sr = storage.reference
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), Credential.CODE_UPLOAD)
        setContentView(R.layout.activity_image_upload_container)
        text = findViewById(R.id.tv_progress)
        image = findViewById(R.id.iv_progress)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Credential.CODE_UPLOAD && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            image.setImageBitmap(bitmap)
            val date = Date()
            val imagesRef = sr.child("confirmationPay/${intent.getStringExtra(Credential.KEY_SHARE)}/image${date.time}.jpg")
            imagesRef.putFile(filePath)
                    .addOnSuccessListener {
                        val c = db.collection("transactions")
                        imagesRef.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                c.document(intent.getStringExtra(Credential.KEY_SHARE))
                                        .update("status", 1,
                                                "imageConfirmReceiptStg", "confirmationPay/${intent.getStringExtra(Credential.KEY_SHARE)}/image${date.time}.jpg",
                                                "imageConfirmReceipt", it.result.toString())
                                        .addOnCompleteListener {
                                            Toasty.success(this, getString(R.string.success_upload)).show()
                                            val resultIntent = Intent()
                                            setResult(Activity.RESULT_OK, resultIntent)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toasty.error(this, getString(R.string.fail_upload)).show()
                                            val resultIntent = Intent()
                                            setResult(Activity.RESULT_CANCELED, resultIntent)
                                            finish()
                                        }
                            } else {
                                Toasty.error(this, getString(R.string.fail_upload)).show()
                                val resultIntent = Intent()
                                setResult(Activity.RESULT_CANCELED, resultIntent)
                                finish()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toasty.error(this, getString(R.string.fail_upload)).show()
                        val resultIntent = Intent()
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                        finish()
                    }
                    .addOnProgressListener {
                        val progress = (100.0 * it.bytesTransferred/it.totalByteCount)
                        val progressVal = progress.roundToInt().toString() + getString(R.string.wait_upload)
                        text.text = progressVal
                    }
        }
    }
}
