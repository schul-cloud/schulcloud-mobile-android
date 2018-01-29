package org.schulcloud.mobile.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.Pair;
import org.schulcloud.mobile.util.ViewUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends MainFragment implements DashboardMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    DashboardPresenter mDashboardPresenter;

    @Inject
    EventsAdapter mEventsAdapter;

    @BindView(R.id.events)
    RecyclerView recyclerView;
    @BindView(R.id.openTasks)
    TextView openTasks;
    @BindView(R.id.dueTill)
    TextView dueTillDate;
    @BindView(R.id.cardViewHomework)
    CardView cardViewHomework;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static DashboardFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    public static DashboardFragment newInstance(boolean triggerDataSyncOnCreate) {
        DashboardFragment dashboardFragment = new DashboardFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        dashboardFragment.setArguments(args);

        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true)) {
            startService(CourseSyncService.getStartIntent(getContext()));
            startService(HomeworkSyncService.getStartIntent(getContext()));
            startService(EventSyncService.getStartIntent(getContext()));
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.dashboard_title);

        recyclerView.setAdapter(mEventsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
                    startService(CourseSyncService.getStartIntent(getContext()));
                    startService(HomeworkSyncService.getStartIntent(getContext()));
                    startService(EventSyncService.getStartIntent(getContext()));

                    new Handler().postDelayed(() -> {
                        mDashboardPresenter.showHomework();
                        mDashboardPresenter.showEvents();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        mDashboardPresenter.attachView(this);
        mDashboardPresenter.showHomework();
        mDashboardPresenter.showEvents();

        return view;
    }
    @Override
    public void onPause() {
        mDashboardPresenter.detachView();
        super.onPause();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showOpenHomework(@NonNull Pair<String, String> openHomework) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat simpleDateFormatDeux = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        try {
            date = simpleDateFormat.parse(openHomework.getSecond());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        openTasks.setText(openHomework.getFirst());
        if (openHomework.getSecond().equals("10000-01-31T23:59"))
            dueTillDate.setText("...");
        else
            dueTillDate.setText(simpleDateFormatDeux.format(date));
        cardViewHomework.setOnClickListener(v -> addFragment(HomeworkFragment.newInstance()));
    }
    @Override
    public void showEvents(@NonNull List<Event> eventsForDay) {
        mEventsAdapter.setContext(getContext());
        mEventsAdapter.setEvents(eventsForDay);
    }

    @Override
    public void showCourseDetails(@NonNull String courseId) {
        addFragment(DetailedCourseFragment.newInstance(courseId));
    }
}
