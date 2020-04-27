package com.mobile.diastore.feature.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.diastore.EntryDetailsBinding
import com.mobile.diastore.R
import com.mobile.diastore.feature.home.EntriesSharedViewModel
import com.mobile.diastore.model.Entry
import com.mobile.diastore.model.MealTypeSpecifier
import com.mobile.diastore.model.MomentSpecifier
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.UUID

class EntryDetailsFragment : Fragment() {
    private val args: EntryDetailsFragmentArgs by navArgs()
    private lateinit var binding: EntryDetailsBinding
    private val entriesSharedViewModel by sharedViewModel<EntriesSharedViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        DataBindingUtil.inflate<EntryDetailsBinding>(inflater, R.layout.fragment_entry_details, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        entriesSharedViewModel.setSelectedEntry(args.entry)
        binding.viewModel = entriesSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            val entry = entriesSharedViewModel.selectedEntry.value
            if (entry != null) {
                entriesSharedViewModel.setSelectedEntry(
                    Entry(
                        id = entry.id,
                        bloodSugarLevel = entriesSharedViewModel.bloodSugarLevel.value?.toIntOrNull() ?: 0,
                        carbohydratesIntake = entriesSharedViewModel.carbohydratesIntake.value?.toIntOrNull() ?: 0,
                        insulinIntake = entriesSharedViewModel.insulinIntake.value?.toFloatOrNull() ?: 0F,
                        entryTime = entry.entryTime,
                        entryHour = entry.entryHour,
                        physicalActivityDuration = entriesSharedViewModel.physicalActivityDuration.value?.toIntOrNull() ?: 0,
                        entryMomentSpecifier = binding.entryMomentGroup.entryTimeIdToEntryTime(),
                        mealTypeSpecifier = binding.mealTypeGroup.mealTypeIdToMealType()
                    )
                )
            } else {
                entriesSharedViewModel.setSelectedEntry(
                    Entry(
                        bloodSugarLevel = entriesSharedViewModel.bloodSugarLevel.value?.toIntOrNull() ?: 0,
                        carbohydratesIntake = entriesSharedViewModel.carbohydratesIntake.value?.toIntOrNull() ?: 0,
                        insulinIntake = entriesSharedViewModel.insulinIntake.value?.toFloatOrNull() ?: 0F,
                        entryTime = "13:37",
                        entryHour = "10/10/19",
                        physicalActivityDuration = entriesSharedViewModel.physicalActivityDuration.value?.toIntOrNull() ?: 0,
                        entryMomentSpecifier = binding.entryMomentGroup.entryTimeIdToEntryTime(),
                        mealTypeSpecifier = binding.mealTypeGroup.mealTypeIdToMealType()
                    )
                )
            }
            findNavController().navigateUp()
        }

        entriesSharedViewModel.selectedEntry.observe(viewLifecycleOwner, Observer {
            Log.d("WRKR", it.toString())
            entriesSharedViewModel.bloodSugarLevel.value = if (it?.bloodSugarLevel == null) "0" else it.bloodSugarLevel.toString()
            entriesSharedViewModel.carbohydratesIntake.value = if (it?.carbohydratesIntake == null) "0" else it.carbohydratesIntake.toString()
            entriesSharedViewModel.insulinIntake.value = if (it?.insulinIntake == null) "0" else it.insulinIntake.toString()
            entriesSharedViewModel.physicalActivityDuration.value = if (it?.physicalActivityDuration == null) "0" else it.physicalActivityDuration.toString()
        })
    }

    companion object {
        fun RadioGroup.entryTimeIdToEntryTime(): MomentSpecifier? =
            when (checkedRadioButtonId) {
                R.id.before -> MomentSpecifier.BEFORE_MEAL
                R.id.after -> MomentSpecifier.AFTER_MEAL
                else -> null
            }

        fun RadioGroup.mealTypeIdToMealType(): MealTypeSpecifier? =
            when (checkedRadioButtonId) {
                R.id.breakfast -> MealTypeSpecifier.BREAKFAST
                R.id.lunch -> MealTypeSpecifier.LUNCH
                R.id.dinner -> MealTypeSpecifier.DINNER
                R.id.snack -> MealTypeSpecifier.SNACK
                else -> null
            }
    }
}