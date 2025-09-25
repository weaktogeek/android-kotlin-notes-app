package com.wtg.notes.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.wtg.notes.R
import com.wtg.notes.databinding.FragmentDetailBinding
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
class DetailFragment :
    BaseFragment<FragmentDetailBinding, SharedViewModel>() {

    override val viewModel: SharedViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs() //safe args

    private var note: NoteModel? = null // note received from args

    // remove by crashed on mobiles with android 10
    //private var saveDraftHandler: Handler? = null
    private var lastDraftTitle: String = "" //Last title saved
    private var lastDraftContent: String = "" //Last content saved
    private var colorCard: Int = 0
    private var updated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // receiving bundles
        note = args.note

        checkSettings()
        initUI()
        autoSave()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menu_detail_color -> {

                        selectColorDialog()
                        closeKeyBoard()

                        true
                    }

                    R.id.menu_detail_send -> {

                        sendNote()
                        closeKeyBoard()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.tvEditTime.text =
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(note!!.modified))
    }

    private fun checkSettings() {

        lifecycleScope.launch {

        }
    }

    private fun initUI() = with(binding) {
        // saveDraftHandler = Handler(Looper.getMainLooper()) //autosave handler

        lastDraftTitle = note!!.title
        lastDraftContent = note!!.content

        editDetailTitle.setText(note!!.title)
        editDetailContent.setText(note!!.content)
        colorCard = note!!.color
        root.setBackgroundColor(colorCard)
    }

    private fun autoSave() = with(binding) {
        editDetailTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != "") {
                    // saveDraftHandler!!.removeCallbacksAndMessages(null)
                    // saveDraftHandler!!.postDelayed({
                    lastDraftTitle = p0.toString()
                    autosaveDraft()
                    //  }, 1000)
                }
            }
        })
        editDetailContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != "") {
                    //saveDraftHandler!!.removeCallbacksAndMessages(null)
                    // saveDraftHandler!!.postDelayed({
                    lastDraftContent = p0.toString()
                    autosaveDraft()
                    // }, 1000)
                }
            }
        })
    }

    //Fixed auto save on any devices.
    private fun autosaveDraft() {
        if (lastDraftTitle != note!!.title || lastDraftContent != note!!.content || colorCard != note!!.color) {
            val tmpNote = note

            viewModel.updateNote(tmpNote!!.apply {
                note_id = note!!.note_id
                title = lastDraftTitle
                content = lastDraftContent
                color = colorCard
            })
            updated = true
        }
    }

    private fun selectColorDialog() = with(binding) {
        val colorPicker = ColorPicker(requireActivity()).apply {
            setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    colorCard = color
                    root.setBackgroundColor(color)
                    autosaveDraft()
                }

                override fun onCancel() {}
            })
            setRoundColorButton(true)
            setColumns(5)
            setColors(R.array.free_colors)

        }

        colorPicker.show()
    }

    private fun sendNote() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            if (note!!.content.isEmpty()) {
                putExtra(Intent.EXTRA_TEXT, note!!.title)
            } else {
                putExtra(Intent.EXTRA_TEXT, note!!.content)
            }
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    //Close keyboard when fragment is destroyed
    private fun closeKeyBoard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        if (!updated)
            toast(getString(R.string.not_saved))
        else
            toast(getString(R.string.updated_note_msg))
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailBinding = FragmentDetailBinding.inflate(inflater, container, false)
}