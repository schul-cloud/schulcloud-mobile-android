package org.schulcloud.mobile.ui.files;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    @Inject
    FilePresenter mFilesPresenter;
    private List<File> mFiles;
    private DataManager mDataManager;

    @Inject
    public FilesAdapter(DataManager dataManager) {
        mFiles = new ArrayList<>();
        mDataManager = dataManager;
    }

    public void setFiles(List<File> files) {
        mFiles = files;
    }

    @Override
    public FilesAdapter.FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FilesAdapter.FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilesAdapter.FilesViewHolder holder, int position) {
        File file = mFiles.get(position);
        holder.nameTextView.setText(file.name);
        holder.cardView.setOnClickListener(v -> {
            File file1 = mFiles.get(position);
            mFilesPresenter.loadFileFromServer(file1);
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    class FilesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.card_view)
        CardView cardView;

        public FilesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
