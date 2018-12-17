package org.schulcloud.mobile.controllers.file

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.combinePath
import org.schulcloud.mobile.utils.getPathParts
import org.schulcloud.mobile.utils.limit
import org.schulcloud.mobile.views.CompatTextView
import kotlin.properties.Delegates

open class BreadcrumbsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.breadcrumbsViewStyle
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = BreadcrumbsView::class.java.simpleName
    }

    var onPathSelected: ((String) -> Unit)? = null

    var textColor: Int by Delegates.observable(Color.WHITE) { _, _, new ->
        for (child in children)
            (child as? TextView)?.setTextColor(new)
        dividerDrawable.setColorFilter(new, PorterDuff.Mode.SRC_ATOP)
    }

    private var textSize: Float = 0f

    init {
        dividerDrawable = ResourcesCompat.getDrawable(context.resources,
                R.drawable.ic_chevron_right_black_24dp, context.theme)
        showDividers = SHOW_DIVIDER_MIDDLE

        context.withStyledAttributes(attrs, R.styleable.BreadcrumbsView,
                defStyleAttr, R.style.Widget_App_BreadcrumbsView) {
            textSize = getDimensionOrThrow(R.styleable.BreadcrumbsView_android_textSize)
        }
    }

    fun setPath(path: String?, course: Course? = null) {
        removeAllViews()
        if (path == null)
            return

        val parts = path.getPathParts()

        val title = when (parts.first()) {
            FileRepository.CONTEXT_MY_API -> context.getString(R.string.file_directory_my)
            FileRepository.CONTEXT_COURSES ->
                course?.name ?: context.getString(R.string.file_directory_course_unknown)
            else -> context.getString(R.string.file_directory_unknown)
        }
        addPartView(parts.limit(2).combinePath(), title)

        for (i in 2 until parts.size)
            addPartView(parts.limit(i + 1).combinePath(), parts[i])
    }

    private fun addPartView(path: String, title: String) {
        addView(CompatTextView(context).also {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            it.text = title
            it.setTextColor(textColor)
            it.setOnClickListener { onPathSelected?.invoke(path) }

            with(TypedValue()) {
                context.theme.resolveAttribute(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            android.R.attr.selectableItemBackgroundBorderless
                        else android.R.attr.selectableItemBackground,
                        this, true)
                it.setBackgroundResource(resourceId)
            }
        })
    }
}
