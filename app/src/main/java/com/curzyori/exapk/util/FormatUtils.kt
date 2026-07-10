package com.curzyori.exapk.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val sizeFormatter = DecimalFormat("#.##")

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${sizeFormatter.format(bytes / 1024.0)} KB"
            bytes < 1024 * 1024 * 1024 -> "${sizeFormatter.format(bytes / (1024.0 * 1024))} MB"
            else -> "${sizeFormatter.format(bytes / (1024.0 * 1024 * 1024))} GB"
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
