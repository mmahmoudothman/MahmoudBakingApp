package com.example.osos.mahmoudbakingapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.osos.mahmoudbakingapp.R;
import com.example.osos.mahmoudbakingapp.fragment.IngredientStepDetailFragment;
import com.example.osos.mahmoudbakingapp.model.Step;

import java.util.ArrayList;

import static com.example.osos.mahmoudbakingapp.adapter.StepAdapter.STEPS;

public class DetailActivity extends AppCompatActivity {

    ArrayList<Step> steps;
    int position;
    boolean isTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        if (savedInstanceState == null) {
//                      retive data which parsed
            steps = getIntent().getParcelableArrayListExtra(STEPS);
            position = getIntent().getIntExtra(StepActivity.POSITION, 0);
            isTablet = getIntent().getBooleanExtra(StepActivity.PANES, false);

            IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(StepActivity.POSITION, position);
            bundle.putBoolean(StepActivity.PANES, isTablet);
            bundle.putParcelableArrayList(STEPS, steps);
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_container, fragment)
                    .commit();

        }
    }

}
