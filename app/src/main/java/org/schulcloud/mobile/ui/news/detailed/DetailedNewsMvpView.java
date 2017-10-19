package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.MvpView;

/**
 * Created by araknor on 13.10.17.
 */

public interface DetailedNewsMvpView extends MvpView {

    void showNews(News news);

    void showError();
}
