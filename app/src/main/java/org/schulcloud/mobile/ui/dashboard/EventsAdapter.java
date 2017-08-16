package org.schulcloud.mobile.ui.dashboard;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> mEvent;

    @Inject
    DashboardPresenter mDashboardPresenter;

    @Inject
    public EventsAdapter() { mEvent = new ArrayList<>(); }

    public void setEvents(Context context, List<Event> events) {
        if (events == null || events.isEmpty()) {
            mEvent = new ArrayList<>(1);
            Event e = new Event();
            e.title = context.getString(R.string.dashboard_hours_none);
            e.summary = context.getString(R.string.dashboard_hours_none);
            e.start = "1514674800000";
            e.end = "1514678400000";
            e.type = Event.TYPE_TEMPLATE;
            mEvent.add(e);
        }
        else
            mEvent = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    private String millisToDate(long millis){
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(date);

        //return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMAN).format(millis);
    }

    private int determineProgress(String start, String end) {
        String startTime[] = start.split(":");
        String endTime[] = end.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String currentTime[] = millisToDate(calendar.getTimeInMillis()).split(":");

        float startT  = Integer.parseInt(startTime[0]) * 60 + Integer.parseInt(startTime[1]);
        float endT  = Integer.parseInt(endTime[0]) * 60 + Integer.parseInt(endTime[1]);
        float currentT = Integer.parseInt(currentTime[0]) * 60 + Integer.parseInt(currentTime[1]);

        if (currentT > endT) {
            return -1;
        } else {
            try {
                return Math.round(100 - 100 / ((endT - startT) / (endT - currentT)));
            } catch (ArithmeticException e) {
                return -1;
            }
        }
        }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvent.get(position);

        if (!event.title.equals(event.summary))
            holder.summary.setText(event.summary);

        holder.title.setText(event.title);
        if (event.type != null && !event.type.equals(Event.TYPE_TEMPLATE))
            holder.startDate.setText(millisToDate(Long.parseLong(event.start)) + "/" + millisToDate(Long.parseLong(event.end)));

        if (event.xScCourseId != null) {
            String courseId = event.xScCourseId;
            holder.cardView.setOnClickListener(v -> {
                mDashboardPresenter.showCourse(courseId);
            });
        }

        int progress = determineProgress(millisToDate(Long.parseLong(event.start)), millisToDate(Long.parseLong(event.end)));

        Log.d("Times", String.valueOf(progress));

        if (progress != -1 && progress >= 0) {
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
        @BindView(R.id.startDate)
        TextView startDate;
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
