package com.hydration.app.data

object HydrationCalculator {

    fun calculateDailyGoal(weightKg: Double, activityLevel: String, climate: String): Int {
        val baseline = weightKg * 35.0

        val activityBonus = when (activityLevel.lowercase()) {
            "moderate" -> 350.0
            "active" -> 700.0
            else -> 0.0 // sedentary
        }

        val climateBonus = when (climate.lowercase()) {
            "temperate" -> 350.0
            "hot" -> 600.0
            else -> 0.0 // cold
        }

        return (baseline + activityBonus + climateBonus).toInt()
    }

    fun calculateEffectiveHydration(amountMl: Int, beverageType: String): Int {
        return when (beverageType.lowercase()) {
            "coffee", "tea" -> (amountMl * 0.5).toInt() // 50% deduction due to diuretic effect
            "juice" -> amountMl // 100%
            else -> amountMl // water
        }
    }
}
