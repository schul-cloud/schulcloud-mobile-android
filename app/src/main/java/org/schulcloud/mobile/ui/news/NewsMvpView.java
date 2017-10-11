package org.schulcloud.mobile.ui.news;

import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

/**
 * Created by araknor on 10.10.17.
 */

public interface NewsMvpView extends MvpView {
    void showNews(List<News> newses);

    void showNewsEmpty();

    void showError();

    void showNewsDialog(String newsId);

}
