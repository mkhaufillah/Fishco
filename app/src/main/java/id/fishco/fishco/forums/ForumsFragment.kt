/*
 * Created by mkhaufillah on 5/29/18 10:00 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 5/27/18 1:06 AM
 */

package id.fishco.fishco.forums

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import id.fishco.fishco.forums.addForums.AddForumsActivity
import id.fishco.fishco.forums.scanIdForums.ScanIdForumsActivity
import id.fishco.fishco.R

class ForumsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forums, container, false)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.forums_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_forums -> {
                val intent = Intent(context, AddForumsActivity::class.java)
                context?.startActivity(intent)
                return true
            }
            R.id.scan_id_forums -> {
                val intent = Intent(context, ScanIdForumsActivity::class.java)
                context?.startActivity(intent)
                return true
            }
        }

        return false
    }

}
