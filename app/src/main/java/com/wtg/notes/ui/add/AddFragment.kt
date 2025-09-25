package com.wtg.notes.ui.add

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.wtg.notes.R
import com.wtg.notes.databinding.FragmentAddBinding
import com.wtg.notes.model.NoteModel
import com.wtg.notes.ui.base.BaseFragment
import com.wtg.notes.viewmodel.SharedViewModel
import com.wtg.colorpicker.ColorPicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddFragment : BaseFragment<FragmentAddBinding, SharedViewModel>() {
    override val viewModel: SharedViewModel by activityViewModels()

    private var lastDraftTitle: String = "" //Last title saved
    private var lastDraftContent: String = "" //Last content saved
    private var _id: Int = -1 // note id
    private var colorCard = 0
    private val created = System.currentTimeMillis() //Created date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSettings()
        setup()
        textWatcher()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_add, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menu_add_color -> {

                            selectColorDialog()

                        closeKeyBoard()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.tvAddTime.text =  SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(created))
    }

    private fun checkSettings() {

        lifecycleScope.launch {
        }
    }


    private fun setup() {
        colorCard = ContextCompat.getColor(requireContext(), R.color.default_card_color)
    }

    private fun textWatcher() = with(binding) {
        editAddTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != "") {
                    lastDraftTitle = p0.toString()
                    autoSaveDraft()
                }
            }
        })
        editAddContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != "") {
                    lastDraftContent = p0.toString()
                    autoSaveDraft()
                }
            }
        })
    }

    private fun autoSaveDraft() {
        if (lastDraftTitle.isNotEmpty() || lastDraftContent.isNotEmpty()) {
            val tmpNote = NoteModel(
                title = lastDraftTitle,
                content = lastDraftContent,
                color = colorCard,
                created = created
            )

            if (_id == -1) { // if first time then write
                viewModel.addNote(tmpNote).invokeOnCompletion {
                    viewModel.idLastRow.observeForever { //Get last id row  inserted
                        _id = it
                    }
                }
            } else { // already write, only update
                viewModel.updateNote(tmpNote.apply {
                    note_id = _id
                })
            }
        }
    }

    private fun selectColorDialog() = with(binding) {

        val colorPicker = ColorPicker(requireActivity()).apply {
            setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    colorCard = color
                    root.setBackgroundColor(color)
                    autoSaveDraft()
                }

                override fun onCancel() {}
            })
            setRoundColorButton(true)
            setColumns(5)
            setColors(R.array.free_colors)
        }

        colorPicker.show()
    }

    private fun closeKeyBoard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        if (lastDraftTitle.isEmpty() && lastDraftContent.isEmpty())
            toast(getString(R.string.not_saved))
        else
            toast(getString(R.string.add_note_msg))

        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddBinding = FragmentAddBinding.inflate(inflater, container, false)
}