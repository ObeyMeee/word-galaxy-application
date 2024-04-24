package ua.com.andromeda.wordgalaxy.core.data.pref

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    const val DARK_THEME = "dark_theme"
    val KEY_DARK_THEME = booleanPreferencesKey(DARK_THEME)

    const val ANIMATION_ENABLED = "animation_enabled"
    val KEY_ANIMATION_ENABLED = booleanPreferencesKey(ANIMATION_ENABLED)

    const val TRANSCRIPTIONS_ENABLED = "transcriptions_enabled"
    val KEY_TRANSCRIPTIONS_ENABLED = booleanPreferencesKey(TRANSCRIPTIONS_ENABLED)
    const val DEFAULT_TRANSCRIPTIONS_ENABLED = true

    const val PRONOUNCE_ENGLISH_WORDS = "automatically_pronounce_english_words"
    val KEY_PRONOUNCE_ENGLISH_WORDS = booleanPreferencesKey(PRONOUNCE_ENGLISH_WORDS)
    const val DEFAULT_PRONOUNCE_ENGLISH_WORDS = true

    const val AMOUNT_WORDS_TO_LEARN_PER_DAY = "amount_words_to_learn_per_day"
    val KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY = stringPreferencesKey(AMOUNT_WORDS_TO_LEARN_PER_DAY)
    const val DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY = 10
    const val MIN_AMOUNT_WORDS_TO_LEARN_PER_DAY = 1
    const val MAX_AMOUNT_WORDS_TO_LEARN_PER_DAY = 50

    const val NOTIFICATIONS_FREQUENCY = "notifications_frequency"
    val KEY_NOTIFICATIONS_FREQUENCY = stringPreferencesKey(NOTIFICATIONS_FREQUENCY)
    const val DEFAULT_NOTIFICATIONS_FREQUENCY = 6
    const val MIN_DEFAULT_NOTIFICATIONS_FREQUENCY = 2
    const val MAX_DEFAULT_NOTIFICATIONS_FREQUENCY = 12

}

