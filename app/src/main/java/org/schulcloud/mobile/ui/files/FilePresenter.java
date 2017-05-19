package org.schulcloud.mobile.ui.files;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class FilePresenter extends BasePresenter<FileMvpView> {
    private final DataManager mDataManager;
    private Subscription fileSubscription;
    private Subscription directorySubscription;

    @Inject
    public FilePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(FileMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (fileSubscription != null) fileSubscription.unsubscribe();
        if (directorySubscription != null) directorySubscription.unsubscribe();
    }

    public void loadFiles() {
        checkViewAttached();
        RxUtil.unsubscribe(fileSubscription);
        fileSubscription = mDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        files -> {
                            getMvpView().showFiles(files);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            getMvpView().showError();
                        },
                        () -> {});
    }

    public void loadDirectories() {
        checkViewAttached();
        RxUtil.unsubscribe(directorySubscription);
        directorySubscription = mDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        directories -> {
                            getMvpView().showDirectories(directories);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            getMvpView().showError();
                        },
                        () -> {});
    }

    public void checkSignedIn() {
        super.isAlreadySignedIn(mDataManager);
    }
}

