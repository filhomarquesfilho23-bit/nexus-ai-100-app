package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Armazenamento local privado no dispositivo (SharedPreferences)
 * para salvar ferramentas favoritas, histórico dos últimos 5 sites gerados,
 * contador diário de usos gratuitos e status de desbloqueio VIP.
 */
class LocalPreferencesStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavoriteToolIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITE_TOOLS, emptySet())?.toSet() ?: emptySet()
    }

    fun saveFavoriteToolIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITE_TOOLS, ids).apply()
    }

    fun isVipUnlocked(): Boolean {
        return prefs.getBoolean(KEY_VIP_UNLOCKED, false)
    }

    fun activateVipWithCode(code: String): Boolean {
        val normalized = code.trim().uppercase(Locale.US)
        if (normalized == MASTER_VIP_CODE) {
            prefs.edit().putBoolean(KEY_VIP_UNLOCKED, true).apply()
            return true
        }
        return false
    }

    fun getRemainingFreeUsesToday(todayKey: String = currentDateKey()): Int {
        if (isVipUnlocked()) return MAX_FREE_DAILY_USES
        val used = getUsedCountForDate(todayKey)
        return (MAX_FREE_DAILY_USES - used).coerceAtLeast(0)
    }

    fun hasFreeUsesRemainingToday(todayKey: String = currentDateKey()): Boolean {
        return isVipUnlocked() || getRemainingFreeUsesToday(todayKey) > 0
    }

    fun consumeFreeUseToday(todayKey: String = currentDateKey()): Int {
        if (isVipUnlocked()) return MAX_FREE_DAILY_USES
        val currentUsed = getUsedCountForDate(todayKey)
        val nextUsed = (currentUsed + 1).coerceAtMost(MAX_FREE_DAILY_USES)
        prefs.edit()
            .putString(KEY_DAILY_USAGE_DATE, todayKey)
            .putInt(KEY_DAILY_USAGE_COUNT, nextUsed)
            .apply()
        return (MAX_FREE_DAILY_USES - nextUsed).coerceAtLeast(0)
    }

    private fun getUsedCountForDate(todayKey: String): Int {
        val savedDate = prefs.getString(KEY_DAILY_USAGE_DATE, null)
        if (savedDate != todayKey) {
            prefs.edit()
                .putString(KEY_DAILY_USAGE_DATE, todayKey)
                .putInt(KEY_DAILY_USAGE_COUNT, 0)
                .apply()
            return 0
        }
        return prefs.getInt(KEY_DAILY_USAGE_COUNT, 0).coerceIn(0, MAX_FREE_DAILY_USES)
    }

    fun getSiteHistory(): List<SavedSiteEntry> {
        val raw = prefs.getString(KEY_SITE_HISTORY, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val obj = array.optJSONObject(index) ?: return@mapNotNull null
                SavedSiteEntry(
                    id = obj.optString("id"),
                    companyName = obj.optString("companyName"),
                    niche = obj.optString("niche"),
                    whatsapp = obj.optString("whatsapp"),
                    cityState = obj.optString("cityState"),
                    services = obj.optString("services"),
                    htmlCode = obj.optString("htmlCode"),
                    createdAtMillis = obj.optLong("createdAtMillis", 0L)
                ).takeIf { it.id.isNotBlank() && it.htmlCode.isNotBlank() }
            }
        }.getOrDefault(emptyList())
    }

    fun saveSiteToHistory(entry: SavedSiteEntry): List<SavedSiteEntry> {
        val current = getSiteHistory().filterNot { it.id == entry.id }
        val updated = (listOf(entry) + current).take(MAX_HISTORY_SITES)
        persistHistory(updated)
        return updated
    }

    fun removeSiteFromHistory(id: String): List<SavedSiteEntry> {
        val updated = getSiteHistory().filterNot { it.id == id }
        persistHistory(updated)
        return updated
    }

    private fun persistHistory(entries: List<SavedSiteEntry>) {
        val array = JSONArray()
        entries.take(MAX_HISTORY_SITES).forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("companyName", item.companyName)
                put("niche", item.niche)
                put("whatsapp", item.whatsapp)
                put("cityState", item.cityState)
                put("services", item.services)
                put("htmlCode", item.htmlCode)
                put("createdAtMillis", item.createdAtMillis)
            })
        }
        prefs.edit().putString(KEY_SITE_HISTORY, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "nexus_ai_100_local_storage"
        private const val KEY_FAVORITE_TOOLS = "favorite_tool_ids"
        private const val KEY_SITE_HISTORY = "generated_site_history_v1"
        private const val KEY_DAILY_USAGE_DATE = "daily_free_usage_date"
        private const val KEY_DAILY_USAGE_COUNT = "daily_free_usage_count"
        private const val KEY_VIP_UNLOCKED = "vip_unlimited_unlocked"
        private const val MAX_HISTORY_SITES = 5
        const val MAX_FREE_DAILY_USES = 3
        const val MASTER_VIP_CODE = "NEXUS2026"
        const val MERCADO_PAGO_VIP_URL = "https://mpago.la/13o8xzR"

        fun currentDateKey(now: Date = Date()): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        }
    }
}
