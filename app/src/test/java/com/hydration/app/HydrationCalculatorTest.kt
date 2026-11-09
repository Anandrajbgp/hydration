package com.hydration.app

import com.hydration.app.data.HydrationCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class HydrationCalculatorTest {

    @Test
    fun testCalculateDailyGoal_sedentaryCold() {
        // Weight 70kg * 35 = 2450. Sedentary (+0), Cold (+0) = 2450
        val goal = HydrationCalculator.calculateDailyGoal(70.0, "sedentary", "cold")
        assertEquals(2450, goal)
    }

    @Test
    fun testCalculateDailyGoal_activeHot() {
        // Weight 60kg * 35 = 2100. Active (+700), Hot (+600) = 3400
        val goal = HydrationCalculator.calculateDailyGoal(60.0, "active", "hot")
        assertEquals(3400, goal)
    }

    @Test
    fun testCalculateEffectiveHydration_coffee() {
        // Coffee 300ml * 0.5 = 150ml effective
        val effective = HydrationCalculator.calculateEffectiveHydration(300, "coffee")
        assertEquals(150, effective)
    }

    @Test
    fun testCalculateEffectiveHydration_water() {
        // Water 250ml = 250ml effective
        val effective = HydrationCalculator.calculateEffectiveHydration(250, "water")
        assertEquals(250, effective)
    }
}
