package ua.com.andromeda.wordgalaxy.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import ua.com.andromeda.wordgalaxy.data.model.AudioUrl

fun Context.playPronunciation(audioUrls: List<AudioUrl>) {
    val notBlankAndUniqueAudios = audioUrls.filter { it.isNotBlank() }.distinct()
    if (notBlankAndUniqueAudios.isEmpty()) return

    val mediaPlayer = MediaPlayer()
    with(mediaPlayer) {
        setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )

        notBlankAndUniqueAudios.forEach { audio ->
            setDataSource(this@playPronunciation, Uri.parse(audio))
            prepareAsync()
            setOnPreparedListener { it.start() }
        }
    }
}
