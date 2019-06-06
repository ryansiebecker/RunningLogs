package com.example.runninglogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ViewSavedRuns extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private RecyclerView runsRecycler;
    private RunAdapter mAdapter;
    private ArrayList<Run> runs;
    private final String accessKey = "RunsArrayList";
    private SearchView localSearch;
    private Button sortButton;
    private int buttonStatus; //0=not sorted, 1=distance, 2=time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_runs);
        setTitle("Saved Runs");

        if (null == runs) {
            runs = new ArrayList<>();
        }


        runsRecycler = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        runsRecycler.setLayoutManager(layoutManager);
        localSearch = findViewById(R.id.searchBar);
        sortButton = findViewById(R.id.sortButton);

        localSearch.setOnQueryTextListener(this);
        sortButton.setOnClickListener(this);

        SharedPreferences sharedPref = ViewSavedRuns.this.getSharedPreferences(accessKey, Context.MODE_PRIVATE);
        String RunListJSON = sharedPref.getString(accessKey, "error");

        Gson gson = new Gson();

        ArrayList<Run> savedRuns = gson.fromJson(RunListJSON, new TypeToken<ArrayList<Run>>() {
        }.getType());
        runs = savedRuns;

        mAdapter = new RunAdapter(runs);
        runsRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Run> runsToBeDisplayed = (ArrayList<Run>) runs.clone();
        ArrayList<Run> newRuns = new ArrayList<>();
        for (int i = 0; i < runsToBeDisplayed.size(); i++) {
            if (runsToBeDisplayed.get(i).getTitle().toLowerCase().contains(newText.toLowerCase())) {
                newRuns.add(runsToBeDisplayed.get(i));
            }
        }
        mAdapter.setData(newRuns);
        mAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sortButton) {
            //code to sort shit
            ArrayList<Run> newRuns = (ArrayList<Run>) runs.clone();
            if (buttonStatus == 0) {
                Collections.sort(newRuns, Run.BY_DISTANCE);
                buttonStatus++;
                sortButton.setText("LENGTH");
            } else if (buttonStatus == 1) {
                Collections.sort(newRuns, Run.BY_TIME);
                sortButton.setText("TIME");
                buttonStatus = 0;
            }
            mAdapter.setData(newRuns);
            mAdapter.notifyDataSetChanged();


        }
    }


    private class RunViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Run mRun;
        private TextView mTitleTextView;
        private TextView mStatsTextView;

        public RunViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.textViewRunTitle);
            mStatsTextView = itemView.findViewById(R.id.textViewRunStats);
            itemView.setOnClickListener(this);
        }

        public void bindRun(Run run) {
            mRun = run;
            mTitleTextView.setText(mRun.getTitle());
            Date runDate = mRun.getDate();
            SimpleDateFormat Dateformatter = new SimpleDateFormat("MMMM d, yyyy");
            SimpleDateFormat timeformatter = new SimpleDateFormat("h:mm a");
            String dateoutput = Dateformatter.format(runDate);
            String timeoutput = timeformatter.format(runDate);
            mStatsTextView.setText(dateoutput + " | " + mRun.getDistance() + " miles | " + mRun.getRunLength());
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(ViewSavedRuns.this, RunDetails.class);
            //title
            i.putExtra("TITLE", mRun.getTitle());
            //
            Date temp = mRun.getDate();
            SimpleDateFormat Dateformatter = new SimpleDateFormat("MMMM d, yyyy");
            SimpleDateFormat timeformatter = new SimpleDateFormat("h:mm a");
            String dateoutput = Dateformatter.format(temp);
            String timeoutput = timeformatter.format(temp);
            //date
            i.putExtra("DATE", dateoutput);
            //time
            i.putExtra("TIME", timeoutput);
            //condition
            i.putExtra("CONDITION", mRun.getCondition());
            //humidity
            i.putExtra("HUMIDITY", mRun.getHumidity());
            //temp
            i.putExtra("TEMP", mRun.getTemp());
            //winds
            i.putExtra("WIND", mRun.getWind());
            //mileage
            i.putExtra("MILEAGE", mRun.getDistance());
            //pace
            i.putExtra("PACE", mRun.getPace());
            //time
            i.putExtra("RUNTIME", mRun.getRunLength().toString());
            //notes
            i.putExtra("NOTES", mRun.getNotes());
            startActivity(i);
        }
    }

    public class RunAdapter extends RecyclerView.Adapter<RunViewHolder> {
        private ArrayList<Run> runs;

        public RunAdapter(ArrayList<Run> runsInput) {
            runs = runsInput;
        }

        public RunViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(ViewSavedRuns.this);
            View view = layoutInflater.inflate(R.layout.run_list_item, viewGroup, false);
            RunViewHolder v = new RunViewHolder(view);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull RunViewHolder runViewHolder, int i) {
            Run tempRun = runs.get(i);
            runViewHolder.bindRun(tempRun);
        }

        public void setData(ArrayList<Run> newRuns) {
            runs = newRuns;
        }

        @Override
        public int getItemCount() {
            return runs.size();
        }
    }

}
