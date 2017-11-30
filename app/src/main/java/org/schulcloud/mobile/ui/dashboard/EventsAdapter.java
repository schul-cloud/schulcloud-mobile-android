package org.schulcloud.mobile.ui.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    @Inject
    DashboardPresenter mDashboardPresenter;

    private List<Event> mEvent;
    private Context mContext;

    @Inject
    public EventsAdapter() { mEvent = new ArrayList<>(); }

    public void setContext(@NonNull Context context) {
        mContext = context;
    }
    public void setEvents(@NonNull List<Event> events) {
        if (events.isEmpty()) {
            mEvent = new ArrayList<>(1);
            Event e = new Event();
            e.title = mContext.getString(R.string.dashboard_hours_none);
            e.summary = mContext.getString(R.string.dashboard_hours_none);
            e.start = "1514674800000";
            e.end = "1514678400000";
            e.type = Event.TYPE_TEMPLATE;
            mEvent.add(e);
        } else
            mEvent = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    private int determineProgress(long start, long end) {
        Calendar c = Calendar.getInstance();
        long now = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
        c.setTimeInMillis(start);
        long t1 = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
        c.setTimeInMillis(end);
        long t2 = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);

        return now > t2 ? -1 : (int) (100 * (now - t1) / (t2 - t1));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvent.get(position);

        if (!event.title.equals(event.summary))
            holder.summary.setText(event.summary);

        long start = Long.parseLong(event.start);
        long end = Long.parseLong(event.end);
        holder.title.setText(event.title);
        if (event.type != null && !event.type.equals(Event.TYPE_TEMPLATE))
            holder.time.setText(mContext.getString(R.string.dashboard_event_time,
                    new Date(start), new Date(Long.parseLong(event.end))));

        if (event.xScCourseId != null)
            holder.cardView.setOnClickListener(
                    v -> mDashboardPresenter.showCourseDetails(event.xScCourseId));

        int progress = determineProgress(start, end);
        Log.d("Times", String.valueOf(progress));
        if (progress >= 0) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(progress);
        }
    }

    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.summary)
        TextView summary;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
