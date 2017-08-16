package org.schulcloud.mobile.ui.dashboard;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.homework.HomeworkActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.Pair;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends BaseActivity implements DashboardMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    DashboardPresenter mDashboardPresenter;
    @Inject
    EventsAdapter mEventsAdapter;

    @BindView(R.id.events)
    RecyclerView mRecyclerView;

    @BindView(R.id.openTasks)
    TextView openTasks;
    @BindView(R.id.dueTill)
    TextView dueTillDate;
    @BindView(R.id.cardViewHomework)
    CardView cardViewHomework;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_dashboard, null, false);
        mDrawer.addView(contentView, 0);
        ButterKnife.bind(this);


        mRecyclerView.setAdapter(mEventsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDashboardPresenter.attachView(this);
        mDashboardPresenter.checkSignedIn(this);

        mDashboardPresenter.showHomeworks();
        mDashboardPresenter.showEvents();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(CourseSyncService.getStartIntent(this));
            startService(HomeworkSyncService.getStartIntent(this));
            startService(EventSyncService.getStartIntent(this));
        }

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed), getResources().getColor(R.color.hpiOrange), getResources().getColor(R.color.hpiYellow));

        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(CourseSyncService.getStartIntent(this));
                    startService(HomeworkSyncService.getStartIntent(this));
                    startService(EventSyncService.getStartIntent(this));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mDashboardPresenter.showHomeworks();
                        mDashboardPresenter.showEvents();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

    }

    @Override
    protected void onDestroy() {
        mDashboardPresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void showOpenHomeworks(Pair<String, String> openHomeworks) {
        openTasks.setText(openHomeworks.getFirst());
        if (openHomeworks.getSecond().equals("10000-01-31T23:59"))
            dueTillDate.setText("...");
        else
            dueTillDate.setText(openHomeworks.getSecond());
        cardViewHomework.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeworkActivity.class);
            this.startActivity(intent);
            this.finish();
        });
    }

    @Override
    public void showEvents(List<Event> eventsForDay) {
        mEventsAdapter.setEvents(this, eventsForDay);
        mEventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCourse(String courseId) {
        DetailedCourseFragment frag = new DetailedCourseFragment();
        Bundle args = new Bundle();
        args.putString(DetailedCourseFragment.ARGUMENT_COURSE_ID, courseId);
        frag.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overlay_fragment_container, frag)
                .addToBackStack(null)
                .commit();
    }
}
