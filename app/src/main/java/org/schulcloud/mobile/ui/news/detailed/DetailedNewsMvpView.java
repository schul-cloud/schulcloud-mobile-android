package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedNewsMvpView extends MvpView {

    void showNews(News news);

}
