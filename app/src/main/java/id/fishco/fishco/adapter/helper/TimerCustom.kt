/*
 * Created by mkhaufillah on 6/21/18 9:53 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/21/18 9:53 PM
 */

package id.fishco.fishco.adapter.helper

import android.app.Activity
import android.os.CountDownTimer
import android.widget.TextView
import es.dmoral.toasty.Toasty
import id.fishco.fishco.R
import java.util.concurrent.TimeUnit

class TimerCustom(millisInFuture: Long, countDownInterval: Long, private var time: TextView, private var activity: Activity)
    : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        val text = String.format("%02d Hari %02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
        time.text = text
    }

    override fun onFinish() {
        Toasty.error(activity, activity.getString(R.string.time_is_out))
        activity.finish()
    }
}
