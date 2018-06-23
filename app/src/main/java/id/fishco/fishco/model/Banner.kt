/*
 * Created by mkhaufillah on 5/30/18 6:29 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/30/18 6:29 PM
 */

package id.fishco.fishco.model

import java.util.Date

open class Banner {
    var url: String? = null
    var urlStg: String? = null
    var name: String? = null
    var desc: String? = null
    var publisher: String? = null
    var idCardPublisher: String? = null
    var expired: Date? = null
    var timestamp: Date? = null
}