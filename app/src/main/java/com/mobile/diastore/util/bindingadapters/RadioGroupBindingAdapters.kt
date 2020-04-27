package com.mobile.diastore.util.bindingadapters

import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.mobile.diastore.R
import com.mobile.diastore.model.MealTypeSpecifier
import com.mobile.diastore.model.MomentSpecifier

@BindingAdapter("entryMoment")
fun RadioGroup.selectEntryMoment(entryMomentSpecifier: MomentSpecifier?) {
    when(entryMomentSpecifier) {
        MomentSpecifier.BEFORE_MEAL -> this.check(R.id.before)
        MomentSpecifier.AFTER_MEAL -> this.check(R.id.after)
    }

}

@BindingAdapter("mealType")
fun RadioGroup.selectMealType(mealTypeSpecifier: MealTypeSpecifier?) {
    when(mealTypeSpecifier) {
        MealTypeSpecifier.BREAKFAST -> this.check(R.id.breakfast)
        MealTypeSpecifier.LUNCH -> this.check(R.id.lunch)
        MealTypeSpecifier.DINNER -> this.check(R.id.dinner)
        MealTypeSpecifier.SNACK -> this.check(R.id.snack)
    }
}