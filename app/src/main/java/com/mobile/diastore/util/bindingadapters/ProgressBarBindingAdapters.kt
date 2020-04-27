package com.mobile.diastore.util.bindingadapters

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.isVisible(isVisible: Boolean?) {
    visibility = if (isVisible == true) {
        View.VISIBLE
    } else {
        View.GONE
    }
}