package dipen.kotlin.contactpicker.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import dipen.kotlin.contactpicker.R

fun String.Companion.empty() = ""

fun Context.openAppSettingPage() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}

fun Context.showRationaleDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    function: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            function.invoke()
        }
        .create()
        .show()
}