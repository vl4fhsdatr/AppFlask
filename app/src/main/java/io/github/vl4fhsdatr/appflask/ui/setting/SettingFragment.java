package io.github.vl4fhsdatr.appflask.ui.setting;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.github.vl4fhsdatr.appflask.R;


public class SettingFragment extends PreferenceFragmentCompat {

    @SuppressWarnings("unused")
    private static final String TAG = "SettingFragment";

    public SettingFragment() {
    }

    @SuppressWarnings("unused")
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

}
