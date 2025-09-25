package com.wtg.notes.ui.adapter

import android.text.util.Linkify
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wtg.notes.databinding.ItemGridNoteBinding
import com.wtg.notes.model.NoteModel
import java.text.SimpleDateFormat
import java.util.*

class GridViewHolder(private val binding: ItemGridNoteBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteModel) {
        binding.apply {
            if (item.color != 0) {
                cardBgColor.setCardBackgroundColor(item.color)
            }

            if (item.title.isEmpty()) {
                tvTitleNote.visibility = View.GONE
            } else {
                tvTitleNote.text = item.title
            }
            if (item.content.isEmpty()) {
                tvContainerNote.visibility = View.GONE
            } else {
                tvContainerNote.text = item.content
                Linkify.addLinks(
                    tvContainerNote,
                    Linkify.WEB_URLS or Linkify.PHONE_NUMBERS or Linkify.EMAIL_ADDRESSES
                )
            }

            tvDateNote.text =
                SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(item.modified))
        }
    }
}