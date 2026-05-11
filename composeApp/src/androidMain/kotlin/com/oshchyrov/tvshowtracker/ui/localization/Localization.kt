package com.oshchyrov.tvshowtracker.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.util.Strings

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun localizedString(key: String): String = Strings.get(key, LocalAppLanguage.current)
