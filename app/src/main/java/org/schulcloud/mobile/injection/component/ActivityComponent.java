package org.schulcloud.mobile.injection.component;

import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity mainActivity);

    void inject(SignInActivity signInActivity);

    void inject(FileActivity fileActivity);

    void inject(MainActivity mainActivity);

    void inject(SettingsActivity settingsActivity);
}
