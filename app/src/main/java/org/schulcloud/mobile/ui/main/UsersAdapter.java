package org.schulcloud.mobile.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> mUsers;

    @Inject
    public UsersAdapter() {
        mUsers = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ribot, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.nameTextView.setText(String.format("%s %s",
                user.getLastName(), user.getFirstName()));
        holder.emailTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.text_email)
        TextView emailTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
