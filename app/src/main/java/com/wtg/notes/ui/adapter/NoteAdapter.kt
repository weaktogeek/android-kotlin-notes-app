package com.wtg.notes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wtg.notes.databinding.ItemGridNoteBinding
import com.wtg.notes.databinding.ItemListNoteBinding
import com.wtg.notes.model.NoteModel
import java.util.*

class NoteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var isSwitchView = true

    private val differCallback = object : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.modified == newItem.modified
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var listFull: List<NoteModel> = mutableListOf()

    // set data  list
    fun setData(list: List<NoteModel>) {
        this.listFull = list
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LIST_ITEM ->
                ListViewHolder(
                    ItemListNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            GRID_ITEM ->
                GridViewHolder(
                    ItemGridNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            else -> throw IllegalArgumentException("Undefined view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = differ.currentList[position]

        when (holder.itemViewType) {
            LIST_ITEM -> {
                val listHolder = holder as ListViewHolder
                listHolder.bind(item)

                // on item click
                listHolder.itemView.setOnClickListener { onItemClickListener?.let { it(item) } }
            }

            GRID_ITEM -> {
                val gridHolder = holder as GridViewHolder
                gridHolder.bind(item)

                // on item click
                gridHolder.itemView.setOnClickListener { onItemClickListener?.let { it(item) } }
            }

            else -> throw IllegalArgumentException("Undefined view type")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSwitchView) {
            LIST_ITEM
        } else {
            GRID_ITEM
        }
    }

    fun toggleViewType(isSwitched: Boolean): Boolean {
        isSwitchView = !isSwitched
        return isSwitchView
    }

    fun switchViewLayout(isListView: Boolean ){

    }

    // Filter implement
    override fun getFilter(): Filter {
        return filterNoteModel
    }

    private val filterNoteModel = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchStr: String = constraint.toString().lowercase(Locale.getDefault())

            val templist = mutableListOf<NoteModel>()

            //check if is searching
            if (searchStr.isEmpty()) {
                templist.addAll(listFull)
            } else {
                for (item in listFull) {
                    if (item.title.lowercase(Locale.getDefault()).contains(searchStr) ||
                        item.content.lowercase(Locale.getDefault()).contains(searchStr)
                    ) {
                        templist.add(item)
                    }
                }
            }

            val filterresults = FilterResults()
            filterresults.values = templist
            return filterresults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val listCleared = ArrayList<NoteModel>()

            // get list result
            val result = results!!.values as List<*>

            // check every object
            result.forEach { obj ->
                if (obj is NoteModel) {
                    listCleared.add(obj)
                }
            }

            // Send list to recyclerview
            differ.submitList(listCleared)
        }
    }

    // on item click listener
    private var onItemClickListener: ((NoteModel) -> Unit)? = null
    fun setOnItemClickListener(listener: (NoteModel) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        private const val LIST_ITEM = 0
        private const val GRID_ITEM = 1
    }
}

