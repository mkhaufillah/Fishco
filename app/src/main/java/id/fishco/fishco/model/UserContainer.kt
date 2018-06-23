/*
 * Created by mkhaufillah on 6/1/18 11:10 AM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/1/18 11:10 AM
 */

package id.fishco.fishco.model

import java.util.Date

class UserContainer : User() {
    var id: String? = null
    var phone: String? = null
    var gender: String? = null
    var birthDate: Date? = null
    var photo: String? = null
    var photoStg: String? = null
    var cover: String? = null
    var coverStg: String? = null
    var idCard: String? = null
    var phoneVerified: Boolean? = null
}