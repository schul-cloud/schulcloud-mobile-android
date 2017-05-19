package org.schulcloud.mobile.ui.main;

import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface MainMvpView extends MvpView {

    void showUsers(List<User> users);

    void showUsersEmpty();

    void showError();

}
