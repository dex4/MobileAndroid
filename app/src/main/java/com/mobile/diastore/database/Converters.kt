package com.mobile.diastore.database

import androidx.room.TypeConverter
import com.mobile.diastore.model.MealTypeSpecifier
import com.mobile.diastore.model.MomentSpecifier

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromMomentSpecifier(momentSpecifier: MomentSpecifier): String =
            momentSpecifier.toString()

        @TypeConverter
        @JvmStatic
        fun toMomentSpecifier(momentSpecifierString: String): MomentSpecifier =
            MomentSpecifier.valueOf(momentSpecifierString)

        @TypeConverter
        @JvmStatic
        fun fromMealTypeSpecifier(mealTypeSpecifier: MealTypeSpecifier): String =
            mealTypeSpecifier.toString()

        @TypeConverter
        @JvmStatic
        fun toMealTypeSpecifier(mealTypeSpecifierString: String): MealTypeSpecifier =
            MealTypeSpecifier.valueOf(mealTypeSpecifierString)
    }
}