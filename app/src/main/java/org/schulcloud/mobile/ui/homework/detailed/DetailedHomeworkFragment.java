package org.schulcloud.mobile.ui.homework.detailed;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Comment;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.base.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class DetailedHomeworkFragment extends BaseFragment implements DetailedHomeworkMvpView {
    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    public static final String ARGUMENT_HOMEWORK_ID = "homeworkId";

    private String homeworkId = null;

    @Inject
    DetailedHomeworkPresenter mDetailedHomeworkPresenter;

    @Inject
    CommentsAdapter mCommentsAdapter;

    @BindView(R.id.homeworkName)
    TextView homeworkName;
    @BindView(R.id.homeworkDescription)
    TextView homeworkDescription;
    @BindView(R.id.homeworkDue)
    TextView homeworkDueDate;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.gradeComment)
    TextView gradeComment;
    @BindView(R.id.nonPrivate)
    RelativeLayout nonPrivate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_detailed_homework, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        homeworkId = args.getString(ARGUMENT_HOMEWORK_ID);

        mRecyclerView.setAdapter(mCommentsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDetailedHomeworkPresenter.attachView(this);
        mDetailedHomeworkPresenter.loadHomework(homeworkId);

        return view;
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showHomework(Homework homework) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date untilDate = null;
        try {
            untilDate = dateFormat.parse(homework.dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (untilDate.before(new Date())) {
            homeworkDueDate.setPaintFlags(homeworkDueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (homework.courseId != null && homework.courseId.name != null)
            homeworkName.setText(getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            homeworkName.setText(homework.name);
        homeworkDescription.setText(Html.fromHtml(homework.description));

        if (homework.restricted == null || !homework.restricted)
            mDetailedHomeworkPresenter.loadComments(homeworkId);
        else
            nonPrivate.setVisibility(View.GONE);

        if (untilDate != null)
            homeworkDueDate.setText(dateFormatDeux.format(untilDate));

        String course = homework.courseId != null ? homework.courseId.name : "";
    }

    @Override
    public void showError() {

    }

    @Override
    public void showSubmission(Submission submission, String userId) {
        if (submission == null) {
            submission = new Submission();
            submission.comment = getString(R.string.homework_homework_notSubmitted);
            submission.comments = new RealmList<>();
            Comment comment = new Comment();
            comment.comment = getString(R.string.homework_homework_comments_none);
            submission.comments.add(comment);
        }

        if (submission.grade != null)
            grade.setText(Integer.toString(submission.grade));
        if (submission.gradeComment != null)
            gradeComment.setText(Html.fromHtml(submission.gradeComment));

        mCommentsAdapter.setSubmissions(submission.comments);
        mCommentsAdapter.setUserId(userId);
        mCommentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void goToSignIn() {
        // Necessary in fragment?
    }
}
