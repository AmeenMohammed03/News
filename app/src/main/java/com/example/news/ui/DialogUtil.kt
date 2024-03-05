package com.example.news.ui

import android.app.AlertDialog
import android.content.Context

object DialogUtil {

    fun showDialog(context: Context, title: String, message: String): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}