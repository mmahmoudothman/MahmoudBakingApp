package com.example.osos.mahmoudbakingapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.osos.mahmoudbakingapp.R;
import com.example.osos.mahmoudbakingapp.adapter.IngredientAdapter;
import com.example.osos.mahmoudbakingapp.adapter.StepAdapter;
import com.example.osos.mahmoudbakingapp.model.Ingredient;
import com.example.osos.mahmoudbakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.osos.mahmoudbakingapp.adapter.RecipeAdapter.RECIPE;

public class MasterListFragment extends Fragment {


    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;
    private StepAdapter mStepAdapter;
    private Recipe recipe;

    @BindView(R.id.rv_ingredients_steps)
    RecyclerView mIngredientStepRecyclerView;
    @BindView(R.id.rv_ingredients)
    RecyclerView mIngredients;

    private StepAdapter.OnStepListener mClickListener;

    //          constructor
    public MasterListFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(RECIPE);
        }
        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);
        recipe = getActivity().getIntent().getParcelableExtra(RECIPE);
        ingredients = recipe.getAlls();

        mStepAdapter = new StepAdapter(getContext(), recipe, mClickListener);
        mIngredientStepRecyclerView.setAdapter(mStepAdapter);

        ingredientAdapter = new IngredientAdapter(ingredients);
        mIngredients.setAdapter(ingredientAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECIPE, recipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClickListener = (StepAdapter.OnStepListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

}
