package com.mobile.diastore.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.diastore.EntryBinding
import com.mobile.diastore.R
import com.mobile.diastore.model.Entry

typealias EntryClickListener = ((Entry) -> Unit)
typealias DeleteEntryClickListener = ((Entry) -> Unit)

class EntryAdapter :
    RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {
    private var entries = mutableListOf<Entry>()
    private var entryClickListener: EntryClickListener? = null
    private var deleteEntryClickListener: DeleteEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder =
        EntryViewHolder(parent = parent)

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    fun setOnEntryClickListener(entryClickListener: EntryClickListener) {
        this.entryClickListener = entryClickListener
    }

    fun setOnDeleteEntryClickListener(deleteEntryClickListener: DeleteEntryClickListener) {
        this.deleteEntryClickListener = deleteEntryClickListener
    }

    fun setEntriesList(entries: MutableList<Entry>) {
        val diffCallback = EntriesDiffCallback(this.entries, entries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.entries = entries
        diffResult.dispatchUpdatesTo(this)
    }

    inner class EntryViewHolder(
        private val parent: ViewGroup,
        private val binding: EntryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.view_entry,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: Entry) {
            binding.entry = entry
            binding.root.setOnClickListener {
                entryClickListener?.invoke(entry)
            }
            binding.buttonClose.setOnClickListener {
                deleteEntryClickListener?.invoke(entry)
            }
        }
    }

    private class EntriesDiffCallback(private val oldList: List<Entry>, private val newList: List<Entry>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] === newList[newItemPosition]

    }
}