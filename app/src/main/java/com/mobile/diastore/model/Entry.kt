package com.mobile.diastore.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@JsonClass(generateAdapter = true)
@Entity(tableName = "entries")
@Parcelize
data class Entry(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    val bloodSugarLevel: Int,
    val carbohydratesIntake: Int,
    val insulinIntake: Float,
    val entryTime: String,
    val entryHour: String,
    val physicalActivityDuration: Int = 0,
    val entryMomentSpecifier: MomentSpecifier? = null,
    val mealTypeSpecifier: MealTypeSpecifier? = null
): Parcelable

enum class MomentSpecifier {
    BEFORE_MEAL, AFTER_MEAL
}

enum class MealTypeSpecifier {
    BREAKFAST, LUNCH, DINNER, SNACK
}