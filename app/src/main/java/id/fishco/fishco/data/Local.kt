/*
 * Created by mkhaufillah on 5/29/18 10:00 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 12:45 AM
 */

package id.fishco.fishco.data

import android.content.Context
import id.fishco.fishco.model.BannerContainer
import id.fishco.fishco.model.ProductContainer
import id.fishco.fishco.model.UserContainer
import android.app.Activity
import android.preference.PreferenceManager
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Local {
    companion object {
        private var deviceWidth: Int = 0
        val banners = ArrayList<BannerContainer>()
        val topProducts = ArrayList<ProductContainer>()
        var user = UserContainer()

        fun getDeviceWidth(context: Context): Int {
            if (deviceWidth == 0) {
                val dm = context.resources.displayMetrics
                deviceWidth = dm.widthPixels
            }
            return deviceWidth
        }

        fun addBanner(banner: BannerContainer) {
            for (b in banners) {
                if (banner.id.equals(b.id)) return
            }
            banners.add(banner)
        }

        fun addTopProduct(product: ProductContainer) {
            for (tp in topProducts) {
                if (product.id.equals(tp.id)) return
            }
            topProducts.add(product)
        }

        fun resetBannerAndTopProduct() {
            banners.clear()
            topProducts.clear()
        }

        fun saveArrayList(list: ArrayList<String>?, key: String, activity:Activity) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            val gson = Gson()
            val json = gson.toJson(list)
            editor.putString(key, json)
            editor.apply()
        }

        fun getArrayList(key: String, activity:Activity): ArrayList<String>? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val gson = Gson()
            val json = prefs.getString(key, null)
            val type = object : TypeToken<ArrayList<String>>() {}.type
            return gson.fromJson(json, type)
        }
    }
}