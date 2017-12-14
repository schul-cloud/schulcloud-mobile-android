package org.schulcloud.mobile.ui.courses;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> mCourses;

    @Inject
    CoursePresenter mCoursePresenter;

    @Inject
    public CourseAdapter() {
        mCourses = new ArrayList<>();
    }

    public void setCourses(List<Course> course) {
        mCourses = course;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = mCourses.get(position);

        holder.nameTextView.setText(course.name);
        holder.descriptionTextView.setText(course.description);

        holder.colorView.setBackgroundColor(Color.parseColor(course.color));

        holder.cardView.setOnClickListener(v -> mCoursePresenter.showCourseDetail(course._id));
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.text_description)
        TextView descriptionTextView;
        @BindView(R.id.view_hex_color)
        AwesomeTextView colorView;
        @BindView(R.id.card_view)
        CardView cardView;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
