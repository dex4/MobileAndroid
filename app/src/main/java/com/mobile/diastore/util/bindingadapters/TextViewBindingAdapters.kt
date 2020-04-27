package com.mobile.diastore.util.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobile.diastore.R
import com.mobile.diastore.model.MealTypeSpecifier
import com.mobile.diastore.model.MomentSpecifier

@BindingAdapter("carbs")
fun TextView.setCarbs(carbs: Int) {
    text = context.getString(R.string.carbs_template, carbs)
}

@BindingAdapter("insulin")
fun TextView.setInsulin(insulin: Float) {
    text = context.getString(R.string.insulin_template, insulin)
}

@BindingAdapter("sport")
fun TextView.setPhysicalActivity(sport: Int) {
    text = context.getString(R.string.sport_template, sport)
}

@BindingAdapter("momentType", "mealType", requireAll = false)
fun TextView.setMealMomentText(momentType: MomentSpecifier?, mealType: MealTypeSpecifier?) {
    var mealMoment =
        when (momentType) {
            MomentSpecifier.BEFORE_MEAL -> context.getString(R.string.moment_before)
            MomentSpecifier.AFTER_MEAL -> context.getString(R.string.moment_after)
            else -> ""
        }
    mealMoment += when (mealType) {
        MealTypeSpecifier.BREAKFAST -> "Breakfast"
        MealTypeSpecifier.LUNCH -> "Lunch"
        MealTypeSpecifier.DINNER -> "Dinner"
        MealTypeSpecifier.SNACK -> "Snack"
        null -> ""
    }
    text = mealMoment
}