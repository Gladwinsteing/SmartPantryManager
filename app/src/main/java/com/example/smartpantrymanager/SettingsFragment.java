package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private static final String PREFS = "smart_pantry_prefs";
    private static final String KEY_ALERTS = "expiry_alerts";
    private static final String KEY_UNITS = "preferred_units";

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        Switch alerts = root.findViewById(R.id.switch_expiry_alerts);
        Spinner units = root.findViewById(R.id.spinner_units);

        alerts.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        alerts.setOnCheckedChangeListener((v, checked) ->
                prefs.edit().putBoolean(KEY_ALERTS, checked).apply());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Metric (g, ml)", "Imperial (oz, cups)"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        units.setAdapter(adapter);
        units.setSelection(prefs.getInt(KEY_UNITS, 0));
        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                prefs.edit().putInt(KEY_UNITS, pos).apply();
            }
            @Override public void onNothingSelected(AdapterView<?> p) { }
        });

        return root;
    }
}