package com.peacecorps.malaria.ui.user_profile.second_analysis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.peacecorps.malaria.R;

/**
 * Created by Anamika Tripathi on 11/8/18.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineHolder> {

    private SecondAnalysisPresenter<SecondAnalysisFragment> presenter;
    private Context context;

    TimelineAdapter(SecondAnalysisPresenter<SecondAnalysisFragment> presenter, Context context) {
        this.presenter = presenter;
        this.context = context;
    }

    @NonNull
    @Override
    public TimelineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = View.inflate(context, R.layout.item_analysis_timeline, null);
        return new TimelineHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineHolder holder, int position) {
        holder.description.setText(presenter.getDataList().get(position).getPercentage());
        holder.title.setText(presenter.getDataList().get(position).getMonth());
    }


    @Override
    public int getItemCount() {
        if (presenter.getDataList() == null)
            return 0;
        return presenter.getDataList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    class TimelineHolder extends RecyclerView.ViewHolder {

        private TimelineView timelineView;
        private TextView title, description;

        TimelineHolder(View itemView, int viewType) {
            super(itemView);
            timelineView = itemView.findViewById(R.id.time_marker);
            title = itemView.findViewById(R.id.text_timeline_title);
            description = itemView.findViewById(R.id.text_timeline_description);
            timelineView.initLine(viewType);
        }

    }
}
