package org.schulcloud.mobile.ui.files;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Directory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DirectoriesAdapter
        extends RecyclerView.Adapter<DirectoriesAdapter.DirectoriesViewHolder> {
    @Inject
    FilePresenter mFilesPresenter;
    @Inject
    DataManager dataManager;
    private List<Directory> mDirectories;

    @Inject
    public DirectoriesAdapter() {
        mDirectories = new ArrayList<>();
    }

    public void setDirectories(List<Directory> directories) {
        mDirectories = directories;
    }

    @Override
    public DirectoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_directory, parent, false);
        return new DirectoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DirectoriesViewHolder holder, int position) {
        Directory directory = mDirectories.get(position);
        holder.nameTextView.setText(directory.name);

        String path = directory.path + directory.name;

        // remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // has to be final for lambda expressions
        String finalPath = path;

        holder.cardView.setOnClickListener(v -> mFilesPresenter.goIntoDirectory(finalPath));
        // TODO: refactor it when we also support course/class files
        holder.deleteDirectory.setOnClickListener(
                v -> mFilesPresenter.startDirectoryDeleting(finalPath, directory.name));
    }

    @Override
    public int getItemCount() {
        return mDirectories.size();
    }

    class DirectoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.directory_delete_icon)
        AwesomeTextView deleteDirectory;

        public DirectoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
