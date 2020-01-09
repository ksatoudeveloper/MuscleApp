package io.github.ksatoudeveloper.muscleapp;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import io.github.ksatoudeveloper.muscleapp.data.AppDatabase;
import io.github.ksatoudeveloper.muscleapp.data.Exercise;
import io.github.ksatoudeveloper.muscleapp.data.ExerciseDao;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import java.lang.ref.WeakReference;


public class AddExerciseActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText weightEditText;
    Button addButton;
    private AsyncTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        setSupportActionBar(findViewById(R.id.toolbar));

        nameEditText = findViewById(R.id.name);
        weightEditText = findViewById(R.id.weight);
        addButton = findViewById(R.id.add);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                AddExerciseActivity.this.afterTextChanged();
            }
        };
        nameEditText.addTextChangedListener(afterTextChangedListener);
        weightEditText.addTextChangedListener(afterTextChangedListener);
        addButton.setOnClickListener(this::onClickAddButton);
    }

    protected void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    void afterTextChanged() {
        if (0 < nameEditText.getText().toString().length()
                && 0 < weightEditText.getText().toString().length()) {
            try {
                Integer.parseInt(weightEditText.getText().toString());
                addButton.setEnabled(true);
            } catch (NumberFormatException ignore) {
                addButton.setEnabled(false);
            }
        } else {
            addButton.setEnabled(false);
        }
    }

    void onClickAddButton(View view) {
        if (task == null) {
            Exercise exercise = new Exercise();
            exercise.name = nameEditText.getText().toString();
            exercise.weight = Integer.parseInt(weightEditText.getText().toString());
            task = new RegisterTask(this).execute(exercise);
        }
    }

    private static class RegisterTask extends AsyncTask<Exercise, Void, Void> {
        private WeakReference<AddExerciseActivity> activity;
        private ExerciseDao exerciseDao;

        RegisterTask(AddExerciseActivity activity) {
            this.activity = new WeakReference<>(activity);
            this.exerciseDao = AppDatabase.getInstance(activity.getApplicationContext()).getExerciseDao();
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exerciseDao.insert(exercises[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            AddExerciseActivity activity = this.activity.get();
            if (activity != null) {
                activity.task = null;
                activity.finish();
            }
        }
    }
}
