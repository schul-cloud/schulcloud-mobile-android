package org.schulcloud.mobile.ui.homework.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.DialogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddHomeworkFragment extends MainFragment implements AddHomeworkMvpView {

    @Inject
    AddHomeworkPresenter mAddHomeworkPresenter;

    private DateFormat mDateFormat;
    private Calendar mAvailableDateCalendar;
    private Calendar mDueDateCalendar;

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.course)
    AppCompatSpinner course;
    @BindView(R.id.isPrivate)
    CheckBox isPrivate;
    @BindView(R.id.description)
    TextInputEditText description;
    @BindView(R.id.availableDate)
    TextView availableDate;
    @BindView(R.id.dueDate)
    TextView dueDate;
    @BindView(R.id.publicSubmissions)
    CheckBox publicSubmissions;
    @BindView(R.id.add)
    BootstrapButton add;

    @NonNull
    public static AddHomeworkFragment newInstance() {
        return new AddHomeworkFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mAvailableDateCalendar = Calendar.getInstance();
        mDueDateCalendar = Calendar.getInstance();
        mDueDateCalendar.add(Calendar.DATE, 7);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_homework, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_addHomework_title);

        initDateButton(mAvailableDateCalendar, availableDate);
        initDateButton(mDueDateCalendar, dueDate);

        add.setOnClickListener(v ->
                mAddHomeworkPresenter.addHomework(
                        name.getText().toString().trim(),
                        course.getSelectedItemPosition(),
                        isPrivate.isChecked(),
                        description.getText().toString().trim(),
                        mAvailableDateCalendar,
                        mDueDateCalendar,
                        publicSubmissions.isChecked()));

        mAddHomeworkPresenter.attachView(this);
        mAddHomeworkPresenter.loadData();

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
            } catch (ParseException e) {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(adapter);
        course.setEnabled(true);
    }
    @Override
    public void setCanCreatePublic(boolean canCreatePublic) {
        if (canCreatePublic) {
            isPrivate.setEnabled(true);
            publicSubmissions.setVisibility(View.VISIBLE);
        } else {
            isPrivate.setEnabled(false);
            isPrivate.setChecked(true);
            publicSubmissions.setVisibility(View.GONE);
            publicSubmissions.setChecked(false);
        }
    }

    @Override
    public void showHomeworkSaved() {
        finish();
        DialogFactory.createGenericErrorDialog(getContext(), R.string.homework_addHomework_saved)
                .show();
    }
    @Override
    public void reloadHomeworkList() {
        getActivity().startService(HomeworkSyncService.getStartIntent(getContext()));
    }
    @Override
    public void showCourseLoadingError() {
        course.setEnabled(false);
        DialogFactory.createGenericErrorDialog(getContext(),
                R.string.homework_addHomework_error_loadingCourses).show();
    }
    @Override
    public void showSaveError() {
        DialogFactory.createGenericErrorDialog(getContext(),
                R.string.homework_addHomework_error_saving).show();
    }
    @Override
    public void showNameEmpty() {
        DialogFactory.createGenericErrorDialog(getContext(),
                R.string.homework_addHomework_error_titleEmpty).show();
    }
    @Override
    public void showInvalidDates() {
        DialogFactory.createGenericErrorDialog(getContext(),
                R.string.homework_addHomework_error_datesInvalid).show();
    }
}
