package org.schulcloud.mobile.ui.homework;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.data.sync.SubmissionSyncService;
import org.schulcloud.mobile.ui.homework.add.AddHomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeworkFragment extends MainFragment implements HomeworkMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    public HomeworkPresenter mHomeworkPresenter;

    @Inject
    HomeworkAdapter mHomeworkAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.add_homework)
    FloatingActionButton fabAddHomework;

    @NonNull
    public static HomeworkFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    @NonNull
    public static HomeworkFragment newInstance(boolean triggerDataSyncOnCreate) {
        HomeworkFragment homeworkFragment = new HomeworkFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        homeworkFragment.setArguments(args);

        return homeworkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true)) {
            startService(HomeworkSyncService.getStartIntent(getContext()));
            startService(SubmissionSyncService.getStartIntent(getContext()));
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_title);

        recyclerView.setAdapter(mHomeworkAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(HomeworkSyncService.getStartIntent(getContext()));
                    startService(SubmissionSyncService.getStartIntent(getContext()));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mHomeworkPresenter.loadHomework();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        fabAddHomework.setOnClickListener(v -> addFragment(AddHomeworkFragment.newInstance()));

        mHomeworkPresenter.attachView(this);
        mHomeworkPresenter.loadHomework();

        return view;
    }
    @Override
    public void onPause() {
        mHomeworkPresenter.detachView();
        super.onPause();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showHomework(@NonNull List<Homework> homework) {
        mHomeworkAdapter.setHomework(homework);
        mHomeworkAdapter.setContext(getContext());
    }
    @Override
    public void showCanCreateHomework(boolean canCreateHomework) {
        ViewUtil.setVisibility(fabAddHomework, canCreateHomework);
    }
    @Override
    public void showHomeworkEmpty() {
        mHomeworkAdapter.setHomework(Collections.emptyList());
    }
    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.homework_loading_error)
                .show();
    }

    @Override
    public void showHomeworkDetail(@NonNull String homeworkId) {
        addFragment(DetailedHomeworkFragment.newInstance(homeworkId));
    }
}
