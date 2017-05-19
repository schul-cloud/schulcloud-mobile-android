package org.schulcloud.mobile.ui.files;

import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;


public interface FileMvpView extends MvpView {
    void showFiles(List<File> files);

    void showDirectories(List<Directory> directories);

    void showError();
}
