package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocalPreferencesStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalPreferencesStoreTest {

    private lateinit var store: LocalPreferencesStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("nexus_ai_100_local_storage", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        store = LocalPreferencesStore(context)
    }

    @Test
    fun dailyQuotaCountsDownFromThreeAndResetsNextDay() {
        val day1 = "2026-11-15"
        val day2 = "2026-11-16"

        assertEquals(3, store.getRemainingFreeUsesToday(day1))
        assertTrue(store.hasFreeUsesRemainingToday(day1))

        assertEquals(2, store.consumeFreeUseToday(day1))
        assertEquals(1, store.consumeFreeUseToday(day1))
        assertEquals(0, store.consumeFreeUseToday(day1))
        assertFalse(store.hasFreeUsesRemainingToday(day1))

        // New day resets back to 3/3
        assertEquals(3, store.getRemainingFreeUsesToday(day2))
        assertTrue(store.hasFreeUsesRemainingToday(day2))
    }

    @Test
    fun vipActivationRequiresMasterCodeAndUnlocksUnlimitedUses() {
        val day = "2026-11-15"
        assertFalse(store.activateVipWithCode("INVALIDO"))
        assertFalse(store.isVipUnlocked())

        // Exhaust free daily credits
        store.consumeFreeUseToday(day)
        store.consumeFreeUseToday(day)
        store.consumeFreeUseToday(day)
        assertFalse(store.hasFreeUsesRemainingToday(day))

        // Activate with master code NEXUS2026 (trimmed / case-insensitive)
        assertTrue(store.activateVipWithCode(" nexus2026 "))
        assertTrue(store.isVipUnlocked())
        assertTrue(store.hasFreeUsesRemainingToday(day))
        assertEquals(3, store.getRemainingFreeUsesToday(day))
    }
}
