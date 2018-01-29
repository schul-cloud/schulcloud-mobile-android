package org.schulcloud.mobile.ui.homework;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.font.FontAwesome;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder> {

    private List<Homework> mHomework;
    private Context mContext;

    @Inject
    HomeworkPresenter mHomeworkPresenter;

    @Inject
    public HomeworkAdapter() {
        mHomework = new ArrayList<>();
    }

    public void setHomework(@NonNull List<Homework> homework) {
        mHomework = homework;
        notifyDataSetChanged();
    }

    public void setContext(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public HomeworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_homework, parent, false);
        return new HomeworkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeworkViewHolder holder, int position) {
        Homework homework = mHomework.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date untilDate = null;
        try {
            untilDate = dateFormat.parse(homework.dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (untilDate.before(new Date())) {
            holder.dueDate
                    .setPaintFlags(holder.dueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.gray_dark));
        }

        if (homework.courseId != null && homework.courseId.name != null)
            holder.nameTextView.setText(mContext.getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            holder.nameTextView.setText(homework.name);
        holder.descriptionTextView.setText(Html.fromHtml(homework.description));

        if (homework.restricted != null && homework.restricted)
            holder.private_homework.setFontAwesomeIcon(FontAwesome.FA_LOCK);

        if (homework.courseId != null && homework.courseId.color != null)
            holder.colorView.setBackgroundColor(Color.parseColor(homework.courseId.color));

        if (untilDate != null)
            holder.dueDate.setText(dateFormatDeux.format(untilDate));

        String course = homework.courseId != null ? homework.courseId.name : "";

        holder.cardView.setOnClickListener(v ->
                mHomeworkPresenter.showHomeworkDetail(homework._id));
    }

    @Override
    public int getItemCount() {
        return mHomework.size();
    }

    class HomeworkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.text_description)
        TextView descriptionTextView;
        @BindView(R.id.view_hex_color)
        AwesomeTextView colorView;
        @BindView(R.id.private_homework)
        AwesomeTextView private_homework;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.text_dueDate)
        TextView dueDate;

        public HomeworkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
