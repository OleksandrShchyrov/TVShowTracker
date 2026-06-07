package com.oshchyrov.tvshowtracker.util

import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {

    @Test
    fun englishReturnsTranslation() {
        assertEquals("Search Shows", Strings.get("search_shows", AppLanguage.ENGLISH))
    }

    @Test
    fun ukrainianReturnsTranslation() {
        assertEquals("Пошук серіалів", Strings.get("search_shows", AppLanguage.UKRAINIAN))
    }

    @Test
    fun ukrainianUsesUkrainianTranslationWhenAvailable() {
        assertEquals("Каталог серіалів", Strings.get("browse_shows", AppLanguage.UKRAINIAN))
    }

    @Test
    fun unknownKeyReturnsKeyItself() {
        assertEquals("unknown_key", Strings.get("unknown_key", AppLanguage.ENGLISH))
    }
}
