package com.csce482.arcdecisiontool.Controllers.Timeline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csce482.arcdecisiontool.Controllers.Input.InputActivity;
import com.csce482.arcdecisiontool.Controllers.Main.MainActivity;
import com.csce482.arcdecisiontool.Models.Hurricane;
import com.csce482.arcdecisiontool.Models.Task;
import com.csce482.arcdecisiontool.R;
import com.csce482.arcdecisiontool.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This is the timeline activity.
 */
public class TimelineActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private boolean showOnlyCompletedTasks = false;

    private Task [] dataSet;

    final int H120 = 0, H96 = 1, H7248 = 2, H24 = 3, LANDFALL = 4, POSTLANDFALL = 5;
    final String [] timelines = {
            "120", "96", "7248", "24", "0", "-1"
    };
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Hurricane hurricane;
    private LinearLayout linearLayout;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.timelineLinearLayout);

        realm = Realm.getDefaultInstance();

        if (Utils.hurricaneEventExists()) {
            hurricane = Utils.getHurricane();
            getSupportActionBar().setTitle(hurricane.getName());
        }
        setUpRecyclerView();
        setUpButtonCallbacks();
        switchTimeline(H120);

        MainActivity.refreshScheduledDates();
        Date landfall = hurricane.getLandfall();
        Date now = new Date();

        long timeInMS = landfall.getTime() - now.getTime();

        new CountDownTimer(timeInMS, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView countDownView = findViewById(R.id.countDownView);
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                countDownView.setText(hms);
                mAdapter.notifyDataSetChanged();
            }

            public void onFinish() {
                TextView countDownView = findViewById(R.id.countDownView);
                countDownView.setText("0:00");
            }
        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_input:
                Intent inputMenuIntent = new Intent(this, InputActivity.class);
                startActivity(inputMenuIntent);
                return true;
            case R.id.menu_reset_hurricane:
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("Pressing 'YES' will delete information about the hurricane and tasks.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Utils.resetEvent();
                                Intent inputMenuIntent = new Intent(TimelineActivity.this, InputActivity.class);
                                String key = "hello";
                                String value = "from the other side";
								inputMenuIntent.putExtra(key, value);
                                startActivity(inputMenuIntent);
                            }})
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                return true;
            case R.id.menu_view_completed:
                showOnlyCompletedTasks = !showOnlyCompletedTasks;
                if (showOnlyCompletedTasks && Utils.completedTasksCount() == 0) {
                    Toast.makeText(TimelineActivity.this, "You haven't completed any tasks.", Toast.LENGTH_SHORT).show();
                    showOnlyCompletedTasks = false;
                    return true;
                }
                switchTimeline(currentTimelineIndex);
                item.setTitle(showOnlyCompletedTasks ? "Show Incomplete Tasks" : "Show Completed Tasks");
                if (showOnlyCompletedTasks) getSupportActionBar().setTitle("Completed Tasks");
                else getSupportActionBar().setTitle(hurricane.getName());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.timelineRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TimelineAdapter(dataSet, new OnTimelineItemClickListener() {
            @Override
            public void onTimelineItemClick(int index) {
                onClick(index);
            }
        }, new OnTimelineItemLongClickListener() {
            @Override
            public boolean onTimelineItemLongClick(int index) {
                return onLongClick(index);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpButtonCallbacks() {
        for (int i = 0; i < linearLayout.getChildCount(); i++){
            final int buttonIndex = i;
            final Button timelineSwitcher = (Button) linearLayout.getChildAt(i);

            timelineSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchTimeline(buttonIndex);
                }
            });
        }
    }

    private int currentTimelineIndex = -1;

    /**
     * Based on the index of the row of buttons representing the timelines in the timeline view, sets the recycler view's dataset.
     * @param buttonIndex The index corresponding to the timeline button that was pressed.
     */
    public void switchTimeline(int buttonIndex) {
        setDataSet(buttonIndex);
        ((TimelineAdapter) mAdapter).mDataset = getDataSet();
        mAdapter.notifyDataSetChanged();

        if (buttonIndex != currentTimelineIndex) {
            int oldColor = currentTimelineIndex % 2 == 0 ? ContextCompat.getColor(this, R.color.colorWhite) : ContextCompat.getColor(this, R.color.colorPrimaryLight);
            if (currentTimelineIndex != -1) {

                linearLayout.getChildAt(currentTimelineIndex).setBackgroundColor(oldColor);
            }
            linearLayout.getChildAt(buttonIndex).setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            currentTimelineIndex = buttonIndex;

        }
        manageEmptyTimelineView();

    }

    /**
     * Set the dataset corresponding to the timeline timelines[timelineDataset].
     * @param timelineDataSet The index in "timelines" to choose which timeline to load data from.
     */
    private void setDataSet(int timelineDataSet) {
        dataSet = loadDataSet(timelineDataSet, showOnlyCompletedTasks);
        ((TimelineAdapter) mAdapter).mDataset = dataSet;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Load the data set as an array of Tasks for the recycler view.
     * @param timelineDataSet The index in "timelines" to choose which timeline to load data from.
     * @param completion Determine whether to load complete tasks or incomplete tasks from the timeline.
     * @return Returns the data set as an array of Tasks.
     */
    private Task [] loadDataSet(int timelineDataSet, boolean completion) {

        //If the corresponding field was not given a value in the input screen, the boolean will evaluate to false.
        boolean airportCloseTimesExists = Utils.getHurricane().getAirportsCloseTime().getTime() > 0,
                galeForceWindArrivalExists = Utils.getHurricane().getGaleForceWindArrive().getTime() > 0,
                leadTimeToOpenSheltersExists = Utils.getHurricane().getLeadTimeToOpenShelters().getTime() > 0,
                hunkerDownTimeExists = Utils.getHurricane().getHunkerDownTime().getTime() > 0,
                expectedReEntryExists = Utils.getHurricane().getExpectedReEntryTime().getTime() > 0,
                callReg1Exists = Utils.getHurricane().getConferenceCallTimesForRegion().first().getTime() > 0,
                callReg2Exists = Utils.getHurricane().getConferenceCallTimesForRegion().last().getTime() > 0,
                callNHQ1Exists = Utils.getHurricane().getConferenceCallTimesForNHQ().first().getTime() > 0,
                callNHQ2Exists = Utils.getHurricane().getConferenceCallTimesForNHQ().last().getTime() > 0;
        boolean [] inputFieldsExistArray = {airportCloseTimesExists, galeForceWindArrivalExists,
                leadTimeToOpenSheltersExists, hunkerDownTimeExists, expectedReEntryExists, callReg1Exists,
                callReg2Exists, callNHQ1Exists, callNHQ2Exists};

        RealmResults<Task> results = realm.where(Task.class).equalTo("timeline", timelines[timelineDataSet]).and().equalTo("isCompleted", completion).findAll();
        ArrayList<Task> set = new ArrayList<>();
        for (int i = 0; i < results.size(); i++){
            if (Utils.isHighPriority(results.get(i)) && !results.get(i).getName().equals(Utils.inputtedTasks[1])) //high priority tasks first.
                set.add(results.get(i));
        }
        for (int i = 0; i < results.size(); i++){
            if (!Utils.isHighPriority(results.get(i)) && !results.get(i).getName().equals(Utils.inputtedTasks[1])) {
                boolean shouldAdd = true;
                boolean shouldAddCalls = !Utils.timelineHasExpired(timelines[timelineDataSet]);
                boolean isACall = Utils.isConferenceCall(results.get(i));
                if (!shouldAddCalls && isACall) continue;
                //Do not add to the data set if they weren't scheduled in the input screen. (WORKS ON CALLS ONLY.)
                String nm = results.get(i).getName();
                for (int j = 0; j < Utils.inputtedTasks.length; j++) { //If results.get(i) is one of the inputted tasks, set index to its index in Utils.inputtedTasks.
                    if (nm.equals(Utils.inputtedTasks[j])) {
                        if (!inputFieldsExistArray[j]) {
                            shouldAdd = false; //then check if it was given a date in the input activity. if not, do not add!
                            break;
                        }
                    }
                }
                if (shouldAdd) set.add(results.get(i));
            }
        }
        return set.toArray(new Task[set.size()]);
    }

    /**
     * Get the current effective data set visible in the timeline view.
     * @return The data set as an array of Tasks.
     */
    private Task [] getDataSet() {
        Task [] rval = ((TimelineAdapter) mAdapter).mDataset;
        return rval;
    }

    /**
     * Code to perform an action when an individual task is clicked.
     * @param index The index in the timeline data set.
     */
    private void onClick(final int index) {
        new AlertDialog.Builder(this)
                .setTitle("Toggle Completion")
                .setMessage("Is this task complete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        toggleCompletion(index, true);
                    }})
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toggleCompletion(index, false);
                    }
                }).show();

    }

    /**
     * Code to perform an action when an individual task is long-clicked.
     * @param index The index in the timeline data set.
     * @return The programmer's choice: Returning false causes the regular onClick function to execute after finger release, returning true only performs this code throughout the action.
     */
    private boolean onLongClick(final int index) {
        //NOTE: return true prevents registration of a regular click after finger release.
        //return false causes the regular onClick to execute after finger release.
        toggleCompletion(index, true);
        return true;
    }

    /**
     * Sets the Task in the data set to complete or incomplete.
     * @param index The index of the task in the data set to operate on.
     * @param value Pass true to set the task as complete, false to set as incomplete.
     */
    private void toggleCompletion(int index, boolean value) {
        if (value){
            if (getDataSet()[index].isCompleted()) return; //Do nothing if completed already.
            realm.beginTransaction();
            Task task = getDataSet()[index];
            task.setCompleted(true);
            task.setCompletionDate(new Date());
            realm.commitTransaction();
            Toast.makeText(TimelineActivity.this, "Task has been marked as completed.", Toast.LENGTH_SHORT).show();
            setDataSet(currentTimelineIndex);
            manageEmptyTimelineView();
        }
        else {
            realm.beginTransaction();
            Task task = getDataSet()[index];
            task.setCompleted(false);
            realm.commitTransaction();
            Toast.makeText(TimelineActivity.this, "Task has been marked as incomplete.", Toast.LENGTH_SHORT).show();
            setDataSet(currentTimelineIndex);
            manageEmptyTimelineView();
        }
    }

    private void manageEmptyTimelineView() {
        TextView emptyTimelineTV = findViewById(R.id.emptyTimeline);
        if (loadDataSet(currentTimelineIndex, true).length == 0 && loadDataSet(currentTimelineIndex, false).length == 0) {
            emptyTimelineTV.setText("There are no tasks associated with this timeline.");
        }
        else {
            int count = loadDataSet(currentTimelineIndex, !showOnlyCompletedTasks).length;
            if (showOnlyCompletedTasks) {
                emptyTimelineTV.setText("There are " + count + " incomplete tasks. In the menu, select \"Show incomplete tasks\" to view them.");
            } else {
                emptyTimelineTV.setText("All " + count + " tasks have been completed. In the menu, select \"Show completed tasks\" to view them. ");
            }
        }
    }

}