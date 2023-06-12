package me.ako.yts.presentation.view

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ako.yts.R
import me.ako.yts.domain.controller.DataRepository
import me.ako.yts.domain.util.Utils
import me.ako.yts.domain.viewmodel.AppViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceChangeListener {
    private val viewModel: AppViewModel by activityViewModels()
    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var repository: DataRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<ListPreference>("theme")?.onPreferenceChangeListener = this
        findPreference<Preference>("clear")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear data")
                .setMessage("Are you sure you want to delete movie data?")
                .setPositiveButton("Delete") { v, which ->
                    lifecycleScope.launch {
                        repository.deleteAll()
                    }

                    viewModel.clearMovies()
                    utils.deleteCache()

                    v.dismiss()
                }
                .show()

            true
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        return when (preference.key) {
            "theme" -> {
                utils.setTheme(newValue as String)
                true
            }

            else -> false
        }
    }
}