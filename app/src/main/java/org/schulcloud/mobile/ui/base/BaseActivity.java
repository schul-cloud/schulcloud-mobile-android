package org.schulcloud.mobile.ui.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.util.LongSparseArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.font.FontAwesome;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.courses.CourseActivity;
import org.schulcloud.mobile.ui.dashboard.DashboardActivity;
import org.schulcloud.mobile.ui.feedback.FeedbackDialog;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.homework.HomeworkActivity;
import org.schulcloud.mobile.ui.news.NewsActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.NetworkUtil;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> sComponentsMap = new LongSparseArray<>();
    protected DrawerLayout mDrawer;
    protected ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    public Toolbar mToolbar;
    // todo: maybe move this to DataManager
    private PreferencesHelper mPreferencesHelper;
    @Inject
    DataManager mDataManager;
    // Curently just nonsense Data and Logos, change here for the actual list
    private String[] layers;
    private String[] resources = {
            FontAwesome.FA_TH_LARGE,
            FontAwesome.FA_FILE,
            FontAwesome.FA_GRADUATION_CAP,
            FontAwesome.FA_TASKS,
            FontAwesome.FA_COGS,
            FontAwesome.FA_NEWSPAPER_O,
            FontAwesome.FA_SIGN_OUT
    };
    private ActivityComponent mActivityComponent;
    private long mActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drawer_layout);
        TypefaceProvider.registerDefaultIconSets();
        mPreferencesHelper = new PreferencesHelper(this.getBaseContext());

        // Setup Actionbar / Toolbar
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (!NetworkUtil.isNetworkConnected(this.getBaseContext())) {
            TextView offline = (TextView) findViewById(R.id.offlineBadge);
            offline.setVisibility(View.VISIBLE);
        }

        layers = new String[]{
                getString(R.string.dashboard_title),
                getString(R.string.files_title),
                getString(R.string.courses_title),
                getString(R.string.homework_title),
                getString(R.string.settings_title),
                getString(R.string.news_title),
                getString(R.string.logout_title)
        };
        // Idea found on StackOverflow
        // http://stackoverflow.com/questions/21405958/how-to-display-navigation-drawer-in-all-activities
        // Init and data filling of the navigation drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View header = getLayoutInflater().inflate(R.layout.drawer_user, null);
        mDrawerList.addHeaderView(header);
        mDrawerList.setAdapter(new NavItemAdapter(this, layers, resources));
        mDrawerList.setOnItemClickListener((arg0, arg1, pos, arg3) -> openActivityForPos(pos));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.base_drawer_open, R.string.base_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.setDrawerListener(mDrawerToggle);

        mDrawer.post(() -> mDrawerToggle.syncState());

        TextView username = (TextView) findViewById(R.id.username);
        username.setText(mPreferencesHelper.getCurrentUsername());

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        mActivityId = savedInstanceState != null ?
            savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (null == sComponentsMap.get(mActivityId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                .applicationComponent(SchulCloudApplication.get(this).getComponent())
                .build();
            sComponentsMap.put(mActivityId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = sComponentsMap.get(mActivityId);
        }
        mActivityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
    }
    @Override
    @CallSuper
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }
    // Magic happens here for choosing according to the position in the array
    private void openActivityForPos(int pos) {
        Class c;
        switch (pos) {
            case 1: // Dashboard
                c = DashboardActivity.class;
                break;
            case 2: // files
                mPreferencesHelper.clear(PreferencesHelper.PREFERENCE_STORAGE_CONTEXT);
                c = FileActivity.class;
                break;
            case 3: // Course
                c = CourseActivity.class;
                break;
            case 4: // homework
                c = HomeworkActivity.class;
                break;
            case 5: // settings
                c = SettingsActivity.class;
                break;
            case 6: // news
                c = NewsActivity.class;
                break;
            case 7: // logout
                // clear all local user data
                mDataManager.signOut();
                c = SignInActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(this, c);
        this.startActivity(intent);
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.base_action_feedback:
                FeedbackDialog.newInstance(getClass().getSimpleName(),
                    mPreferencesHelper.getCurrentUsername())
                    .show(getSupportFragmentManager(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", mActivityId);
            sComponentsMap.remove(mActivityId);
        }
        super.onDestroy();
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }
}
