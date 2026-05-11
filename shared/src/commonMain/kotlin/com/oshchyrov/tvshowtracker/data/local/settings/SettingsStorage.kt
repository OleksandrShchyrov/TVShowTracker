package com.oshchyrov.tvshowtracker.data.local.settings

interface SettingsStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}
