package com.example.smartpantrymanager;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.PantryItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Show Pantry tab on first launch, but only if we're not restoring state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PantryFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                selected = new PantryFragment();
            } else if (id == R.id.nav_suggestions) {
                selected = new SuggestionsFragment();
            } else {
                selected = new SettingsFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selected)
                    .commit();
            return true;
        });
    }
}