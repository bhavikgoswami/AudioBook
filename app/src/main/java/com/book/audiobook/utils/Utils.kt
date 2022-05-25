package com.book.audiobook.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import com.book.audiobook.R

/**
 * This Utils class contains all the common method which are used multiple times in application.
 */
class Utils {
    companion object {
        private var dialog: Dialog? = null

        /**
         * This method checks the Internet Connectivity
         *
         * @param context : Context
         * @return boolean value based on internet connectivity status
         */
        fun checkInternetConnection(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }

        /**
         * This method shows progress dialog
         *
         * @param context : Context
         */
        fun showProgressDialog(context: Context) {
            dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog?.setContentView(inflate)
            dialog?.setCancelable(false)
            dialog?.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            dialog?.show()
        }

        /**
         * This method hides the progress dialog if its showing
         *
         */
        fun hideProgressDialog() {
            if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
        }

    }
}