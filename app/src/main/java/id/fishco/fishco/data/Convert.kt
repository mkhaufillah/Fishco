/*
 * Created by mkhaufillah on 6/4/18 2:53 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/4/18 2:53 PM
 */

package id.fishco.fishco.data

import android.content.Context
import id.fishco.fishco.R
import java.text.DecimalFormat
import java.util.Date

class Convert {
    companion object {
        /*
         * Copyright 2012 Google Inc.
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */

        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS

        fun getTimeAgo(timePar: Long, context: Context): String? {
            var time = timePar
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }

            val date = Date()
            val now = date.time
            if (time > now || time <= 0) {
                return null
            }

            val diff = now - time
            return if (diff < MINUTE_MILLIS) {
                context.getString(R.string.just_now)
            } else if (diff < 2 * MINUTE_MILLIS) {
                context.getString(R.string.a_minute_ago)
            } else if (diff < 50 * MINUTE_MILLIS) {
                "${diff / MINUTE_MILLIS} " + context.getString(R.string.minutes_ago)
            } else if (diff < 90 * MINUTE_MILLIS) {
                context.getString(R.string.an_hour_ago)
            } else if (diff < 24 * HOUR_MILLIS) {
                "${diff / HOUR_MILLIS} " + context.getString(R.string.hours_ago)
            } else if (diff < 48 * HOUR_MILLIS) {
                context.getString(R.string.yesterday)
            } else {
                "${diff / DAY_MILLIS} " + context.getString(R.string.days_ago)
            }
        }

        fun compressIDR(value: Double, context: Context): String {
            val df = DecimalFormat("#.##")
            val price: String
            var pricePrint = value
            var priceCount = pricePrint / 1000

            if (priceCount <= 1) {
                price = "Rp. ${df.format(pricePrint)}"
            } else {
                pricePrint = priceCount
                priceCount /= 1000
                if (priceCount <= 1) {
                    price = "Rp. ${df.format(pricePrint)} " + context.getString(R.string.th)
                } else {
                    pricePrint = priceCount
                    priceCount /= 1000
                    if (priceCount <= 1) {
                        price = "Rp. ${df.format(pricePrint)} " + context.getString(R.string.m)
                    } else {
                        pricePrint = priceCount
                        priceCount /= 1000
                        if (priceCount <= 1) {
                            price = "Rp. ${df.format(pricePrint)} " + context.getString(R.string.b)
                        } else {
                            if (priceCount <= 1) {
                                pricePrint = priceCount
                                priceCount /= 1000
                                price = "Rp. ${df.format(pricePrint)} " + context.getString(R.string.t)
                            } else {
                                val v = "$value"
                                price = "Rp. ${v.substring(0, 10)}..."
                            }
                        }
                    }
                }
            }

            return price
        }
    }
}