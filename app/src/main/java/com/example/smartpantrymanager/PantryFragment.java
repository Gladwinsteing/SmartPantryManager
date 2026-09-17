package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PantryFragment extends Fragment implements PantryAdapter.OnItemClickListener {

    private RecyclerView recycler;
    private TextView emptyView;
    private DatabaseHelper db;
    private PantryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pantry, container, false);

        recycler = root.findViewById(R.id.pantry_recycler);
        emptyView = root.findViewById(R.id.pantry_empty);
        FloatingActionButton fab = root.findViewById(R.id.fab_add_item);

        db = new DatabaseHelper(requireContext());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), AddEditItemActivity.class);
            // No id extra means "Add mode"
            startActivity(i);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.getAllPantryItems();
        if (adapter == null) {
            adapter = new PantryAdapter(items, this);
            recycler.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            // Simpler: rebuild the adapter to reflect the new list
            adapter = new PantryAdapter(items, this);
            recycler.setAdapter(adapter);
        }

        if (items.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent i = new Intent(requireContext(), AddEditItemActivity.class);
        i.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Remove " + item.getName() + " from pantry?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deletePantryItem(item.getId());
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}