package org.schulcloud.mobile.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.homework.HomeworkCourse
import org.schulcloud.mobile.utils.fitColorToTheme
import org.schulcloud.mobile.utils.getTextColorForBackground
import org.schulcloud.mobile.utils.toColor
import org.schulcloud.mobile.utils.visibilityBool
import kotlin.properties.Delegates

open class CourseChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.courseChipStyle
) : Chip(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = CourseChip::class.java.simpleName
    }

    var course by Delegates.observable<Course?>(null) { _, _, new ->
        initFrom(new?.name, new?.color)
    }
    var homeworkCourse by Delegates.observable<HomeworkCourse?>(null) { _, _, new ->
        initFrom(new?.name, new?.color)
    }

    private fun initFrom(name: String?, rawColor: String?) {
        if (name == null || name.isNullOrBlank()) {
            visibilityBool = false
            return
        }

        val color = context.fitColorToTheme(rawColor.toColor())
        val textColors = ColorStateList.valueOf(context.getTextColorForBackground(color))
        text = name
        setTextColor(textColors)
        chipIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_brand_course_black, context.theme)
        chipIconTint = textColors
        chipBackgroundColor = ColorStateList.valueOf(color)

        visibilityBool = true
    }
}
