package io.github.ksatoudeveloper.muscleapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.github.ksatoudeveloper.muscleapp.data.AppDatabase;
import io.github.ksatoudeveloper.muscleapp.data.Exercise;
import io.github.ksatoudeveloper.muscleapp.data.ExerciseDao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

/**
 * メイン画面
 */
public class MainActivity extends AppCompatActivity {

    private ListView list;
    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        list = findViewById(R.id.list);
        findViewById(R.id.fab).setOnClickListener(this::onClickAddButton);
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        task = new LoadTask(this).execute();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    void onClickAddButton(View view) {
        startActivity(new Intent(getApplicationContext(), AddExerciseActivity.class));
    }

    void onClickDeleteButton(Exercise exercise) {
        if (task == null) {
            task = new DeleteTask(this).execute(exercise);
        }
    }

    void Set(List<Exercise> exerciseList) {
        list.setAdapter(new Adapter(this, exerciseList));
    }

    private class Adapter extends BaseAdapter {
        private final MainActivity activity;
        private final LayoutInflater layoutInflater;
        private final List<Exercise> exerciseList;

        private Adapter(@NonNull MainActivity activity, @NonNull List<Exercise> exerciseList) {
            this.activity = activity;
            this.layoutInflater = LayoutInflater.from(activity);
            this.exerciseList = exerciseList;
        }

        @Override
        public int getCount() {
            return exerciseList.size();
        }

        @Override
        public Exercise getItem(int position) {
            return exerciseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            }
            Exercise exercise = getItem(position);

            TextView nameView = convertView.findViewById(R.id.name);
            TextView weightView = convertView.findViewById(R.id.weight);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            nameView.setText(exercise.name);
            weightView.setText(String.format(Locale.ENGLISH, "%d", exercise.weight));
            deleteButton.setOnClickListener(view -> {
                activity.onClickDeleteButton(exercise);
            });

            return convertView;
        }
    }

    static class LoadTask extends AsyncTask<Void, Void, List<Exercise>> {
        private WeakReference<MainActivity> activity;
        private ExerciseDao exerciseDao;

        private LoadTask(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
            this.exerciseDao = AppDatabase.getInstance(activity.getApplicationContext()).getExerciseDao();
        }

        @Override
        protected List<Exercise> doInBackground(Void... voids) {
            return exerciseDao.queryAll();
        }

        @Override
        protected void onPostExecute(List<Exercise> exerciseList) {
            MainActivity activity = this.activity.get();
            if (activity != null) {
                activity.Set(exerciseList);
                activity.task = null;
            }
        }
    }

    static class DeleteTask extends AsyncTask<Exercise, Void, Void> {
        private WeakReference<MainActivity> activity;
        private ExerciseDao exerciseDao;

        private DeleteTask(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
            this.exerciseDao = AppDatabase.getInstance(activity.getApplicationContext()).getExerciseDao();
        }

        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.delete(exercise[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MainActivity activity = this.activity.get();
            if (activity != null) {
                activity.task = null;
                activity.task = new LoadTask(activity).execute();
            }
        }
    }
}
