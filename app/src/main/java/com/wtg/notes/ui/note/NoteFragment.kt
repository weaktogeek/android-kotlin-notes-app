package com.wtg.notes.ui.note

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wtg.notes.R
import com.wtg.notes.databinding.FragmentNoteBinding
import com.wtg.notes.ui.adapter.NoteAdapter
import com.wtg.notes.ui.base.BaseFragment
import com.wtg.notes.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding, SharedViewModel>() {

    override val viewModel: SharedViewModel by activityViewModels()

    private lateinit var notesAdapter: NoteAdapter
    private lateinit var rvNotes: RecyclerView


    //helper if you need any setup with ads activated
    private var isSwitchView = false

    // onBackPressed
    private val callback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Leave empty do disable back press or
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRV()
        checkSettings()
        observeNotes()
        eventClick()
        swipeToDeleteNote()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_note, menu)

                // item Grid
                val menuItem = menu.findItem(R.id.menu_view_mode)

                if (!isSwitchView) {
                    menuItem.setIcon(R.drawable.baseline_grid_view_24)
                    menuItem.title = getString(R.string.label_grid_mode)
                } else {
                    menuItem.setIcon(R.drawable.baseline_view_list_24)
                    menuItem.title = getString(R.string.label_list_mode)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menu_action_search -> {
                        val searchView: SearchView = menuItem.actionView as SearchView
                        searchView.inputType = InputType.TYPE_CLASS_TEXT
                        searchView.setOnQueryTextListener(object : OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                notesAdapter.filter.filter(newText)
                                return false
                            }
                        })
                        true
                    }

                    R.id.menu_note_settings -> {

                        findNavController().navigate(R.id.action_noteFragment_to_settingsFragment)
                        true
                    }

                    R.id.menu_view_mode -> {
                        isSwitchView = !isSwitchView
                        viewModel.setTypeView(isSwitchView)
                        switchView(isSwitchView)

                        if (!isSwitchView) {
                            menuItem.setIcon(R.drawable.baseline_grid_view_24)
                            menuItem.title = getString(R.string.label_grid_mode)
                        } else {
                            menuItem.setIcon(R.drawable.baseline_view_list_24)
                            menuItem.title = getString(R.string.label_list_mode)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNoteBinding = FragmentNoteBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private fun switchView(isSwitched: Boolean) {
        // set layout for grid or list
        notesAdapter.toggleViewType(isSwitchView)

        rvNotes.layoutManager =
            if (!isSwitched)
                LinearLayoutManager(applicationContext())
            else
                StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        notesAdapter.notifyDataSetChanged()
    }

    private fun setupRV() {
        notesAdapter = NoteAdapter()
        rvNotes = binding.rvNotes.apply {
            adapter = notesAdapter
            layoutManager =
                LinearLayoutManager(applicationContext())
        }
    }

    private fun swipeToDeleteNote() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // get item position & delete notes
                val position = viewHolder.adapterPosition
                val notes = notesAdapter.differ.currentList[position]
                viewModel.deleteNote(notes).also {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_deleted_msg),
                        Snackbar.LENGTH_LONG
                    )
                        .apply { // action if want undelete note
                            setAction(getString(R.string.undo)) {
                                viewModel.updateNote(notes)
                            }
                            show()
                        }
                }
            }
        }

        // attach swipe callback to rv
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvNotes)
        }
    }

    private fun eventClick() = with(binding) {
        btnAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_addFragment)
        }

        // onclick navigate to add notes
        notesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("note", it)
            }
            findNavController().navigate(
                R.id.action_noteFragment_to_detailFragment,
                bundle
            )
        }
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllNotesByCreatedDate().observe(viewLifecycleOwner) {
                notesAdapter.setData(it)
            }
        }
    }

    private fun checkSettings() = lifecycleScope.launch {
        isSwitchView = viewModel.readStore.getSwitchView.first()
        switchView(isSwitchView)
    }

    private fun exitDialog(withAds: Boolean = true) {
        val dialogBuilder = if (withAds) {
            MaterialAlertDialogBuilder(requireActivity()).apply {

                // if the dialog is cancelable
                setCancelable(false)

                setView(R.layout.alert_dialog_exit)

                // positive button text and action
                setPositiveButton(resources.getString(R.string.quit)) { _, _ ->
                    requireActivity().finish()
                }
                // negative button text and action
                setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            }
        } else {
            MaterialAlertDialogBuilder(requireActivity()).apply {
                setIcon(R.mipmap.ic_launcher)
                setTitle(R.string.app_name)
                setMessage(resources.getString(R.string.message))

                // positive button text and action
                setPositiveButton(resources.getString(R.string.quit)) { _, _ ->
                    requireActivity().finish()
                }
                // negative button text and action
                setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            }
        }

        dialogBuilder.show()
    }
}