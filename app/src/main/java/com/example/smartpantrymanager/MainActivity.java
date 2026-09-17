package com.example.smartpantrymanager;

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
        // DO NOT call setContentView yet — this is a temporary test harness.

        DatabaseHelper db = new DatabaseHelper(this);

        // 1. Confirm recipes seeded
        List<Recipe> recipes = db.getAllRecipes();
        android.util.Log.d("DB_TEST", "Recipes seeded: " + recipes.size());
        for (Recipe r : recipes) {
            android.util.Log.d("DB_TEST", "  - " + r.getName()
                    + " (" + db.getIngredientsForRecipe(r.getId()).size() + " ingredients)");
        }

        // 2. Add a pantry item
        db.addPantryItem(new PantryItem("Egg", 2, "unit", null));
        db.addPantryItem(new PantryItem("Butter", 100, "g", null));

        // 3. Confirm it saved
        List<PantryItem> pantry = db.getAllPantryItems();
        android.util.Log.d("DB_TEST", "Pantry size: " + pantry.size());
        for (PantryItem p : pantry) android.util.Log.d("DB_TEST", "  - " + p);

        // 4. Strict matches
        List<Recipe> matches = db.getStrictMatches();
        android.util.Log.d("DB_TEST", "Strict matches: " + matches.size());
        for (Recipe r : matches) android.util.Log.d("DB_TEST", "  ✓ " + r.getName());

        // 5. Delete the egg and re-check
        int eggId = pantry.get(0).getId();
        db.deletePantryItem(eggId);
        List<Recipe> after = db.getStrictMatches();
        android.util.Log.d("DB_TEST", "After deleting egg, matches: " + after.size());
        for (Recipe r : after) android.util.Log.d("DB_TEST", "  ✓ " + r.getName());
    }

}