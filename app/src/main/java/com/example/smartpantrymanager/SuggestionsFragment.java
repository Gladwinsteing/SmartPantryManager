package com.example.smartpantrymanager;

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
import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

public class SuggestionsFragment extends Fragment {

    private RecyclerView recycler;
    private TextView emptyView;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_suggestions, container, false);
        recycler = root.findViewById(R.id.suggestions_recycler);
        emptyView = root.findViewById(R.id.suggestions_empty);
        db = new DatabaseHelper(requireContext());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMatches();
    }

    private void loadMatches() {
        List<Recipe> matches = db.getStrictMatches();
        RecipeAdapter adapter = new RecipeAdapter(matches, r -> {
            Intent i = new Intent(requireContext(), RecipeDetailActivity.class);
            i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, r.getId());
            startActivity(i);
        });
        recycler.setAdapter(adapter);

        if (matches.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }
}