package org.schulcloud.mobile.ui.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Device;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {
    @Inject
    SettingsPresenter mSettingsPresenter;
    private List<Device> mDevices;
    private UserDataManager mUserDataManager;

    @Inject
    public DevicesAdapter(UserDataManager userDataManager) {
        mDevices = new ArrayList<>();
        mUserDataManager = userDataManager;
    }

    public void setDevices(List<Device> devices) {
        mDevices = devices;
    }

    @Override
    public DevicesAdapter.DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new DevicesAdapter.DevicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DevicesAdapter.DevicesViewHolder holder, int position) {
        Device device = mDevices.get(position);
        holder.nameTextView.setText(device.name);
        holder.tokenText.setText(device.token);

        if (mUserDataManager.getPreferencesHelper().getMessagingToken().equals(holder.tokenText.getText().toString())) {
            holder.awesomeTextView.setFontAwesomeIcon("fa_trash");
            holder.awesomeTextView.setOnClickListener(v -> mSettingsPresenter.deleteDevice(device));
        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class DevicesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_item_icon)
        AwesomeTextView awesomeTextView;
        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.token)
        TextView tokenText;

        public DevicesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
