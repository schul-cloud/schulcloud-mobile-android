package org.schulcloud.mobile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.test.common.TestDataFactory;
import org.schulcloud.mobile.ui.homework.HomeworkMvpView;
import org.schulcloud.mobile.ui.homework.HomeworkPresenter;
import org.schulcloud.mobile.util.RxSchedulersOverrideRule;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HomeworkPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock
    HomeworkMvpView mMockHomeworkMvpView;
    @Mock
    HomeworkDataManager mMockDataManager;
    private HomeworkPresenter mHomeworkPresenter;

    @Before
    public void setUp() {
        mHomeworkPresenter = new HomeworkPresenter(mMockDataManager);
        mHomeworkPresenter.attachView(mMockHomeworkMvpView);
    }

    @After
    public void tearDown() {
        mHomeworkPresenter.detachView();
    }

    @Test
    public void loadUsersReturnsUsers() {
        List<Homework> homeworks = TestDataFactory.makeListHomework(10);
        doReturn(Observable.just(homeworks))
                .when(mMockDataManager)
                .getHomework();

        mHomeworkPresenter.loadHomework();
        verify(mMockHomeworkMvpView).showHomework(homeworks);
        verify(mMockHomeworkMvpView, never()).showHomeworkEmpty();
        verify(mMockHomeworkMvpView, never()).showError();
    }

    @Test
    public void loadUsersReturnsEmptyList() {
        doReturn(Observable.just(Collections.emptyList()))
                .when(mMockDataManager)
                .getHomework();

        mHomeworkPresenter.loadHomework();
        verify(mMockHomeworkMvpView).showHomeworkEmpty();
        verify(mMockHomeworkMvpView, never()).showHomework(anyListOf(Homework.class));
        verify(mMockHomeworkMvpView, never()).showError();
    }

    @Test
    public void loadUsersFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockDataManager)
                .getHomework();

        mHomeworkPresenter.loadHomework();
        verify(mMockHomeworkMvpView).showError();
        verify(mMockHomeworkMvpView, never()).showHomeworkEmpty();
        verify(mMockHomeworkMvpView, never()).showHomework(anyListOf(Homework.class));
    }

}