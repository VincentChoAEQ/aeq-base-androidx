/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog

object ActivityUtils {

    fun hideSoftInput(view: View?) {
        if (view != null && view.windowVisibility == View.VISIBLE) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showForcedUpgradeDialog(context: Context) {
        AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Need a upgrade")
            .setMessage("Go to google play upgrade your app?")
            .setPositiveButton("Yes") { _, _ ->
                //set what would happen when positive button is clicked
                //todo go to google play for upgrade application
                (context as Activity).finish()
            }
            .setNegativeButton("No") { _, _ ->
                //set what should happen when negative button is clicked
                (context as Activity).finish()
            }
            .show()
    }
}
