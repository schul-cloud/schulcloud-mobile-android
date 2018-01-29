package org.schulcloud.mobile.ui.files.overview;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.ui.layouts.ListItemLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseDirectoryAdapter
        extends RecyclerView.Adapter<CourseDirectoryAdapter.CourseViewHolder> {

    private List<Course> mCourses;

    @Inject
    FileOverviewPresenter mFileOverviewPresenter;

    @Inject
    public CourseDirectoryAdapter() {
        mCourses = new ArrayList<>();
    }

    public void setCourses(@NonNull List<Course> course) {
        mCourses = course;
        notifyDataSetChanged();
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_directory_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = mCourses.get(position);

        holder.vLil_wrapper.setOnClickListener(v ->
                mFileOverviewPresenter.showCourseDirectory(course._id));

        holder.vTv_name.setText(course.name);
        holder.vV_color.setBackgroundColor(Color.parseColor(course.color));
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.directoryCourse_lil_wrapper)
        ListItemLayout vLil_wrapper;
        @BindView(R.id.directoryCourse_tv_name)
        TextView vTv_name;
        @BindView(R.id.directoryCourse_v_color)
        View vV_color;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
