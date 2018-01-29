package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.dialogs.SimpleDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectoriesAdapter
        extends RecyclerView.Adapter<DirectoriesAdapter.DirectoryViewHolder> {

    @Inject
    FilesPresenter mFilesPresenter;

    private List<Directory> mDirectories;
    private boolean mCanDeleteDirectories = false;

    @Inject
    public DirectoriesAdapter() {
        mDirectories = new ArrayList<>();
    }

    public void setDirectories(@NonNull List<Directory> directories) {
        mDirectories = directories;
        notifyDataSetChanged();
    }
    public List<Directory> getDirectories() {
        return mDirectories;
    }
    public void setCanDeleteDirectories(boolean canDeleteDirectories) {
        mCanDeleteDirectories = canDeleteDirectories;
        notifyItemRangeChanged(0, mDirectories.size());
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.item_directory, parent, false);

        PopupMenu menu = new PopupMenu(context, view.findViewById(R.id.directory_iv_overflow));
        menu.inflate(R.menu.item_directory);
        menu.getMenu().findItem(R.id.files_directory_action_delete)
                .setVisible(mCanDeleteDirectories);

        DirectoryViewHolder holder = new DirectoryViewHolder(view, menu);
        ViewUtil.setVisibility(holder.vIv_overflow, mCanDeleteDirectories);

        return holder;
    }
    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Directory directory = mDirectories.get(position);
        PopupMenu menu = holder.nOverflow;

        holder.itemView.setOnClickListener(v -> mFilesPresenter.onDirectorySelected(directory));
        holder.vTv_name.setText(directory.name);

        holder.vIv_overflow.setOnClickListener(v -> menu.show());
        holder.itemView.setOnLongClickListener(v -> {
            menu.show();
            return true;
        });
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.files_directory_action_delete:
                    new SimpleDialogBuilder(context)
                            .title(R.string.files_directoryDelete_dialogTitle)
                            .message(context.getString(R.string.files_directoryDelete_request,
                                    directory.name))
                            .buildAsSingle()
                            .subscribe(
                                    o -> mFilesPresenter.onDirectoryDeleteSelected(directory),
                                    throwable -> {}); // Ignore cancel event
                    return true;

                default:
                    return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDirectories.size();
    }

    class DirectoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.directory_tv_name)
        TextView vTv_name;

        @BindView(R.id.directory_iv_overflow)
        ImageView vIv_overflow;

        PopupMenu nOverflow;

        public DirectoryViewHolder(View itemView, @NonNull PopupMenu menuOverflow) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            nOverflow = menuOverflow;
        }
    }
}
