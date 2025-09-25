package com.wtg.notes.ui.settings

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wtg.notes.DATABASE_NAME
import com.wtg.notes.R
import com.wtg.notes.databinding.FragmentSettingsBinding
import com.wtg.notes.ui.base.BaseFragment
import com.wtg.notes.utils.DateUtil
import com.wtg.notes.utils.FileUtil
import com.wtg.notes.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SharedViewModel>() {
    override val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSettings()
        setUI()
        switchListener()
        clickListener()
    }

    // Only events from buttons
    private fun clickListener() = with(binding) {

    }

    // Only events from switch button
    private fun switchListener() = with(binding) {
        darkModeSwitch.setOnCheckedChangeListener { _, b ->
            AppCompatDelegate.setDefaultNightMode(if (b) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            viewModel.setDarkMode(b)
        }

        /*gridModeSwitch.setOnCheckedChangeListener { _, b -> // if you need
             viewModel.setTypeView(b)
         }*/
    }

    // Any data that you want load on UI before to show
    private fun setUI() = with(binding) {
        tvVersion.text = getVersion()
    }

    /*Check all settings for show UI*/
    private fun checkSettings() = lifecycleScope.launch {

        val checkDarkMode = viewModel.readStore.getDarkMode.first()

        with(binding) {
            darkModeSwitch.isChecked = checkDarkMode
        }
    }

    private fun getVersion(): String {
        return try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                applicationContext().packageManager
                    .getPackageInfo(
                        applicationContext().packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
            } else {
                applicationContext().packageManager
                    .getPackageInfo(applicationContext().packageName, 0)
            }
            "Version ${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "No version"
        }
    }



    //Delete all notes
    private fun dialogDeleteDB() {

        MaterialAlertDialogBuilder(requireActivity()).apply {
            setIcon(R.mipmap.ic_launcher)
            setTitle("Do you really want to do it?")
            setMessage("This operation will delete all your notes and cannot be recovered.")

            // positive button text and action
            setPositiveButton("Yes") { _, _ ->
                // fast fix, optimizer next update

                viewModel.deleteAllNotes()
                toast("Database cleared!")
            }
            // negative button text and action
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
}