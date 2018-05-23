package com.csce482.arcdecisiontool.Controllers.Timeline;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csce482.arcdecisiontool.Models.Task;
import com.csce482.arcdecisiontool.R;
import com.csce482.arcdecisiontool.Utils.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Adapts a dataset into a RecyclerView.Adapter object for presentation in the timeline view.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    public Task [] mDataset;
    private OnTimelineItemClickListener mListener;
    private OnTimelineItemLongClickListener mLongClickListener;

    /**
     * Store information about the views in an individual timeline row.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTextView;
        private TextView detailView;
        private TextView completionDate;
        public int index;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.timeline_list_item_textview);
            detailView = (TextView) v.findViewById(R.id.timeline_list_item_detail);
            completionDate = (TextView) v.findViewById(R.id.timeline_list_item_completionDate);
        }
        public void bind(final Task task, final OnTimelineItemClickListener listener, final OnTimelineItemLongClickListener longClickListener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    listener.onTimelineItemClick(index);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override public boolean onLongClick(View v){
                    return longClickListener.onTimelineItemLongClick(index);
                }
            });
        }
    }

    /**
     * Constructor for TimelineAdapter.
     * @param myDataSet An array of tasks to show in the timeline view.
     * @param listener An on-click listener.
     * @param longClickListener A long-click listener.
     */
    public TimelineAdapter(Task [] myDataSet, OnTimelineItemClickListener listener, OnTimelineItemLongClickListener longClickListener){
        mListener = listener;
        mLongClickListener = longClickListener;
        mDataset = myDataSet;
    }

    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setUpRowViews(holder, position);
    }

    /**
     * Manage the way information is displayed in an individual timeline row.
     * @param holder The View Holder.
     * @param position The position in the timeline dataset array.
     */
    private void setUpRowViews(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (!Utils.hurricaneEventExists()) return;
        Task task = mDataset[position];

        holder.index = position;
        holder.mTextView.setText(task.getName());

        long timeTilTimelineExpires = Utils.millisUntilTimelineExpires(task.getTimeline());
        if (timeTilTimelineExpires < 0) timeTilTimelineExpires = 0;
        String hms = Utils.hmsFormat(timeTilTimelineExpires);

        if (task.isCompleted()) {
            String color = (position % 2 == 0) ? "#00e2b5" : "#00ffcc";
            holder.itemView.setBackgroundColor(Color.parseColor(color));
            holder.completionDate.setText("Completed on:\n" + Utils.formattedDate(task.getCompletionDate()));
        }
        else {
            String color = (position % 2 == 0) ? "#ddfffc" : "#ffffff";

            if (Utils.isHighPriority(task)) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF00D4FF"));

                holder.completionDate.setText("(high priority) incomplete, time left: " + hms);
            }
            else {
                if (Utils.timelineAboutToOrHasExpired(task.getTimeline())) {
                    color = (position % 2 == 0) ? "#FFA3A3" : "#ff7f7f";
                    holder.itemView.setBackgroundColor(Color.parseColor(color));
                    holder.detailView.setTextColor(Color.parseColor("#000000"));
                }
                else {
                    holder.itemView.setBackgroundColor(Color.parseColor(color));
                    holder.detailView.setTextColor(Color.parseColor("#FF8800"));
                }
                holder.completionDate.setText("incomplete, time left: " + hms);
            }


        }

        int index;
        if ((index = Utils.isInputtedTask(task)) != -1) {
            boolean isConferenceCall = index >= Utils.inputtedTasks.length - 4;
            String time = Utils.formattedDate(Utils.inputtedTasksDates[index]);
            holder.detailView.setText("Scheduled for: " + time); // TODO: set completion date at inputactivity
            holder.detailView.setVisibility(View.VISIBLE);
            if (!task.isCompleted()) {

                if (!isConferenceCall) { //simply get the difference between now and scheduled time

                    long timeTilScheduled = Utils.millisUntil(Utils.inputtedTasksDates[index].getTime());
                    if (timeTilScheduled < 0) timeTilScheduled = 0;
                    String timeTilHMS = Utils.hmsFormat(timeTilScheduled);
                    holder.completionDate.setText("incomplete, time left: " + timeTilHMS);
                } else { // This item is a Conference Call
                    /* PROBLEM: NEED TO SET holder.completionDate TEXT TO TIME UNTIL THIS CALL
                    1 Determine the day the call must occur for this timeline.
                    2 Create a date on that day with the time of the call, called d.
                    3 set the text to d - currentDate TODO: handle the fact that 72-48 is two days
                    * */

                    Date dayOfCall = new Date(Utils.millisAtTimelineStart(task.getTimeline())); //Day in which this timeline starts.
                    Calendar dayOfCallCalendar = Calendar.getInstance();
                    dayOfCallCalendar.setTime(dayOfCall); //Day in which this timeline starts, as a Calendar object. (correct)

                    Calendar getHourAndMin = Calendar.getInstance();
                    getHourAndMin.setTimeInMillis(Utils.inputtedTasksDates[index].getTime());
                    dayOfCallCalendar.set(Calendar.HOUR_OF_DAY, getHourAndMin.get(Calendar.HOUR_OF_DAY));
                    dayOfCallCalendar.set(Calendar.MINUTE, getHourAndMin.get(Calendar.MINUTE)); //correct
                    dayOfCall = dayOfCallCalendar.getTime();
                    long timeTil = Utils.millisUntil(dayOfCall.getTime());
                    if (timeTil < 0) timeTil = 0;
                    String timeTilHMS = Utils.hmsFormat(timeTil);
                    holder.completionDate.setText("incomplete, time left: " + timeTilHMS);
                    holder.detailView.setText("Scheduled for: " + Utils.formattedDate(dayOfCallCalendar.getTime()));
                }
            }
        } else holder.detailView.setVisibility(View.GONE);


        holder.bind(task, mListener, mLongClickListener); //this goes last to avoid bugs. I think on click listeners need the holder's fields to be initialized
    }

    /**
     * Count the items in the timeline dataset.
     * @return Returns the number of items in the timeline dataset.
     */
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
