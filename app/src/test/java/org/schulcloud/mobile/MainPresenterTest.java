package org.schulcloud.mobile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.test.common.TestDataFactory;
import org.schulcloud.mobile.ui.main.MainMvpView;
import org.schulcloud.mobile.ui.main.MainPresenter;
import org.schulcloud.mobile.util.RxSchedulersOverrideRule;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock
    MainMvpView mMockMainMvpView;
    @Mock
    DataManager mMockDataManager;
    private MainPresenter mMainPresenter;

    @Before
    public void setUp() {
        mMainPresenter = new MainPresenter(mMockDataManager);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {
        mMainPresenter.detachView();
    }

    @Test
    public void loadUsersReturnsUsers() {
        List<User> users = TestDataFactory.makeListUsers(10);
        doReturn(Observable.just(users))
                .when(mMockDataManager)
                .getUsers();

        mMainPresenter.loadUsers();
        verify(mMockMainMvpView).showUsers(users);
        verify(mMockMainMvpView, never()).showUsersEmpty();
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadUsersReturnsEmptyList() {
        doReturn(Observable.just(Collections.emptyList()))
                .when(mMockDataManager)
                .getUsers();

        mMainPresenter.loadUsers();
        verify(mMockMainMvpView).showUsersEmpty();
        verify(mMockMainMvpView, never()).showUsers(anyListOf(User.class));
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadUsersFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockDataManager)
                .getUsers();

        mMainPresenter.loadUsers();
        verify(mMockMainMvpView).showError();
        verify(mMockMainMvpView, never()).showUsersEmpty();
        verify(mMockMainMvpView, never()).showUsers(anyListOf(User.class));
    }
}
