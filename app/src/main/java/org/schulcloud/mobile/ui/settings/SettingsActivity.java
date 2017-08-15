package org.schulcloud.mobile.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.PermissionsUtil;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    public static final Integer CALENDAR_PERMISSION_CALLBACK_ID = 42;
    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.EventActivity.EXTRA_TRIGGER_SYNC_FLAG";
    @Inject
    SettingsPresenter mSettingsPresenter;

    @Inject
    PreferencesHelper mPreferencesHelper;

    @Inject
    DevicesAdapter mDevicesAdapter;

    @BindView(R.id.switch_calendar)
    SwitchCompat switch_calendar;

    @BindView(R.id.btn_create_device)
    BootstrapButton btn_create_device;

    @BindView(R.id.devices_recycler_view)
    RecyclerView devices_recycler_view;

    @BindView(R.id.name_local_calendar)
    TextView name_local_calendar;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawer.addView(contentView, 0);
        getSupportActionBar().setTitle(R.string.settings_title);
        ButterKnife.bind(this);


        devices_recycler_view.setAdapter(mDevicesAdapter);
        devices_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mSettingsPresenter.attachView(this);
        mSettingsPresenter.checkSignedIn(this);

        if (mPreferencesHelper.getCalendarSyncEnabled()) mSettingsPresenter.loadEvents(false);
        mSettingsPresenter.loadDevices();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(EventSyncService.getStartIntent(this));
            startService(DeviceSyncService.getStartIntent(this));
        }

        btn_create_device.setOnClickListener(view -> mSettingsPresenter.registerDevice());

        initializeCalendarSwitch(
                mPreferencesHelper.getCalendarSyncEnabled(),
                mPreferencesHelper.getCalendarSyncName());

        switch_calendar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) mSettingsPresenter.loadEvents(true);
            mPreferencesHelper.saveCalendarSyncEnabled(isChecked);
            initializeCalendarSwitch(isChecked, mPreferencesHelper.getCalendarSyncName());
        });
    }

    private void initializeCalendarSwitch(Boolean isChecked, String calendarName) {
        switch_calendar.setChecked(isChecked);
        name_local_calendar.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        name_local_calendar.setText(calendarName.equals("null") ? "" : calendarName);
    }

    @Override
    protected void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/


    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void showEventsEmpty() {
        //Toast.makeText(this, R.string.empty_events, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDevices(List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void reload() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        finish();
    }

    @Override
    public void connectToCalendar(List<Event> events, Boolean promptForCalendar) {
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
                    R.string.settings_calendar_choose)
                    .setItems(calendarValues, (dialogInterface, i) -> chosenValueIndex[0] = i) // update choice
                    .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) -> { // handle choice
                        if (chosenValueIndex[0] != null && chosenValueIndex[0] > 0) {

                            String calendarName = calendarValues[chosenValueIndex[0]].toString();
                            Log.i("[CALENDAR CHOSEN]: ", calendarName);
                            mPreferencesHelper.saveCalendarSyncName(calendarName);

                            // send all events to calendar
                            mSettingsPresenter.writeEventsToLocalCalendar(
                                    calendarName,
                                    events,
                                    calendarContentUtil,
                                    promptForCalendar);
                        }
                    })
                    .show();
        } else {
            mSettingsPresenter.writeEventsToLocalCalendar(
                    mPreferencesHelper.getCalendarSyncName(),
                    events,
                    calendarContentUtil,
                    promptForCalendar);
        }

    }

    @Override
    public void showSyncToCalendarSuccessful() {
        DialogFactory.createSuperToast(
                this,
                getResources().getString(R.string.settings_calendar_sync_successful),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .show();
    }
}
