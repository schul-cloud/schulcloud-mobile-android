package org.schulcloud.mobile.ui.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;

public class NavItemAdapter extends BaseAdapter {

    private Activity activity;
    private String[] titles;
    private String[] icIds;

    public NavItemAdapter(Activity context, String[] titles, String[] icIds) {
        this.activity = context;
        this.titles = titles;
        this.icIds = icIds;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout frameLayout =
                (FrameLayout) activity.getLayoutInflater().inflate(R.layout.drawer_list_item, parent, false);
        ((TextView) frameLayout.findViewById(R.id.mdTextPrimary)).setText(titles[position]);
        ((AwesomeTextView) frameLayout.findViewById(R.id.mdImage)).setFontAwesomeIcon(icIds[position]);
        return frameLayout;
    }
}
