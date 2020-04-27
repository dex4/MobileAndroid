package com.mobile.diastore.feature.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.diastore.HomeBinding
import com.mobile.diastore.R
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private lateinit var binding: HomeBinding
    private val entriesSharedViewModel by sharedViewModel<EntriesSharedViewModel>()
    private val homeViewModel by viewModel<HomeViewModel>()
    private val adapter: EntryAdapter = EntryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        DataBindingUtil.inflate<HomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).also {
            binding = it
            binding.lifecycleOwner = viewLifecycleOwner
        }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = homeViewModel
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.inflateMenu(R.menu.home_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sync -> homeViewModel.getEntriesFromServer()
            }
            true
        }

        binding.entriesRecycler.adapter = adapter.apply {
            setOnEntryClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToEntryDetailsFragment(
                        it
                    )
                )
            }
            setOnDeleteEntryClickListener {
                homeViewModel.deleteEntry(it.id)
            }
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToEntryDetailsFragment()
            )
        }

        entriesSharedViewModel.selectedEntry.observe(viewLifecycleOwner, Observer {
            it?.let {
                homeViewModel.handleSelectedEntryChange(it)
            }
        })

        homeViewModel.entries.observe(viewLifecycleOwner, Observer {
            adapter.setEntriesList(it.toMutableList())
        })

        homeViewModel.getError.observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.container, R.string.home_load_data_error, Snackbar.LENGTH_SHORT)
                .show()
        })

        homeViewModel.deleteError.observe(viewLifecycleOwner, Observer {
            Snackbar.make(
                binding.container,
                R.string.home_delete_entry_error,
                Snackbar.LENGTH_SHORT
            ).show()
        })

        homeViewModel.deletedEntry.observe(viewLifecycleOwner, Observer {
            homeViewModel.entries.value?.toMutableList()?.let { list ->
                adapter.setEntriesList(list)
            }
        })

        homeViewModel.updateError.observe(viewLifecycleOwner, Observer {
            Snackbar.make(
                binding.container,
                R.string.home_update_entry_error,
                Snackbar.LENGTH_SHORT
            ).show()
        })

        homeViewModel.addError.observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.container, R.string.home_add_entry_error, Snackbar.LENGTH_SHORT)
                .show()
        })
    }
}