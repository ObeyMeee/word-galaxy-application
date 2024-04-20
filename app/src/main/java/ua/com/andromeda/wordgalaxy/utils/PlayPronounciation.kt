package ua.com.andromeda.wordgalaxy.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import ua.com.andromeda.wordgalaxy.core.domain.model.AudioUrl
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic

private var currentMediaPlayer: MediaPlayer? = null


fun Context.playPronunciation(phonetics: List<Phonetic>) {
    val audioUrls = phonetics
        .asSequence()
        .map { it.audio }
        .distinct()
        .filter { it.isNotBlank() }
        .toList()

    playAudioFilesSequentially(audioUrls)
}

private fun Context.playAudioFilesSequentially(audioUrls: List<AudioUrl>) {
    if (audioUrls.isEmpty()) return

    // Stop the currently playing MediaPlayer if exists
    currentMediaPlayer?.apply {
        stop()
        release()
    }

    val mediaPlayer = MediaPlayer()
    mediaPlayer.apply {
        setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        setDataSource(this@playAudioFilesSequentially, Uri.parse(audioUrls.first()))
        prepareAsync()
        setOnPreparedListener {
            it.start()
        }
        setOnCompletionListener {
            // Release the current MediaPlayer before playing the next audio
            it.release()
            currentMediaPlayer = null
            playAudioFilesSequentially(audioUrls.drop(1))
        }

        // Set the current MediaPlayer for reference
        currentMediaPlayer = mediaPlayer
    }
}
