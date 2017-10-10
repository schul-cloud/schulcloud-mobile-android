package org.schulcloud.mobile.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.homework.HomeworkPresenter;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by araknor on 10.10.17.
 */

public class NewsActivity extends BaseActivity implements NewsMvpView {

    @Inject
    public NewsPresenter mNewsPresenter;
    //TODO: make NewsAdapter

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        activityComponent().inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_news, null, false);
        mDrawer.addView(contentView,0);
        getSupportActionBar().setTitle("News");
        ButterKnife.bind(this);

        mNewsPresenter.attachView(this);
        mNewsPresenter.checkSignIn(this);
    }

    @Override
    public void showNews(List<News> newses) {
        DialogFactory.createGenericErrorDialog(this,"Funtkion noch nicht implementiert.");
    }

    @Override
    public void showNewsEmpty() {
        DialogFactory.createGenericErrorDialog(this,"Funktion noch nicht implementiert.");
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this,"Leider konnten die News nicht geladen werden");
    }

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }
}
