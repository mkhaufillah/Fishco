/*
 * Created by mkhaufillah on 6/1/18 8:23 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/1/18 8:23 AM
 */

package id.fishco.fishco.model

import java.util.Date

open class Transaction {
    var product: String? = null
    var buyer: String? = null
    var address: Address? = null
    var qty: Int? = null
    var desc: String? = null
    var productCost: Double? = null
    var courierCost: Double? = null
    var courierType: String? = null
    var status: Int? = null
    var totalCost: Double? = null
    var payMethod: String? = null
    var exp: Date? = null
    var resi: String? = null
    var timestamp: Date? = null
}