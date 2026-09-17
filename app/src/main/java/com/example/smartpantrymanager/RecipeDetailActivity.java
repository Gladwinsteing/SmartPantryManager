package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int id = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (id == -1) { finish(); return; }

        DatabaseHelper db = new DatabaseHelper(this);
        Recipe recipe = db.getRecipeById(id);
        if (recipe == null) { finish(); return; }

        ((TextView) findViewById(R.id.detail_name)).setText(recipe.getName());
        ((TextView) findViewById(R.id.detail_steps)).setText(recipe.getSteps());

        List<RecipeIngredient> ings = db.getIngredientsForRecipe(id);
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ri : ings) {
            sb.append("• ")
                    .append(ri.getIngredientName())
                    .append(" — ")
                    .append(ri.getRequiredQuantity())
                    .append(" ")
                    .append(ri.getUnit())
                    .append("\n");
        }
        ((TextView) findViewById(R.id.detail_ingredients)).setText(sb.toString());
    }
}