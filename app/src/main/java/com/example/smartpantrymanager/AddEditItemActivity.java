package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private EditText inputName, inputQuantity, inputExpiry;
    private Spinner inputUnit;
    private TextView formTitle;
    private DatabaseHelper db;
    private int editingId = -1;

    private static final String[] UNITS = {"unit", "g", "kg", "ml", "l"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);
        inputExpiry = findViewById(R.id.input_expiry);
        inputUnit = findViewById(R.id.input_unit);
        formTitle = findViewById(R.id.form_title);
        Button save = findViewById(R.id.btn_save);

        db = new DatabaseHelper(this);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputUnit.setAdapter(unitAdapter);

        // Did we get an id? If so, we're in edit mode.
        editingId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (editingId != -1) {
            formTitle.setText("Edit Ingredient");
            prefillFromDb();
        }

        save.setOnClickListener(v -> saveItem());
    }

    private void prefillFromDb() {
        for (PantryItem p : db.getAllPantryItems()) {
            if (p.getId() == editingId) {
                inputName.setText(p.getName());
                inputQuantity.setText(String.valueOf(p.getQuantity()));
                // select matching unit if present
                for (int i = 0; i < UNITS.length; i++) {
                    if (UNITS[i].equalsIgnoreCase(p.getUnit())) {
                        inputUnit.setSelection(i);
                        break;
                    }
                }
                if (p.getExpiryDate() != null) inputExpiry.setText(p.getExpiryDate());
                break;
            }
        }
    }

    private void saveItem() {
        String name = inputName.getText().toString().trim();
        String qtyStr = inputQuantity.getText().toString().trim();
        String expiry = inputExpiry.getText().toString().trim();
        String unit = inputUnit.getSelectedItem().toString();

        // ---- Input validation ----
        if (name.isEmpty()) {
            inputName.setError("Please enter a name");
            inputName.requestFocus();
            return;
        }
        if (qtyStr.isEmpty()) {
            inputQuantity.setError("Please enter a quantity");
            inputQuantity.requestFocus();
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            inputQuantity.setError("Quantity must be a number");
            inputQuantity.requestFocus();
            return;
        }
        if (qty <= 0) {
            inputQuantity.setError("Quantity must be greater than 0");
            inputQuantity.requestFocus();
            return;
        }
        // Simple expiry format check
        if (!expiry.isEmpty() && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            inputExpiry.setError("Use format yyyy-MM-dd");
            inputExpiry.requestFocus();
            return;
        }

        if (editingId == -1) {
            PantryItem item = new PantryItem(name, qty, unit,
                    expiry.isEmpty() ? null : expiry);
            db.addPantryItem(item);
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        } else {
            PantryItem item = new PantryItem(editingId, name, qty, unit,
                    expiry.isEmpty() ? null : expiry);
            db.updatePantryItem(item);
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}