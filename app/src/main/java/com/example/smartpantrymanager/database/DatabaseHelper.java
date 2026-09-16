package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.IngredientNormaliser;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ---- Database metadata ----
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // ---- Table: pantry_items ----
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // ---- Table: recipes ----
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // ---- Table: recipe_ingredients ----
    public static final String TABLE_RECIPE_ING = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "required_quantity";
    public static final String COL_RI_UNIT = "unit";

    // ---- CREATE statements ----
    private static final String CREATE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PANTRY_NAME + " TEXT NOT NULL, " +
                    COL_PANTRY_QTY + " REAL NOT NULL, " +
                    COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                    COL_PANTRY_EXPIRY + " TEXT" +
                    ");";

    private static final String CREATE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RECIPE_NAME + " TEXT NOT NULL, " +
                    COL_RECIPE_STEPS + " TEXT NOT NULL" +
                    ");";

    private static final String CREATE_RECIPE_ING =
            "CREATE TABLE " + TABLE_RECIPE_ING + " (" +
                    COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                    COL_RI_NAME + " TEXT NOT NULL, " +
                    COL_RI_QTY + " REAL NOT NULL, " +
                    COL_RI_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE" +
                    ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY);
        db.execSQL(CREATE_RECIPES);
        db.execSQL(CREATE_RECIPE_ING);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simplest strategy for an assignment: drop everything and recreate.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // =====================================================
    //  PANTRY CRUD
    // =====================================================

    /** Insert a new pantry item. Stores a normalised name for reliable matching. */
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, IngredientNormaliser.normalise(item.getName()));
        cv.put(COL_PANTRY_QTY, item.getQuantity());
        cv.put(COL_PANTRY_UNIT, item.getUnit());
        cv.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, cv);
        db.close();
        return id;
    }

    /** Read every pantry item, ordered alphabetically. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_PANTRY,
                null, null, null, null, null,
                COL_PANTRY_NAME + " ASC"
        );
        if (c.moveToFirst()) {
            do {
                list.add(cursorToPantryItem(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    /** Update an existing pantry item. */
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, IngredientNormaliser.normalise(item.getName()));
        cv.put(COL_PANTRY_QTY, item.getQuantity());
        cv.put(COL_PANTRY_UNIT, item.getUnit());
        cv.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        int rows = db.update(
                TABLE_PANTRY, cv,
                COL_PANTRY_ID + " = ?",
                new String[]{ String.valueOf(item.getId()) }
        );
        db.close();
        return rows;
    }

    /** Delete a pantry item by id. */
    public void deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_PANTRY,
                COL_PANTRY_ID + " = ?",
                new String[]{ String.valueOf(id) }
        );
        db.close();
    }

    private PantryItem cursorToPantryItem(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(COL_PANTRY_ID));
        String name = c.getString(c.getColumnIndexOrThrow(COL_PANTRY_NAME));
        double qty = c.getDouble(c.getColumnIndexOrThrow(COL_PANTRY_QTY));
        String unit = c.getString(c.getColumnIndexOrThrow(COL_PANTRY_UNIT));
        String expiry = c.getString(c.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
        return new PantryItem(id, name, qty, unit, expiry);
    }

    // =====================================================
    //  RECIPE READ
    // =====================================================

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " ASC");
        if (c.moveToFirst()) {
            do {
                list.add(new Recipe(
                        c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_RECIPE_STEPS))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null,
                COL_RECIPE_ID + " = ?",
                new String[]{ String.valueOf(recipeId) },
                null, null, null);
        Recipe r = null;
        if (c.moveToFirst()) {
            r = new Recipe(
                    c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_STEPS))
            );
        }
        c.close();
        db.close();
        return r;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPE_ING, null,
                COL_RI_RECIPE_ID + " = ?",
                new String[]{ String.valueOf(recipeId) },
                null, null, null);
        if (c.moveToFirst()) {
            do {
                list.add(new RecipeIngredient(
                        c.getInt(c.getColumnIndexOrThrow(COL_RI_ID)),
                        c.getInt(c.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_RI_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_RI_QTY)),
                        c.getString(c.getColumnIndexOrThrow(COL_RI_UNIT))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // =====================================================
    //  STRICT MATCHING
    // =====================================================

    /**
     * THE CORE LOGIC.
     * Returns recipes whose every required ingredient is present in the
     * pantry in at least the required quantity, in a compatible unit.
     */
    public List<Recipe> getStrictMatches() {
        // 1. Build pantry map: normalised name -> total base quantity + unit
        List<PantryItem> pantry = getAllPantryItems();
        java.util.Map<String, Double> qtyMap = new java.util.HashMap<>();
        java.util.Map<String, String> unitMap = new java.util.HashMap<>();

        for (PantryItem p : pantry) {
            String key = IngredientNormaliser.normalise(p.getName());
            double base = com.example.smartpantrymanager.utils.UnitConverter
                    .toBase(p.getQuantity(), p.getUnit());
            Double existing = qtyMap.get(key);
            qtyMap.put(key, existing == null ? base : existing + base);
            unitMap.put(key, p.getUnit()); // remember one representative unit
        }

        // 2. Check each recipe
        List<Recipe> matches = new ArrayList<>();
        for (Recipe r : getAllRecipes()) {
            List<RecipeIngredient> needed = getIngredientsForRecipe(r.getId());
            boolean canMake = true;

            for (RecipeIngredient ri : needed) {
                String key = IngredientNormaliser.normalise(ri.getIngredientName());
                Double haveBase = qtyMap.get(key);
                if (haveBase == null) { canMake = false; break; }

                String pantryUnit = unitMap.get(key);
                if (!com.example.smartpantrymanager.utils.UnitConverter
                        .sameCategory(pantryUnit, ri.getUnit())) {
                    canMake = false; break;
                }

                double needBase = com.example.smartpantrymanager.utils.UnitConverter
                        .toBase(ri.getRequiredQuantity(), ri.getUnit());
                if (haveBase < needBase) { canMake = false; break; }
            }

            if (canMake) matches.add(r);
        }
        return matches;
    }

    // =====================================================
    //  RECIPE SEEDING (runs once on first install)
    // =====================================================

    private void seedRecipes(SQLiteDatabase db) {
        // 18 recipes — every name normalised automatically on insert.
        addSeedRecipe(db, "Boiled Egg",
                "1. Boil water.\n2. Add egg.\n3. Cook 7 minutes.\n4. Cool and peel.",
                new Object[][]{
                        {"egg", 1.0, "unit"}
                });

        addSeedRecipe(db, "Scrambled Eggs",
                "1. Beat eggs.\n2. Melt butter in pan.\n3. Add eggs, stir until set.",
                new Object[][]{
                        {"egg", 2.0, "unit"},
                        {"butter", 10.0, "g"}
                });

        addSeedRecipe(db, "Tomato Pasta",
                "1. Boil pasta in salted water.\n2. Fry chopped tomatoes in oil.\n3. Mix and serve.",
                new Object[][]{
                        {"pasta", 200.0, "g"},
                        {"tomato", 3.0, "unit"},
                        {"oil", 15.0, "ml"}
                });

        addSeedRecipe(db, "Garlic Butter Pasta",
                "1. Boil pasta.\n2. Melt butter, add crushed garlic.\n3. Toss pasta in garlic butter.",
                new Object[][]{
                        {"pasta", 200.0, "g"},
                        {"butter", 30.0, "g"},
                        {"garlic", 2.0, "unit"}
                });

        addSeedRecipe(db, "Cheese Omelette",
                "1. Beat eggs with salt.\n2. Pour into hot pan.\n3. Add grated cheese, fold, serve.",
                new Object[][]{
                        {"egg", 2.0, "unit"},
                        {"cheese", 50.0, "g"},
                        {"butter", 10.0, "g"}
                });

        addSeedRecipe(db, "Tomato Sandwich",
                "1. Toast bread.\n2. Slice tomato.\n3. Butter bread, add tomato, close.",
                new Object[][]{
                        {"bread", 2.0, "unit"},
                        {"tomato", 1.0, "unit"},
                        {"butter", 10.0, "g"}
                });

        addSeedRecipe(db, "Cheese Sandwich",
                "1. Butter bread.\n2. Add cheese slice.\n3. Close and toast if desired.",
                new Object[][]{
                        {"bread", 2.0, "unit"},
                        {"cheese", 30.0, "g"},
                        {"butter", 10.0, "g"}
                });

        addSeedRecipe(db, "Onion Soup",
                "1. Fry sliced onions in butter.\n2. Add water, simmer 20 min.\n3. Season and serve.",
                new Object[][]{
                        {"onion", 3.0, "unit"},
                        {"butter", 20.0, "g"},
                        {"water", 500.0, "ml"}
                });

        addSeedRecipe(db, "Rice and Beans",
                "1. Boil rice.\n2. Heat beans.\n3. Combine and season.",
                new Object[][]{
                        {"rice", 150.0, "g"},
                        {"beans", 200.0, "g"},
                        {"water", 300.0, "ml"}
                });

        addSeedRecipe(db, "Simple Fried Rice",
                "1. Cook rice.\n2. Fry onion in oil.\n3. Add rice, soy sauce, stir.",
                new Object[][]{
                        {"rice", 150.0, "g"},
                        {"onion", 1.0, "unit"},
                        {"oil", 15.0, "ml"},
                        {"soy sauce", 15.0, "ml"}
                });

        addSeedRecipe(db, "Potato Soup",
                "1. Boil diced potatoes in water.\n2. Mash lightly.\n3. Add milk, simmer, season.",
                new Object[][]{
                        {"potato", 3.0, "unit"},
                        {"water", 500.0, "ml"},
                        {"milk", 200.0, "ml"}
                });

        addSeedRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes.\n2. Drain.\n3. Mash with butter and milk.",
                new Object[][]{
                        {"potato", 4.0, "unit"},
                        {"butter", 30.0, "g"},
                        {"milk", 100.0, "ml"}
                });

        addSeedRecipe(db, "Banana Smoothie",
                "1. Peel banana.\n2. Blend with milk.\n3. Serve chilled.",
                new Object[][]{
                        {"banana", 1.0, "unit"},
                        {"milk", 250.0, "ml"}
                });

        addSeedRecipe(db, "Fruit Salad",
                "1. Chop fruit.\n2. Toss together.\n3. Chill and serve.",
                new Object[][]{
                        {"banana", 1.0, "unit"},
                        {"apple", 1.0, "unit"},
                        {"orange", 1.0, "unit"}
                });

        addSeedRecipe(db, "Apple Oatmeal",
                "1. Cook oats in milk.\n2. Dice apple.\n3. Top oats with apple.",
                new Object[][]{
                        {"oat", 50.0, "g"},
                        {"milk", 200.0, "ml"},
                        {"apple", 1.0, "unit"}
                });

        addSeedRecipe(db, "Peanut Butter Toast",
                "1. Toast bread.\n2. Spread peanut butter.\n3. Serve.",
                new Object[][]{
                        {"bread", 1.0, "unit"},
                        {"peanut butter", 20.0, "g"}
                });

        addSeedRecipe(db, "Garlic Rice",
                "1. Cook rice.\n2. Fry crushed garlic in oil.\n3. Mix through rice.",
                new Object[][]{
                        {"rice", 150.0, "g"},
                        {"garlic", 2.0, "unit"},
                        {"oil", 15.0, "ml"}
                });

        addSeedRecipe(db, "Lemon Water",
                "1. Squeeze lemon into water.\n2. Stir.\n3. Serve cold.",
                new Object[][]{
                        {"lemon", 1.0, "unit"},
                        {"water", 250.0, "ml"}
                });
    }

    /** Helper used only during seeding. */
    private void addSeedRecipe(SQLiteDatabase db, String name, String steps,
                               Object[][] ingredients) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_NAME, name);
        cv.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (Object[] row : ingredients) {
            ContentValues ing = new ContentValues();
            ing.put(COL_RI_RECIPE_ID, recipeId);
            ing.put(COL_RI_NAME,
                    IngredientNormaliser.normalise((String) row[0]));
            ing.put(COL_RI_QTY, (Double) row[1]);
            ing.put(COL_RI_UNIT, (String) row[2]);
            db.insert(TABLE_RECIPE_ING, null, ing);
        }
    }
}