package com.example.pet.ui.settings;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.example.pet.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.root_preferences);
        //setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}