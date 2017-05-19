package org.schulcloud.mobile.ui.files;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DirectoriesAdapter extends RecyclerView.Adapter<DirectoriesAdapter.DirectoriesViewHolder> {
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
    }

    @Override
    public int getItemCount() {
        return mDirectories.size();
    }

    class DirectoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name) TextView nameTextView;


        public DirectoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
