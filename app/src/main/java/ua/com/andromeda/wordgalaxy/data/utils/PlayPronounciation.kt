package ua.com.andromeda.wordgalaxy.data.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

fun Context.playPronunciation(audioUrls: List<String>) {
    val mediaPlayer = MediaPlayer()
    with(mediaPlayer) {
        setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )

        audioUrls.forEach { audio ->
            setDataSource(this@playPronunciation, Uri.parse(audio))
            prepareAsync()
            setOnPreparedListener { it.start() }
        }
    }
}
