package ua.com.andromeda.wordgalaxy.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun Context.shareLink(uriString: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, uriString)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share link with...")
    startActivity(shareIntent)
}

fun Context.openLink(uriString: String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(uriString)
    )
    startActivity(intent)
}

fun Context.sendEmail(to: String, subject: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.type = "message/rfc822"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show()
    }
}