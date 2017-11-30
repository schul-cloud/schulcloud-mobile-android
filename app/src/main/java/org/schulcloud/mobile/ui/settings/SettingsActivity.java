package org.schulcloud.mobile.ui.settings;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.PermissionsUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    public static final Integer CALENDAR_PERMISSION_CALLBACK_ID = 42;
    private static final String EXTRA_TRIGGER_SYNC = "org.schulcloud.mobile.ui.settings.SettingsActivity.EXTRA_TRIGGER_SYNC";

    @Inject
    SettingsPresenter mSettingsPresenter;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    DevicesAdapter mDevicesAdapter;

    @BindView(R.id.switch_calendar)
    Switch switch_calendar;
    @BindView(R.id.btn_create_device)
    BootstrapButton btn_create_device;
    @BindView(R.id.devices_recycler_view)
    RecyclerView devices_recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.settings_title);

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC, true)) {
            startService(EventSyncService.getStartIntent(this));
            startService(DeviceSyncService.getStartIntent(this));
        }

        // Calender
        updateCalendarSwitch(
                mPreferencesHelper.getCalendarSyncEnabled(),
                mPreferencesHelper.getCalendarSyncName());
        switch_calendar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mSettingsPresenter.loadEvents(true);
            mPreferencesHelper.saveCalendarSyncEnabled(isChecked);
            updateCalendarSwitch(isChecked, mPreferencesHelper.getCalendarSyncName());
        });

        // Notifications
        btn_create_device.setOnClickListener(view -> mSettingsPresenter.registerDevice());

        devices_recycler_view.setAdapter(mDevicesAdapter);
        devices_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        // About
        findViewById(R.id.settings_about_contributors).setOnLongClickListener(v -> {
            ClipboardManager clipboardManager =
                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(
                    getString(R.string.settings_about_contributors_clipBoard_title),
                    TextUtils.join(getString(R.string.general_list_separator),
                            mSettingsPresenter.getContributors()));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, R.string.settings_about_contributors_clipBoard_notification,
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        findViewById(R.id.about_github).setOnClickListener(v ->
                mSettingsPresenter.showGitHub(getResources()));
        findViewById(R.id.about_contact).setOnClickListener(v ->
                mSettingsPresenter.contact(
                        getString(R.string.settings_about_contact_mail_to),
                        getString(R.string.settings_about_contact_mail_subject)));
        findViewById(R.id.about_imprint).setOnClickListener(v ->
                mSettingsPresenter.showImprint(getResources()));

        // Presenter
        mSettingsPresenter.attachView(this);
        if (mPreferencesHelper.getCalendarSyncEnabled())
            mSettingsPresenter.loadEvents(false);
        mSettingsPresenter.loadDevices();
        mSettingsPresenter.loadContributors(getResources());
    }
    private void updateCalendarSwitch(boolean isChecked, String calendarName) {
        switch_calendar.setChecked(isChecked);
        if (isChecked && calendarName != null)
            switch_calendar.setText(
                    getString(R.string.settings_calendar_sync_calenderChosen, calendarName));
        else
            switch_calendar.setText(R.string.settings_calendar_sync);
    }

    @Override
    protected void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/
    // Calender
    @Override
    public void showEventsEmpty() {
        //Toast.makeText(this, R.string.empty_events, Toast.LENGTH_LONG).show();
    }

    @Override
    public void connectToCalendar(@NonNull List<Event> events, boolean promptForCalendar) {
        // grant calendar permission, powered sdk version 23
        PermissionsUtil.checkPermissions(
                CALENDAR_PERMISSION_CALLBACK_ID,
                this,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR);
        CalendarContentUtil calendarContentUtil = new CalendarContentUtil(this);

        Set<String> calendars = calendarContentUtil.getCalendars();
        CharSequence[] calendarValues = calendars.toArray(new CharSequence[calendars.size()]);

        // saves selected calendar index on dialog prompting
        final Integer[] chosenValueIndex = new Integer[1];

        if (promptForCalendar) {
            DialogFactory.createSingleSelectDialog(
                    this,
                    calendarValues,
                    Arrays.asList(calendarValues).indexOf(mPreferencesHelper.getCalendarSyncName()),
                    (dialogInterface, i) -> chosenValueIndex[0] = i,
                    R.string.settings_calendar_choose)
                    .setPositiveButton(R.string.dialog_action_ok,
                            (dialogInterface, i) -> { // handle choice
                                String calendarName = calendarValues[chosenValueIndex[0]]
                                        .toString();
                                Log.i("[CALENDAR CHOSEN]: ", calendarName);
                                mPreferencesHelper.saveCalendarSyncName(calendarName);
                                updateCalendarSwitch(true, calendarName);

                                // send all events to calendar
                                mSettingsPresenter.writeEventsToLocalCalendar(
                                        calendarName,
                                        events,
                                        calendarContentUtil,
                                        promptForCalendar);
                            })
                    .show();
        } else
            mSettingsPresenter.writeEventsToLocalCalendar(
                    mPreferencesHelper.getCalendarSyncName(),
                    events,
                    calendarContentUtil,
                    promptForCalendar);
    }

    @Override
    public void showSyncToCalendarSuccessful() {
        DialogFactory.createSuperToast(
                this,
                getResources().getString(R.string.settings_calendar_sync_successful),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .show();
    }

    // Notifications
    @Override
    public void showDevices(@NonNull List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void reloadDevices() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        finish();
    }

    // About
    @Override
    public void showContributors(@NonNull String[] contributors) {
        LinearLayout contributorsWrapper = ButterKnife.findById(this,
                R.id.settings_about_contributors);

        int[] attrs = new int[]{android.R.attr.textColor};
        TypedArray a = obtainStyledAttributes(R.style.Preference_Description, attrs);
        int textColor = a.getColor(0, 0);
        a.recycle();

        for (String contributor : contributors) {
            TextView textView = new TextView(this, null, R.style.Preference_Description);
            textView.setPadding(
                    getResources().getDimensionPixelSize(R.dimen.bootstrap_alert_paddings),
                    0, 0, 0);
            textView.setTextColor(textColor);
            textView.setText(contributor);
            contributorsWrapper.addView(textView);
        }
    }
    @Override
    public void showExternalContent(@NonNull Uri data) {
        startActivity(new Intent(Intent.ACTION_VIEW, data));
    }
}
