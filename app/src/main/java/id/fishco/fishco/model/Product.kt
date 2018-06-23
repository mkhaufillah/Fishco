/*
 * Created by mkhaufillah on 5/29/18 10:01 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 12:44 AM
 */

package id.fishco.fishco.model

import java.util.Date

open class Product {
    var fisherman: String? = null
    var address: Address? = null
    var name: String? = null
    var price: Double? = null
    var qty: Int? = null
    var weight: Int? = null
    var desc: String? = null
    var category: Int? = null
    var sold: Int? = null
    var seeing: Int? = null
    var avgStar: Float? = null
    var photo: ArrayList<String>? = null
    var photoStg: ArrayList<String>? = null
    var listCourier: ArrayList<String>? = null
    var timestamp: Date? = null
}