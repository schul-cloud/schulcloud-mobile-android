package org.schulcloud.mobile.ui.homework.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.homework.HomeworkActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddHomeworkFragment extends BaseFragment implements AddHomeworkMvpView {
    @Inject
    AddHomeworkPresenter mAddHomeworkPresenter;

    @BindView(R.id.name)
    TextInputEditText mName;
    @BindView(R.id.course)
    AppCompatSpinner mCourse;
    @BindView(R.id.isPrivate)
    CheckBox mIsPrivate;
    @BindView(R.id.description)
    TextInputEditText mDescription;

    private DateFormat mDateFormat;
    @BindView(R.id.availableDate)
    TextView mAvailableDate;
    private Calendar mAvailableDateCalendar;
    @BindView(R.id.dueDate)
    TextView mDueDate;
    private Calendar mDueDateCalendar;

    @BindView(R.id.publicSubmissions)
    CheckBox mPublicSubmissions;
    @BindView(R.id.add)
    BootstrapButton mAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_add_homework, container, false);
        ButterKnife.bind(this, view);

        mAddHomeworkPresenter.attachView(this);
        mAddHomeworkPresenter.loadData();

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mAvailableDateCalendar = Calendar.getInstance();
        initDateButton(mAvailableDateCalendar, mAvailableDate);
        mDueDateCalendar = Calendar.getInstance();
        mDueDateCalendar.add(Calendar.DATE, 7);
        initDateButton(mDueDateCalendar, mDueDate);

        mAdd.setOnClickListener(v ->
                mAddHomeworkPresenter.addHomework(
                        mName.getText().toString().trim(),
                        mCourse.getSelectedItemPosition(),
                        mIsPrivate.isChecked(),
                        mDescription.getText().toString().trim(),
                        mAvailableDateCalendar,
                        mDueDateCalendar,
                        mPublicSubmissions.isChecked()));

        return view;
    }
    @Override
    public void onDetach() {
        mAddHomeworkPresenter.detachView();
        super.onDetach();
    }
    private void initDateButton(Calendar calendar, TextView button) {
        button.setText(mDateFormat.format(calendar.getTime()));
        button.setOnClickListener((v) ->
        {
            try {
                calendar.setTime(mDateFormat.parse(button.getText().toString()));
            }
            catch (ParseException e) {
                Timber.e(e, "There was an error loading the courses.");
            }
            new DatePickerDialog(getActivity(),
                    (dialog, year, month, dayOfMonth) ->
                    {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        new TimePickerDialog(getActivity(),
                                (view, hourOfDay, minute) ->
                                {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    button.setText(mDateFormat.format(calendar.getTime()));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), true)
                                .show();
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    @Override
    public void setCourses(List<String> courses) {
        for (int i = 0; i < courses.size(); i++)
            if (courses.get(i) == null)
                courses.set(i, getString(R.string.homework_homework_course_none));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourse.setAdapter(adapter);
        mCourse.setEnabled(true);
    }
    @Override
    public void setCanCreatePublic(boolean canCreatePublic) {
        if (canCreatePublic) {
            mIsPrivate.setEnabled(true);
            mPublicSubmissions.setVisibility(View.VISIBLE);
        }
        else {
            mIsPrivate.setEnabled(false);
            mIsPrivate.setChecked(true);
            mPublicSubmissions.setVisibility(View.GONE);
            mPublicSubmissions.setChecked(false);
        }
    }
    @Override
    public void showHomeworkSaved() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        Toast.makeText(getActivity(), R.string.homework_addHomework_saved, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void reloadHomeworkList() {
        getActivity().startService(HomeworkSyncService.getStartIntent(getActivity()));
    }
    @Override
    public void showCourseLoadingError() {
        mCourse.setEnabled(false);
        Toast.makeText(getActivity(), R.string.homework_addHomework_error_loadingCourses, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showSaveError() {
        Toast.makeText(getActivity(), R.string.homework_addHomework_error_saving, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showNameEmpty() {
        Toast.makeText(getActivity(), R.string.homework_addHomework_error_titleEmpty, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showInvalidDates() {
        Toast.makeText(getActivity(), R.string.homework_addHomework_error_datesInvalid, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void goToSignIn() {
    }
}
