package com.example.osos.mahmoudbakingapp.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.osos.mahmoudbakingapp.R;
import com.example.osos.mahmoudbakingapp.adapter.RecipeAdapter;
import com.example.osos.mahmoudbakingapp.model.Recipe;
import com.example.osos.mahmoudbakingapp.network.ApiClient;
import com.example.osos.mahmoudbakingapp.network.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.osos.mahmoudbakingapp.adapter.RecipeAdapter.RECIPE;

public class MainActivity extends AppCompatActivity {

    //                  inflate items
    @BindView(R.id.rv_recipe)
    RecyclerView recipeRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar loading;
    private ArrayList<Recipe> recipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//              to set orientation if portrait or landscape
        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recipeRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else {
            recipeRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

//              if it was cached
        if (savedInstanceState != null) {
            recipesList = savedInstanceState.getParcelableArrayList(RECIPE);
            setData();
        } else {
            loadRecipes();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE, recipesList);
    }

    private void setData() {
        loading.setVisibility(View.INVISIBLE);
        RecipeAdapter recipeAdapter = new RecipeAdapter(MainActivity.this, recipesList);
        recipeRecyclerView.setAdapter(recipeAdapter);
    }

    private void loadRecipes() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        final Type TYPE = new TypeToken<ArrayList<Recipe>>() {
        }.getType();
        Call<JsonArray> call = apiInterface.getRecipe();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                recipesList = new Gson().fromJson(response.body(), TYPE);
                setData();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            }
        });
    }


}
