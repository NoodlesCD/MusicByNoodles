package com.csdurnan.music.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.fragment.app.Fragment
import com.csdurnan.music.R
import com.csdurnan.music.activities.MainActivity
import com.csdurnan.music.activities.dataStore
import com.csdurnan.music.dc.PreferencesKeys.LIGHT_MODE
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.prefs.Preferences



/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val lightModeSwitch = view.findViewById<SwitchMaterial>(R.id.s_light_mode)

        val lightMode = booleanPreferencesKey("light_mode")
        val preferencesStore: Flow<Boolean> =
            requireContext().dataStore.data.map { preferences ->
                preferences[lightMode] ?: false
            }

        runBlocking {
            lightModeSwitch.isChecked = preferencesStore.firstOrNull() == true
        }

        lightModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                activity?.setTheme(R.style.Theme_MusicNoodlesCD_Light)
            } else {
                activity?.setTheme(R.style.Theme_MusicNoodlesCD_Dark)
            }

            runBlocking {
                requireContext().dataStore.edit { preferences ->
                    preferences[LIGHT_MODE] = isChecked
                }
            }

            val intent = activity?.intent
            activity?.finish()
            startActivity(intent);
        }

        return view
    }
}